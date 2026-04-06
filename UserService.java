package services;

import dao.UserDao;
import model.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserService {

    private final UserDao userDao;

    public UserService() {
        this.userDao = new UserDao();
    }

    public boolean register(String username, String password) {
        String cleanUsername = validateUsername(username);
        String cleanPassword = validatePassword(password);

        if (userDao.userExists(cleanUsername)) {
            return false;
        }

        User user = new User(cleanUsername, hashPassword(cleanPassword), java.time.LocalDateTime.now().toString());
        return userDao.registerUser(user);
    }

    public boolean login(String username, String password) {
        String cleanUsername = validateUsername(username);
        String cleanPassword = validatePassword(password);
        return userDao.authenticateUser(cleanUsername, hashPassword(cleanPassword));
    }

    public String normalizeUsername(String username) {
        return validateUsername(username);
    }

    private String validateUsername(String username) {
        String cleanUsername = username == null ? "" : username.trim();
        if (cleanUsername.length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters.");
        }
        return cleanUsername;
    }

    private String validatePassword(String password) {
        String cleanPassword = password == null ? "" : password.trim();
        if (cleanPassword.length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters.");
        }
        return cleanPassword;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte value : hashed) {
                builder.append(String.format("%02x", value));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Unable to hash password.", e);
        }
    }
}
