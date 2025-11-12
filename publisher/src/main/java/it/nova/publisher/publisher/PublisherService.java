package it.nova.publisher.publisher;

import it.nova.publisher.config.PublisherConfig;
import org.springframework.amqp.core.AmqpReplyTimeoutException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;

import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.MeterRegistry;

@Service
public class PublisherService {

    private static final String TRACKING_ID_HEADER = "trackingId";

    private final MeterRegistry meterRegistry;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private PublisherConfig publisherConfig;

    public PublisherService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public String sendRequestAndReceivedReply(String messaggio, String topicRoutingKey) {

        final UUID trackingId = UUID.randomUUID();

        Timer.Sample sample = Timer.start(this.meterRegistry);

        Object reply;
        String status = null;

        try {

            reply = rabbitTemplate.convertSendAndReceive(
                    publisherConfig.getExchangeName(),
                    topicRoutingKey,
                    messaggio,
                    message -> {
                        message.getMessageProperties().setHeader(TRACKING_ID_HEADER, trackingId.toString());
                        return message;
                    }
            );

            if (reply == null) {
                status = "timeout";
            } else {
                status = "success";
            }

        } catch (AmqpReplyTimeoutException e) {
            reply = null;
            status = "timeout";
        } catch (Exception e) {
            reply = null;
            status = "error";
        } finally  {
            Timer finalTimer = Timer.builder("publisher.request_reply.latency")
                    .tag("service", "subscriber")
                    .tag("status", status)
                    .register(meterRegistry);

            sample.stop(finalTimer);
        }

        System.out.printf(
                "Publisher: Reply received: '%s'. Tracking ID: %s. Status: %s%n",
                reply,
                trackingId,
                status
        );

        if (status.equals("timeout"))
            return "Timeout";

        return (String) reply;
    }
}