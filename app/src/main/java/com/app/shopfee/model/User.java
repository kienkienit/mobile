package com.app.shopfee.model;

import com.google.gson.Gson;

public class User {

    private String email;
    private String password;

    public User() {}

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String toJSon() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
