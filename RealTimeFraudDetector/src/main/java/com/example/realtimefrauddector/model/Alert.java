package com.example.realtimefrauddector.model;

public record Alert(String userId, String message, long windowStart, long windowEnd) {}
