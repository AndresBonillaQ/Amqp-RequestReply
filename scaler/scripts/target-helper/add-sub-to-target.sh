#!/bin/bash

# This script appends a new target entry to the Prometheus File-based Service Discovery JSON file.

# --- 1. Variable Assignment from Positional Arguments ---
# $1: Path to the Prometheus targets JSON file
TARGETS_FILE="$1"
# $2: Container target in the format 'hostname:port'
CONTAINER_TARGET="$2"
# $3: Unique instance ID used for labeling
INSTANCE_ID="$3"

# --- 2. Validation ---
if [ -z "$TARGETS_FILE" ] || [ -z "$CONTAINER_TARGET" ] || [ -z "$INSTANCE_ID" ]; then
    echo "Error: Missing arguments."
    echo "Usage: $0 <file_targets> <container_target:port> <instance_id>"
    exit 1
fi

echo "   Updating Prometheus Service Discovery file: $TARGETS_FILE"

# --- 3. JSON Update Logic ---

# Check if the file exists; if not, create it as an empty JSON array
if [ ! -f "$TARGETS_FILE" ]; then
    echo "[]" > "$TARGETS_FILE"
    echo "   File created as an empty array."
fi

# New JSON entry content
NEW_TARGET='{ "targets": ["'$CONTAINER_TARGET'"], "labels": { "instance_id": "'$INSTANCE_ID'" } }'

# Read current file content
CURRENT_CONTENT=$(cat "$TARGETS_FILE")

# Robust insertion logic:
if [ "$CURRENT_CONTENT" == "[]" ]; then
    # If the file content is "[]", the new content is just the new entry enclosed
    UPDATED_CONTENT="[ $NEW_TARGET ]"
else
    # If there are already elements, remove the trailing ']'
    CONTENT_TRIMMED=${CURRENT_CONTENT%]}
    # Append a comma, the new entry, and the closing bracket ']'
    UPDATED_CONTENT="$CONTENT_TRIMMED, $NEW_TARGET ]"
fi

# --- 4. Write to File ---
echo "$UPDATED_CONTENT" > "$TARGETS_FILE"

echo "   Target '$CONTAINER_TARGET' successfully added."