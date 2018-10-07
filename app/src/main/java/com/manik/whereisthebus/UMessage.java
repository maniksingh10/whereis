package com.manik.whereisthebus;

public class UMessage {


    private String message ;
    private String time ;
    private String username ;

    public UMessage() {
    }

    public UMessage(String message, String time, String username) {
        this.message = message;
        this.time = time;
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

    public String getUsername() {
        return username;
    }
}
