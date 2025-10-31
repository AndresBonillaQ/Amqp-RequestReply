package it.nova.publisher.config;

import lombok.Getter;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@Getter
public class PublisherConfig {

    public static final int MAX_ATTEMPTS = 3;
    public static final int FIXED_INTERVAL_BETWEEN_ATTEMPTS = 2000; // 2 seconds
    public static final int FIXED_TIMEOUT = 20000; // 20 seconds

    @Value("${spring.rabbitmq.exchange}")
    private String exchangeName;

    @Bean
    public RabbitTemplate rabbitTemplate(org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);

        RetryTemplate retryTemplate = RetryTemplate
                .builder()
                .maxAttempts(MAX_ATTEMPTS)
                .fixedBackoff(FIXED_INTERVAL_BETWEEN_ATTEMPTS)
                .build();

        template.setRetryTemplate(retryTemplate);
        template.setReplyTimeout(FIXED_TIMEOUT);

        return template;
    }
}
