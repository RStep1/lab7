package data;

import java.io.Serializable;

public class User implements Serializable {
    private final String username;
    private final String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPasword() {
        return password;
    }

    @Override
    public String toString() {
        return "username: " + username + "password: " + password;
    }
}