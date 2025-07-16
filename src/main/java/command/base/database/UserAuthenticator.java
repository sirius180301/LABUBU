package command.base.database;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Base64;

public class UserAuthenticator {
    private final DatabaseManager dbManager;

    public UserAuthenticator(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public boolean registerUser(String username, String password) throws SQLException {
        String hashedPassword = hashPassword(password);

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO users (username, password_hash) VALUES (?, ?)")) {

            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) { // Ошибка уникальности
                throw new SQLException("Пользователь с таким именем уже существует");
            }
            throw e;
        }
    }

    public boolean authenticateUser(String username, String password) throws SQLException {
        String hashedPassword = hashPassword(password);

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT password_hash FROM users WHERE username = ?")) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password_hash").equals(hashedPassword);
                }
            }
            return false;
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Ошибка хэширования пароля", e);
        }
    }
}
