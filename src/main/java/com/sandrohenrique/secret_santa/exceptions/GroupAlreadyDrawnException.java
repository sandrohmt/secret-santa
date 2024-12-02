package com.sandrohenrique.secret_santa.exceptions;

public class GroupAlreadyDrawnException extends RuntimeException{
    public GroupAlreadyDrawnException(String message) {
        super(message);
    }
}
