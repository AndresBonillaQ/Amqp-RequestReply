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
    rabbitmq:4.2-management-alpine

echo "Creating RabbitMQ Exporter container..."
docker create \
    --name rabbitmq-exporter \
    --network $NETWORK_NAME \
    -p 9419:9419 \
    -e RABBITMQ_USER=guest \
    -e RABBITMQ_PASSWORD=guest \
    -e RABBITMQ_URL=http://rabbitmq-host:15672 \
    -e RABBITMQ_EXPORTER_INCLUDE_QUEUES=".*" \
    -e RABBITMQ_EXPORTER_INCLUDE_EXCHANGES=".*" \
    kbudde/rabbitmq-exporter

echo "Creating Prometheus container..."
docker create \
    --name prometheus \
    --network $NETWORK_NAME \
    -p 9090:9090 \
    -v $(pwd)/prometheus.yml:/etc/prometheus/prometheus.yml \
    prom/prometheus

echo "Creating Publisher container..."
docker create \
    --name publisher \
    --network $NETWORK_NAME \
    -p 8080:8080 \
    publisher:latest

echo "Running RabbitMQ and publisher..."
docker start rabbitmq-host prometheus publisher rabbitmq-exporter

echo "Enabling rabbitmq_prometheus plugin..."
docker exec rabbitmq-host rabbitmq-plugins enable rabbitmq_prometheus || {
    echo "ERROR: Failed to enable RabbitMQ plugin." ; exit 1
}

echo "Deploying new subscriber..."
./sub-helper/run-sub.sh