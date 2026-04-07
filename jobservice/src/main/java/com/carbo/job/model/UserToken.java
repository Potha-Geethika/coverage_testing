package com.carbo.job.model;

public class UserToken {
    private String message;
    private String token;

    public String getName() {

        return message;
    }

    public void setName(String name) {
        this.message = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
