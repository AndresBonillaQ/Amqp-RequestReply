package it.nova.forwarder.publisher;

import it.nova.forwarder.config.PublisherConfig;
import org.springframework.amqp.core.AmqpReplyTimeoutException;
import org.springframework.amqp.core.Message;
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

    public String sendRequestAndReceivedReply(String message, String topicRoutingKey, Message receivedMessage) {

        String trackingId = (String) receivedMessage.getMessageProperties().getHeaders().get(TRACKING_ID_HEADER);
        if (trackingId == null) {
            trackingId = UUID.randomUUID().toString();
        }

        final String finalTrackingId = trackingId;

        Object reply = rabbitTemplate.convertSendAndReceive(
                publisherConfig.getExchangeName(),
                topicRoutingKey,
                message,
                msg -> {
                    msg.getMessageProperties().setHeader(TRACKING_ID_HEADER, finalTrackingId);
                    return msg;
                }
        );

        if(reply == null)
            return "Timeout!";

        return (String) reply;
    }
}