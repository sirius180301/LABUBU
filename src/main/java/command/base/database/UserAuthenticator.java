package command.base.database;


import command.exeptions.CommandException;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class UserAuthenticator {

    private final DatabaseHandler databaseHandler;

    public UserAuthenticator(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    public boolean registerUser(String username, String password) throws CommandException {
        try {
            if (databaseHandler.registerUser(username, password)) {
                return true;
            } else {
                throw new CommandException("Ошибка при регистрации пользователя.");
            }
        } catch (SQLException e) {
            throw new CommandException("Ошибка базы данных при регистрации: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            throw new CommandException("Ошибка хеширования пароля: " + e.getMessage());
        }
    }

    public boolean authenticateUser(String username, String password) throws CommandException {
        try {
            return databaseHandler.checkCredentials(username, password);
        } catch (SQLException e) {
            throw new CommandException("Ошибка базы данных при аутентификации: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            throw new CommandException("Ошибка хеширования пароля: " + e.getMessage());
        }
    }
}
