package com.sandrohenrique.secret_santa.exceptions;

public class UserAlreadyInGroupException extends RuntimeException{
    public UserAlreadyInGroupException(String message) {
        super(message);
    }
}
