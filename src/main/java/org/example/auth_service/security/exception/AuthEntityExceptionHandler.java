package org.example.auth_service.security.exception;

import com.google.gson.Gson;
import org.example.auth_service.security.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthEntityExceptionHandler {

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException e) {
    ApiResponse<String> signupFailed = new ApiResponse<>(e.getMessage(), HttpStatus.BAD_REQUEST,
      e.getMessage());
    return new ResponseEntity<>(new Gson().toJson(signupFailed), HttpStatus.BAD_REQUEST);
  }
}
