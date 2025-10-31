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

# URL-encode the virtual host and queue name for the API endpoint
encoded_vhost = urllib.parse.quote(VIRTUAL_HOST, safe='')
encoded_queue_name = urllib.parse.quote(QUEUE_NAME)

# Construct the URL for the Management API
# API format is: /api/queues/{vhost}/{queue_name}
API_URL = f"http://{RABBITMQ_HOST}:{RABBITMQ_PORT}/api/queues/{encoded_vhost}/{encoded_queue_name}"

print(f"Monitoring queue '{QUEUE_NAME}' on {RABBITMQ_HOST}:{RABBITMQ_PORT}...")
print(f"Polling every {POLLING_INTERVAL} seconds. Press Ctrl+C to stop.")
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

        # 3. Extract the key metric: 'messages'
        # 'messages' is the total number of messages (ready + unacknowledged)
        messages_in_queue = queue_data.get('messages', 'N/A')

        # 4. Print the result
        timestamp = time.strftime("%Y-%m-%d %H:%M:%S")
        print(f"[{timestamp}] Messages in queue ('{QUEUE_NAME}'): **{messages_in_queue}**")

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