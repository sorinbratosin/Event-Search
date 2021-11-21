package com.sorinbratosin.EventReminder.Service;

public class PasswordsDontMatchException extends Exception{

    public PasswordsDontMatchException(String message) {
        super(message);
    }
}
