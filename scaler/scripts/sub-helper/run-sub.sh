#!/bin/bash

# This script creates and starts a new subscriber container instance
# by automatically generating a unique ID (UUID) for the name.

# --- Configuration ---
NETWORK_NAME="my-amqp-network"
IMAGE_NAME="subscriber:latest"

# --- 1. Unique ID Generation ---

# Check if uuidgen is available
if ! command -v uuidgen &> /dev/null
then
    echo "ERROR: 'uuidgen' command not found."
    echo "Please install it (e.g., 'brew install coreutils' on macOS or 'sudo apt install uuid-runtime' on Linux)."
    exit 1
fi

# Generate a new UUID and take the first 8 characters for the ID
INSTANCE_ID=$(uuidgen | cut -c 1-8)
CONTAINER_NAME="subscriber-$INSTANCE_ID"

echo "   Starting new subscriber instance with auto-generated ID: $INSTANCE_ID"
echo "   Container Name: $CONTAINER_NAME"

# --- 2. Create the Container ---

echo "   Creating container '$CONTAINER_NAME'..."

# Use 'docker create' to define the container properties
docker create \
    --name "$CONTAINER_NAME" \
    --network "$NETWORK_NAME" \
    "$IMAGE_NAME"

if [ $? -ne 0 ]; then
    echo "  ERROR: Failed to create container $CONTAINER_NAME."
    exit 1
fi

# --- 3. Start the Container ---

echo "   Starting container '$CONTAINER_NAME'..."
docker start "$CONTAINER_NAME"

if [ $? -ne 0 ]; then
    echo "  ERROR: Failed to start container $CONTAINER_NAME."
    exit 1
fi

echo "  Subscriber instance $CONTAINER_NAME is running."