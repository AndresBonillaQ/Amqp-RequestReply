package it.nova.publisher.publisher;

import it.nova.publisher.config.PublisherConfig;
import org.springframework.amqp.core.AmqpReplyTimeoutException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PublisherService {

    private static final String TRACKING_ID_HEADER = "trackingId";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private PublisherConfig publisherConfig;

    public String sendRequestAndReceivedReply(String messaggio, String topicRoutingKey) {

        final UUID trackingId = UUID.randomUUID();
        final long startTime = System.currentTimeMillis();

        Object reply = rabbitTemplate.convertSendAndReceive(
                publisherConfig.getExchangeName(),
                topicRoutingKey,
                messaggio,
                message -> {
                    message.getMessageProperties().setHeader(TRACKING_ID_HEADER, trackingId.toString());
                    return message;
                }
        );

        final long endTime = System.currentTimeMillis();
        final long duration = endTime - startTime;

        System.out.printf(
                "Publisher: Reply received: '%s'. Tracking ID: %s. Latency (end-to-end): %dms%n",
                reply,
                trackingId,
                duration
        );

        if(reply == null)
            return "Timeout";

        return (String) reply;

    }
}
