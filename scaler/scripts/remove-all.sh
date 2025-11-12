#!/bin/bash

# Scope: Remove ALL the instances (RabbitMQ, Publisher and all subscriber-*)

echo "Starting remove RabbitMQ, publisher and subscriber-* ..."

# --- 1. Cleanup (Stopping and Removing Containers) ---

# 1.1 Stop and remove the main named containers (publisher, subscriber, rabbitmq-host)
echo "Stopping and removing RabbitMQ and publisher..."
# The '2>&1' redirects error messages (like 'container not found') to oblivion.
docker stop publisher prometheus rabbitmq-host rabbitmq-exporter > /dev/null 2>&1
docker rm publisher prometheus rabbitmq-host rabbitmq-exporter > /dev/null 2>&1

# Stop and Remove all the subscriber-* instances
echo "Stopping and removing subscriber-* ..."
./sub-helper/remove-sub-all.sh

# End of script