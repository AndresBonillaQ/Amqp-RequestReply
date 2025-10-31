package it.nova.subscriber.listener;

import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class MyListener {

    @RabbitListener(queues = "${spring.rabbitmq.queue.info.name}")
    @SendTo("${spring.rabbitmq.exchange}")
    public String processInfoRequest(String request, Message message) {
        log.info("Info Processing: {}", request);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "InfoSubscriber(" + request + ")";
    }

    @RabbitListener(queues = "${spring.rabbitmq.queue.urgent.name}")
    @SendTo("${spring.rabbitmq.exchange}")
    public String processUrgentRequest(String request, Message message) {
        log.info("Urgent Processing: {}", request);
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "UrgentSubscriber(" + request + ")";
    }
}