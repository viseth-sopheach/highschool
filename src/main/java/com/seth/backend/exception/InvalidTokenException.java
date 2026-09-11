package com.seth.backend.exception;

public class InvalidTokenException extends RuntimeException {
   public InvalidTokenException(String message) {
      super(message);
   }
}