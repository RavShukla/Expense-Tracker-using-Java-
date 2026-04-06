package model;

public class User {

    private final String username;
    private final String passwordHash;
    private final String createdAt;

    public User(String username, String passwordHash, String createdAt) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
