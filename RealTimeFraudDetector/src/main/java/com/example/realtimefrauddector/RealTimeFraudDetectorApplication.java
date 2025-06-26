package com.example.realtimefrauddector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@SpringBootApplication
@EnableKafkaStreams
public class RealTimeFraudDetectorApplication {

	public static void main(String[] args) {
		SpringApplication.run(RealTimeFraudDetectorApplication.class, args);
	}

}
