#!/bin/bash

# Scope: Create RabbitMQ, publisher and a new subscriber: this command must be used after remove-all.sh!

# --- 1. Network Creation ---
NETWORK_NAME="my-amqp-network"
echo "Creating network $NETWORK_NAME..."
# Use '|| true' to ignore the error if the network already exists
docker network create $NETWORK_NAME || true

# --- 2. Container Definition (docker create) ---

echo "Creating RabbitMQ container..."
docker create \
    --hostname my-rabbit \
    --name rabbitmq-host \
    --network $NETWORK_NAME \
    -p 5672:5672 \
    -p 15672:15672 \
    -e RABBITMQ_DEFAULT_USER=guest \
    -e RABBITMQ_DEFAULT_PASS=guest \
    rabbitmq:3-management

echo "Creating Publisher container..."
docker create \
    --name publisher \
    --network $NETWORK_NAME \
    -p 8080:8080 \
    publisher:latest

echo "Running RabbitMQ and publisher..."
docker start rabbitmq-host publisher

echo "Deploying new subscriber..."
./sub-helper/run-sub.sh