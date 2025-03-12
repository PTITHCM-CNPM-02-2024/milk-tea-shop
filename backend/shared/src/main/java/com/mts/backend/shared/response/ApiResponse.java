package com.mts.backend.shared.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ApiResponse<T> {
    private T data;
    private String message;
    private boolean success;
    private int status;
    private Object metadata;
    
    public static <T> ApiResponse<T> success(T data){
        return ApiResponse.<T>builder()
                .data(data)
                .message("Success")
                .success(true)
                .status(200)
                .build();
    }
    
    public static <T> ApiResponse<T> success(T data, String message){
        return ApiResponse.<T>builder()
                .data(data)
                .message(message)
                .success(true)
                .status(200)
                .build();
    }
    
    public static <T> ApiResponse<T> success(T data, String message, Object metadata){
        return ApiResponse.<T>builder()
                .data(data)
                .message(message)
                .success(true)
                .status(200)
                .metadata(metadata)
                .build();
    }
    
    public static <T> ApiResponse<T> fail(String message){
        return ApiResponse.<T>builder()
                .message(message)
                .success(false)
                .status(400)
                .build();
    }
    
    public static <T> ApiResponse<T> fail(String message, int status){
        return ApiResponse.<T>builder()
                .message(message)
                .success(false)
                .status(status)
                .build();
    }
    
    public static <T> ApiResponse<T> fail(String message, int status, Object metadata){
        return ApiResponse.<T>builder()
                .message(message)
                .success(false)
                .status(status)
                .metadata(metadata)
                .build();
    }
    
    public static <T> ApiResponse<T> fail(String message, Object metadata){
        return ApiResponse.<T>builder()
                .message(message)
                .success(false)
                .status(400)
                .metadata(metadata)
                .build();
    }
    
}
