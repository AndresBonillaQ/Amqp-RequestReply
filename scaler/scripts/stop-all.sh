#!/bin/bash

# Scope: Stop RabbitMQ, publisher subscriber-*

# --- 1. Stop RabbitMQ and publisher ---
echo "Stopping RabbitMQ and publisher"
docker stop rabbitmq-host publisher

# --- 2. Stop all subscribers ---
echo "Stopping all subscribers..."
./sub-helper/stop-sub-all.sh