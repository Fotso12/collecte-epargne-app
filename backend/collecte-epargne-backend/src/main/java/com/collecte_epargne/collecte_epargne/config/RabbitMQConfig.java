package com.collecte_epargne.collecte_epargne.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue emailNotificationsQueue() {
        return new Queue("email.notifications", true);
    }

    @Bean
    public Queue pushNotificationsQueue() {
        return new Queue("push.notifications", true);
    }

    @Bean
    public Queue realtimeNotificationsQueue() {
        return new Queue("realtime.notifications", true);
    }

    // CRUCIAL : Ce bean dit à RabbitTemplate d'utiliser JSON au lieu de la sérialisation Java native
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}