import requests
import time
import json
import urllib.parse

# --- CONFIGURATION ---
RABBITMQ_HOST = "localhost"             # The hostname of your RabbitMQ container
RABBITMQ_PORT = 15672                   # The Management Plugin port (NOT the AMQP port 5672)
RABBITMQ_USER = "guest"                 # The configured RabbitMQ username
RABBITMQ_PASS = "guest"                 # The configured RabbitMQ password
VIRTUAL_HOST  = "/"                     # The virtual host name (default is "/")
QUEUE_NAME    = "subscriber.queue-info" # The name of the queue to monitor
POLLING_INTERVAL = 0.1                    # Polling interval in seconds
LOG_FILE = "scaler.log"                 # Log file name

# URL-encode the virtual host and queue name for the API endpoint
encoded_vhost = urllib.parse.quote(VIRTUAL_HOST, safe='')
encoded_queue_name = urllib.parse.quote(QUEUE_NAME)

# Construct the URL for the Management API
# API format is: /api/queues/{vhost}/{queue_name}
API_URL = f"http://{RABBITMQ_HOST}:{RABBITMQ_PORT}/api/queues/{encoded_vhost}/{encoded_queue_name}"

print(f"Monitoring queue '{QUEUE_NAME}' on {RABBITMQ_HOST}:{RABBITMQ_PORT}...")
print(f"Polling every {POLLING_INTERVAL} seconds. Log file: {LOG_FILE}")
print("Press Ctrl+C to stop.")
print("-" * 50)

while True:
    try:
        # Send GET request to the RabbitMQ Management API using Basic Authentication
        response = requests.get(
            API_URL,
            auth=(RABBITMQ_USER, RABBITMQ_PASS)
        )

        # 1. Check the HTTP response status
        response.raise_for_status() # Raises an exception for 4xx or 5xx error codes

        # 2. Extract JSON data
        queue_data = response.json()

        # 3. Extract metrics from RabbitMQ API response
        # 'messages': total = ready + unacknowledged
        # 'messages_ready': messaggi in attesa di essere consegnati (pronti per elaborazione)
        # 'messages_unacknowledged': messaggi già consegnati ma non ancora confermati (in elaborazione)
        messages_total = queue_data.get('messages', 0)
        messages_ready = queue_data.get('messages_ready', 0)
        messages_unacknowledged = queue_data.get('messages_unacknowledged', 0)
        consumers = queue_data.get('consumers', 0)  # Numero di consumer connessi

        # 4. Print only Ready messages to console
        timestamp = time.strftime("%Y-%m-%d %H:%M:%S")
        print(f"[{timestamp}] Ready: {messages_ready}, Unacknowledged: {messages_unacknowledged}")

        # 5. Save all metrics to log file
        log_entry = f"[{timestamp}] Queue '{QUEUE_NAME}':\n"
        log_entry += f"  ├─ Total messages: {messages_total} (ready + unacknowledged)\n"
        log_entry += f"  ├─ Ready: {messages_ready} (in attesa di elaborazione)\n"
        log_entry += f"  ├─ Unacknowledged: {messages_unacknowledged} (in elaborazione)\n"
        log_entry += f"  └─ Consumers: {consumers}\n"
        log_entry += "-" * 50 + "\n"
        
        with open(LOG_FILE, 'w', encoding='utf-8') as log_file:
            log_file.write(log_entry)

    except requests.exceptions.HTTPError as e:
        # Handle HTTP errors like 404 (Queue Not Found) or 401 (Authentication Failed)
        if e.response.status_code == 404:
            print(f"WARNING: Queue '{QUEUE_NAME}' does not exist on the RabbitMQ broker.")
        elif e.response.status_code == 401:
            print("AUTHENTICATION ERROR: Check RABBITMQ_USER and RABBITMQ_PASS. Status 401.")
        else:
            print(f"HTTP Error: {e}")

    except requests.exceptions.ConnectionError:
        print(f"CONNECTION ERROR: Cannot reach RabbitMQ at {RABBITMQ_HOST}:{RABBITMQ_PORT}. Is it running?")

    except Exception as e:
        print(f"An unexpected error occurred: {e}")

    # Pause for the defined interval before the next poll
    time.sleep(POLLING_INTERVAL)