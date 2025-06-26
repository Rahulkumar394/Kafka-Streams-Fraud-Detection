package com.example.realtimefrauddector.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import java.io.IOException;

public class JsonSerde<T> implements Serde<T> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Class<T> targetType;

    public JsonSerde(Class<T> targetType) {
        this.targetType = targetType;
    }

    @Override
    public Serializer<T> serializer() {
        return (topic, data) -> {
            if (data == null) return null;
            try {
                return objectMapper.writeValueAsBytes(data);
            } catch (IOException e) {
                throw new SerializationException("Error serializing JSON", e);
            }
        };
    }

    @Override
    public Deserializer<T> deserializer() {
        return (topic, data) -> {
            if (data == null || data.length == 0) return null;
            try {
                return objectMapper.readValue(data, targetType);
            } catch (IOException e) {
                throw new SerializationException("Error deserializing JSON", e);
            }
        };
    }
}