package com.sandrohenrique.secret_santa.exceptions;

public class InsufficientFriendsException extends RuntimeException{
    public InsufficientFriendsException(String message) {
        super(message);
    }
}
