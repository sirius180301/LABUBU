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
                     "INSERT INTO users (name, password) VALUES (?, ?)")) {

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
                     "SELECT password FROM users WHERE name = ?")) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password").equals(hashedPassword);
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
    // Новый метод, который использует метод из DatabaseManager
    public boolean doesUserExist(String username) throws SQLException {
        return dbManager.doesUserExist(username);
    }
    /**
     * Проверяет, есть ли в базе данных хотя бы один пользователь.
     * @return true, если пользователи есть, иначе false.
     * @throws SQLException в случае ошибки SQL.
     */
    public boolean hasAnyUsers() throws SQLException {
        return dbManager.countUsers() > 0;
    }
}
