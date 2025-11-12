#!/bin/bash

# Scope: Stop and remove all containers matching the pattern "subscriber-*"
# and clean the corresponding entries from the Prometheus Service Discovery file.

# --- Configuration ---
TARGETS_FILE="./targets/subscribers.json" # Path to the Prometheus targets file
REMOVE_SCRIPT="./remove_targets.sh"       # Path to the script that cleans the JSON file

echo "Removing all containers matching the pattern 'subscriber-*'..."

# Use 'docker ps -a' to list ALL containers matching the pattern
CONTAINER_NAMES=$(docker ps -a --filter name=subscriber-* --format "{{.Names}}")
CONTAINERS_TO_REMOVE=$(docker ps -a -q --filter name=subscriber-*)

if [ ! -z "$CONTAINERS_TO_REMOVE" ]; then
    echo "    Found and removing IDs: $CONTAINERS_TO_REMOVE"
    echo "    Found and removing Names: $CONTAINER_NAMES"

    # --- 1. Prepare JSON Array for Removal Script ---
    # Convert the space-separated list of names into a valid JSON array string
    # E.g., 'subscriber-a1b2 subscriber-c3d4' -> '["subscriber-a1b2", "subscriber-c3d4"]'
    JSON_NAMES=$(echo $CONTAINER_NAMES | jq --raw-input 'split(" ") | map(select(length > 0))')

    # --- 2. Stop and remove the Docker containers ---
    docker stop $CONTAINERS_TO_REMOVE
    docker rm $CONTAINERS_TO_REMOVE

    # --- 3. Call the Target Removal Script ---
    echo "   Calling target removal script: $REMOVE_SCRIPT"

    # Pass the targets file path and the JSON array of hostnames to remove
    "$REMOVE_SCRIPT" "$TARGETS_FILE" "$JSON_NAMES"

    if [ $? -ne 0 ]; then
        echo "  ERROR: Target removal script failed."
        exit 1
    fi

else
    echo "    No 'subscriber-*' containers found."
fi