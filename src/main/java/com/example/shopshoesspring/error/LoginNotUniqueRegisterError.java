package com.example.shopshoesspring.error;

public class LoginNotUniqueRegisterError extends Error{
    public LoginNotUniqueRegisterError() {
    }

    public LoginNotUniqueRegisterError(String message) {
        super(message);
    }

    public LoginNotUniqueRegisterError(String message, Throwable cause) {
        super(message, cause);
    }

    public LoginNotUniqueRegisterError(Throwable cause) {
        super(cause);
    }

    public LoginNotUniqueRegisterError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
