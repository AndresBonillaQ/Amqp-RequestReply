# 1.2 Stop all containers matching the pattern "subscriber-*"
echo "Stopping all containers matching the pattern 'subscriber-*'..."

# Use 'docker ps -a' to list ALL containers (running or stopped)
# Use '--filter name=subscriber-*' to find containers whose name starts with 'subscriber-'
# Use '-q' (quiet) to output only the IDs
# The whole command sequence: Find IDs -> Stop them -> Remove them
CONTAINERS_TO_REMOVE=$(docker ps -a -q --filter name=subscriber-*)

if [ ! -z "$CONTAINERS_TO_REMOVE" ]; then
    echo "    Found and Stopping IDs: $CONTAINERS_TO_REMOVE"
    # Stop the containers first (if running)
    docker stop $CONTAINERS_TO_REMOVE
else
    echo "    No 'subscriber-*' containers found."
fi