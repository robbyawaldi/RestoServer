package com.unindra.restoserver.models;

import com.unindra.restoserver.DB;
import org.sql2o.Connection;


public class User {
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static User user(String username) {
        try (Connection connection = DB.sql2o.open()) {
            final String query = "SELECT * FROM `user` WHERE `username` = :username";
            return connection
                    .createQuery(query)
                    .addParameter("username", username)
                    .executeAndFetchFirst(User.class);
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
