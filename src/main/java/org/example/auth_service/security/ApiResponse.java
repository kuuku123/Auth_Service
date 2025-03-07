package org.example.auth_service.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class ApiResponse<T>{
    private String message;
    private HttpStatus status;
    private T data;
}