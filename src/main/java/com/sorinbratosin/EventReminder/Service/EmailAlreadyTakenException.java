package com.sorinbratosin.EventReminder.Service;

public class EmailAlreadyTakenException extends Exception {

    public EmailAlreadyTakenException(String message) {
        super(message);
    }
}
