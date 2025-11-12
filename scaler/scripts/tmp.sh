docker create \
    --name rabbitmq-exporter \
    --network my-amqp-network \
    -p 9419:9419 \
    -e RABBITMQ_USER=guest \
    -e RABBITMQ_PASSWORD=guest \
    -e RABBITMQ_URL=http://rabbitmq-host:15672 \
    -e RABBITMQ_EXPORTER_INCLUDE_QUEUES=".*" \
    kbudde/rabbitmq-exporter
