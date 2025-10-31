#!/bin/bash

# Scope: Remove all the subscriber-* and start RabbitMQ, publisher and create a new subscriber: this command must be used after creation of RabbitMQ and publisher containers

# --- 1. Remove all subscriber-* ---
echo "Removing all subscriber-* ..."
./sub-helper/remove-sub-all.sh

# --- 2. Starting RabbitMQ and publisher ---
echo "Starting RabbitMQ and publisher"
docker start rabbitmq-host publisher

# --- 3. Creating new subscriber ---
echo "Deploying new subscriber..."
./sub-helper/run-sub.sh