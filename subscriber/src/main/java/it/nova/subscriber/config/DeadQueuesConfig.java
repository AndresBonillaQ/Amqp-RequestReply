package it.nova.subscriber.config;

import lombok.Getter;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>RabbitMQ configuration defining the centralized Dead Letter Exchange (DLX) and Dead Letter Queue (DLQ).</p>
 * <p>This class contains infrastructure-level resilience logic, separated from business configuration.</p>
 */
@Configuration
@Getter
public class DeadQueuesConfig {

    // --- Centralized Dead Letter Components Names ---
    @Value("${spring.rabbitmq.dlx.exchange}")
    public String DLX_EXCHANGE_NAME;

    @Value("${spring.rabbitmq.dlx.queue}")
    public String CENTRAL_DLQ_NAME;

    // ===============================================
    // 1. Central DLX and DLQ Components
    // ===============================================

    /**
     * Defines the Dead Letter Exchange (DLX).
     * This unique Topic Exchange receives all dead messages from all source queues.
     * @return The DLX TopicExchange bean.
     */
    @Bean
    public FanoutExchange deadLetterExchange() {
        return new FanoutExchange(DLX_EXCHANGE_NAME);
    }

    /**
     * Defines the Central Dead Letter Queue (DLQ).
     * This single durable queue stores all failed messages for later inspection.
     * @return The DLQ Queue bean.
     */
    @Bean
    public Queue centralDeadLetterQueue() {
        return QueueBuilder.durable(CENTRAL_DLQ_NAME).build();
    }

    /**
     * Binds the Central DLQ to the DLX.
     * Uses a fixed Routing Key to ensure all dead messages land in the DLQ.
     * @param centralDeadLetterQueue The DLQ instance.
     * @param fanoutExchange The DLX instance.
     * @return The Binding bean.
     */
    @Bean
    public Binding dlqBinding(Queue centralDeadLetterQueue, FanoutExchange fanoutExchange) {
        return BindingBuilder
                .bind(centralDeadLetterQueue)
                .to(fanoutExchange);
    }
}

