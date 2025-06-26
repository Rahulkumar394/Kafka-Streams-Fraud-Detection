# Kafka-Streams-Fraud-Detection
Detect suspicious transaction patterns (e.g., 3+ txns in 5 seconds) using Kafka Streams.
Kafka Streams — Fraud Detection
Project Name: RealTimeFraudDetector
Goal:
Detect suspicious transaction patterns (e.g., 3+ txns in 5 seconds) using Kafka Streams.

Tech Stack:
Java 17
Spring Boot
Kafka Streams DSL
spring-kafka-streams
TopologyTestDriver
How It Works:
Ingest from transactions.raw
Aggregate by userId over 5-sec windows
Emit alert to fraud.alerts
Rules to Follow:
Kafka-Specific:

Use time-windowed aggregations
Configure state store cleanup
Handle late arrivals via grace period
Code Quality:

Stream logic in @Bean method
Use POJO and Serde abstraction
Testing:

Unit: TopologyTestDriver for stream logic
Integration: End-to-end with time simulation
