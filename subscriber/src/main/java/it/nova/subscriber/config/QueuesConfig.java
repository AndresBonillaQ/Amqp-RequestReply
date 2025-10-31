package it.nova.subscriber.config;

import lombok.Getter;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>RabbitMQ configuration defining the core business components: the main Topic Exchange,
 * the source Queues, and the business Bindings (routing requests based on topic patterns).</p>
 * <p>It relies on DlxConfig for centralized Dead Letter configuration.</p>
 */
@Configuration
@Getter
public class QueuesConfig {

    @Value("${spring.rabbitmq.exchange}")
    private String TOPIC_EXCHANGE_NAME;

    @Value("${spring.rabbitmq.queue.urgent.name}")
    private String URGENT_QUEUE;

    @Value("${spring.rabbitmq.queue.urgent.routing-key}")
    private String URGENT_QUEUE_ROUTING_KEY;

    @Value("${spring.rabbitmq.queue.info.name}")
    private String INFO_QUEUE;

    @Value("${spring.rabbitmq.queue.info.routing-key}")
    private String INFO_QUEUE_ROUTING_KEY;

    @Autowired
    private DeadQueuesConfig deadQueuesConfig;

    // ===============================================
    // 1. Defining exchange topic
    // ===============================================

    /**
     * Defines the main Topic Exchange for business requests.
     * @return The TopicExchange bean.
     */
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE_NAME);
    }

    // ===============================================
    // 2. Defining queues
    // ===============================================


    /**
     * Defines the durable Queue for urgent requests.
     * Configured with arguments to redirect failed messages to the central DLX/DLQ.
     * @return The urgent requests Queue bean.
     */
    @Bean
    public Queue urgentQueue() {
        return QueueBuilder.durable(URGENT_QUEUE)
                .withArgument("x-dead-letter-exchange", deadQueuesConfig.getDLX_EXCHANGE_NAME())
                .build();
    }

    /**
     * Defines the durable Queue for information requests.
     * Also configured to redirect failed messages to the central DLX/DLQ using the same DLX and routing key.
     * @return The information requests Queue bean.
     */
    @Bean
    public Queue infoQueue() {
        return QueueBuilder.durable(INFO_QUEUE)
                .withArgument("x-dead-letter-exchange", deadQueuesConfig.getDLX_EXCHANGE_NAME())
                .build();
    }

    // ===============================================
    // 3. Business Bindings
    // ===============================================

    /**
     * Binds the urgent queue to the main Topic Exchange using a broad topic pattern.
     * @param urgentQueue The urgent queue instance.
     * @param topicExchange The main business Topic Exchange instance.
     * @return The Binding bean.
     */
    @Bean
    public Binding urgentBinding(Queue urgentQueue, TopicExchange topicExchange) {
        return BindingBuilder
                .bind(urgentQueue)
                .to(topicExchange)
                .with(URGENT_QUEUE_ROUTING_KEY);
    }

    /**
     * Binds the information queue to the main Topic Exchange using a specific topic pattern.
     * @param infoQueue The information queue instance.
     * @param topicExchange The main business Topic Exchange instance.
     * @return The Binding bean.
     */
    @Bean
    public Binding infoBinding(Queue infoQueue, TopicExchange topicExchange) {
        return BindingBuilder
                .bind(infoQueue)
                .to(topicExchange)
                .with(INFO_QUEUE_ROUTING_KEY);
    }
}
