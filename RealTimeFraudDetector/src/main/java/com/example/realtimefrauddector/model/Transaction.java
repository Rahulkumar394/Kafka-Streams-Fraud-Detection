package com.example.realtimefrauddector.model;

public record Transaction(String transactionId, String userId, double amount, long timestamp) {}

