package data;

public class User {
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