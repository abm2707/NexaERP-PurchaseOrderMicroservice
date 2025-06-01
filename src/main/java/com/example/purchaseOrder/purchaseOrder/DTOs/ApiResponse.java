package com.example.purchaseOrder.purchaseOrder.DTOs;

import org.springframework.data.domain.Page;

public class ApiResponse<T> {
    private String message;
    private T data;

    // Constructor for success with data
    public ApiResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }

    // Constructor for success with just a message (data is null)
    public ApiResponse(String message) {
        this.message = message;
        this.data = null;
    }

    // Getters
    public String getMessage() { return message; }
    public T getData() { return data; }
}


