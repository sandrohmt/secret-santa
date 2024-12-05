package com.sandrohenrique.secret_santa.exceptions;

public class FriendAlreadyInGroupException extends RuntimeException{
    public FriendAlreadyInGroupException(String message) {
        super(message);
    }
}
