package data;

import java.io.Serializable;

public class User implements Serializable {
    private final String login;
    private final String password;

    public User(String username, String password) {
        this.login = username;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPasword() {
        return password;
    }

    @Override
    public String toString() {
        return "login: " + login + " password: " + password;
    }
}