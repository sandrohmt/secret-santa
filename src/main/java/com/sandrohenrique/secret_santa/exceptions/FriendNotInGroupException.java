package com.sandrohenrique.secret_santa.exceptions;

public class FriendNotInGroupException extends RuntimeException{
    public FriendNotInGroupException(String message) {
        super(message);
    }
}
