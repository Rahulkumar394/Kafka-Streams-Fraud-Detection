package com.example.realtimefrauddector.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public final static String TRANSACTIONS_TOPIC = "transactions.raw";
    public final static String ALERTS_TOPIC = "fraud.alerts";

    @Bean
    public NewTopic transactionsTopic() {
        return TopicBuilder.name(TRANSACTIONS_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic alertsTopic() {
        return TopicBuilder.name(ALERTS_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }
}