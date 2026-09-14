package com.seth.backend.exception;

public class AccountPendingApprovalException extends RuntimeException {
   public AccountPendingApprovalException(String message) {
      super(message);
   }
}