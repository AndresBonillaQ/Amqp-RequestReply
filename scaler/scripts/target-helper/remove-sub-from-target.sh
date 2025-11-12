#!/bin/bash

# This script removes specified targets from the Prometheus File-based Service Discovery JSON file.

# --- 1. Variable Assignment from Positional Arguments ---
# $1: Path to the Prometheus targets JSON file
TARGETS_FILE="$1"
# $2: JSON array string of container names to remove (e.g., '["sub-a1b2", "sub-c3d4"]')
JSON_NAMES_TO_REMOVE="$2"

# --- 2. Validation ---
if [ -z "$TARGETS_FILE" ] || [ -z "$JSON_NAMES_TO_REMOVE" ]; then
    echo "Error: Missing arguments."
    echo "Usage: $0 <file_targets> <json_array_of_hostnames>"
    exit 1
fi

# Check if jq is available
if ! command -v jq &> /dev/null
then
    echo "ERROR: 'jq' command not found. Please install it."
    exit 1
fi

echo "   Updating Prometheus Service Discovery file: $TARGETS_FILE"

# --- 3. JSON Removal Logic ---

if [ -f "$TARGETS_FILE" ]; then

    # Use 'jq' to filter the JSON array: keep only objects whose hostname
    # (the first element of the 'targets' array) is NOT in the list of names to remove.
    UPDATED_CONTENT=$(cat "$TARGETS_FILE" | jq --argjson names "$JSON_NAMES_TO_REMOVE" '
        map(select(
            .targets[0] | split(":")[0] as $host
            | $names | index($host) | not
        ))
    ')

    # Overwrite the file with the updated content
    echo "$UPDATED_CONTENT" > "$TARGETS_FILE"
    echo "   Targets successfully removed from $TARGETS_FILE."
else
    echo "   File $TARGETS_FILE not found. No targets removed."
fi