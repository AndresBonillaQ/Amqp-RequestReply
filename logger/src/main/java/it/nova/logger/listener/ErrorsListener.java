package it.nova.logger.listener;

import it.nova.logger.model.FailedMessage;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ErrorsListener {

    public static final List<FailedMessage> FAILED_MESSAGES_LOG = new ArrayList<>();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @RabbitListener(queues = "${spring.rabbitmq.dlx.queue}")
    public void processFailedMessages(Message message) {

        String body = new String(message.getBody(), StandardCharsets.UTF_8);
        Map<String, Object> headers = message.getMessageProperties().getHeaders();

        String timestamp = LocalDateTime.now().format(FORMATTER);
        String receivedExchange = message.getMessageProperties().getReceivedExchange();
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        String consumerQueue = message.getMessageProperties().getConsumerQueue();

        String reason = "Unknown";
        if (headers.containsKey("x-death")) {
            List<?> xDeathList = (List<?>) headers.get("x-death");
            if (!xDeathList.isEmpty()) {
                Map<?, ?> deathRecord = (Map<?, ?>) xDeathList.get(0);
                reason = (String) deathRecord.get("reason");
            }
        }

        FailedMessage failedMessage = new FailedMessage(
                timestamp,
                body,
                headers,
                routingKey,
                receivedExchange,
                consumerQueue,
                reason
        );

        FAILED_MESSAGES_LOG.add(failedMessage);
        System.out.println("Logged failed message. Total: " + FAILED_MESSAGES_LOG.size());
    }
}