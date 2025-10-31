package it.nova.forwarder.listener;

import it.nova.forwarder.config.PublisherConfig;
import it.nova.forwarder.publisher.PublisherService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
public class MyListener {

    @Autowired
    private PublisherService publisherService;

    @Autowired
    private PublisherConfig publisherConfig;

    @RabbitListener(queues = "${spring.rabbitmq.queue.info.name}")
    @SendTo("${spring.rabbitmq.exchange}")
    public String processInfoRequest(String request, Message message) {
        String subscriberReply = publisherService.sendRequestAndReceivedReply(
                request,
                publisherConfig.getSubscriberInfoRoutingKey(),
                message
        );
        return "UrgentForwarder(" + subscriberReply + ")";
    }

    @RabbitListener(queues = "${spring.rabbitmq.queue.urgent.name}")
    @SendTo("${spring.rabbitmq.exchange}")
    public String processUrgentRequest(String request, Message message) {
        String subscriberReply = publisherService.sendRequestAndReceivedReply(
                request,
                publisherConfig.getSubscriberUrgentRoutingKey(),
                message
        );
        return "InfoForwarder(" + subscriberReply + ")";
    }
}