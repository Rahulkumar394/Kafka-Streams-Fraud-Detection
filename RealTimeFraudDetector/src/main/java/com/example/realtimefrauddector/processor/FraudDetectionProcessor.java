package com.example.realtimefrauddector.processor;

import com.example.realtimefrauddector.config.KafkaTopicConfig;
import com.example.realtimefrauddector.model.Alert;
import com.example.realtimefrauddector.model.Transaction;
import com.example.realtimefrauddector.serde.JsonSerde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import java.time.Duration;

@Component
public class FraudDetectionProcessor {

    private static final int FRAUD_THRESHOLD = 3;

    @Bean // Rule: Stream logic in @Bean method
    public KStream<String, Transaction> processFraud(StreamsBuilder streamsBuilder) {

        // Rule: Use POJO and Serde abstraction
        JsonSerde<Transaction> transactionSerde = new JsonSerde<>(Transaction.class);
        JsonSerde<Alert> alertSerde = new JsonSerde<>(Alert.class);

        KStream<String, Transaction> transactionStream = streamsBuilder
                .stream(KafkaTopicConfig.TRANSACTIONS_TOPIC, Consumed.with(Serdes.String(), transactionSerde));

        transactionStream
                .groupBy((key, transaction) -> transaction.userId(), Grouped.with(Serdes.String(), transactionSerde))
                // Rule: Use time-windowed aggregations
                .windowedBy(TimeWindows.of(Duration.ofSeconds(5))
                        // Rule: Handle late arrivals via grace period
                        .grace(Duration.ofSeconds(1)))
                // Count transactions per user in each window
                // Rule: Configure state store cleanup
                // Materialized.as() creates a state store. For windowed stores,
                // Kafka Streams automatically handles cleanup based on window retention.
                .count(Materialized.as("fraud-counts-store"))
                // Filter windows where count is 3 or more
                .filter((windowedUserId, count) -> count >= FRAUD_THRESHOLD)
                // Convert back to a KStream for output
                .toStream()
                // Create an Alert object
                .map((windowedUserId, count) -> {
                    String userId = windowedUserId.key();
                    String message = String.format("Suspicious activity for user %s: %d txns in 5 seconds.", userId, count);
                    long windowStart = windowedUserId.window().start();
                    long windowEnd = windowedUserId.window().end();
                    return new KeyValue<>(userId, new Alert(userId, message, windowStart, windowEnd));
                })
                // Send the alert to the 'fraud.alerts' topic
                .to(KafkaTopicConfig.ALERTS_TOPIC, Produced.with(Serdes.String(), alertSerde));

        return transactionStream;
    }
}