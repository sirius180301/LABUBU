package command.commands;

import command.base.Command;
import command.base.Enviroment;
import command.base.database.UserAuthenticator;
import command.exeptions.CommandException;

import java.io.InputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Scanner;

public class RegisterCommand extends Command {
    private final UserAuthenticator userAuthenticator;

    public RegisterCommand(UserAuthenticator userAuthenticator) {
        super("register");
        this.userAuthenticator = userAuthenticator;
    }

    @Override
    public void execute(Enviroment env, PrintStream out, InputStream in, String[] args) throws CommandException {
        if (args.length != 0) {
            throw new CommandException("Для регистрации пользователя введите команду register");
        }

        Scanner scanner = new Scanner(in);
        out.print("Введите имя пользователя: ");
        String username = scanner.nextLine().trim();
        if (username.isEmpty()) {
            throw new CommandException("Имя пользователя не может быть пустым.");
        }

        try {
            // ГЛАВНАЯ ПРОВЕРКА: существует ли пользователь?
            if (userAuthenticator.doesUserExist(username)) {
                out.println("Пользователь с именем '" + username + "' уже существует. Пожалуйста, введите пароль для входа.");
                // Если да, запускаем процедуру входа
                performLogin(env, out, in, username);
            } else {
                out.println("Создание нового пользователя '" + username + "'.");
                // Если нет, запускаем процедуру регистрации
                performRegistration(env, out, in, username);
            }
        } catch (SQLException e) {
            throw new CommandException("Ошибка при работе с базой данных: " + e.getMessage());
        }
    }

    /**
     * Внутренний метод для выполнения логики регистрации (создания нового пользователя).
     */
    private void performRegistration(Enviroment env, PrintStream out, InputStream in, String username) throws CommandException, SQLException {
        out.print("Придумайте пароль: ");
        String password = readPassword(in);
        if (password.isEmpty()) {
            throw new CommandException("Пароль не может быть пустым.");
        }

        if (userAuthenticator.registerUser(username, password)) {
            out.println("Пользователь '" + username + "' успешно зарегистрирован.");
            env.setCurrentUser(username); // Автоматический вход
        } else {
            // Этот случай маловероятен, но оставим для полноты
            throw new CommandException("Не удалось зарегистрировать пользователя по неизвестной причине.");
        }
    }

    /**
     * Внутренний метод для выполнения логики входа (проверки пароля существующего пользователя).
     */
    private void performLogin(Enviroment env, PrintStream out, InputStream in, String username) throws CommandException, SQLException {
        out.print("Введите пароль: ");
        String password = readPassword(in);

        if (userAuthenticator.authenticateUser(username, password)) {
            out.println("Вход выполнен успешно для пользователя: " + username);
            env.setCurrentUser(username);
        } else {
            throw new CommandException("Неверный пароль.");
        }
    }

    /**
     * Вспомогательный метод для безопасного чтения пароля.
     */
    private String readPassword(InputStream in) {
        if (System.console() != null) {
            char[] passwordChars = System.console().readPassword();
            return new String(passwordChars);
        } else {
            // Резервный вариант для сред без консоли (например, некоторые IDE)
            Scanner scanner = new Scanner(in);
            return scanner.nextLine().trim();
        }
    }


    @Override
    public String getHelp() {
        return " зарегистрировать нового пользователя или войти, если он уже существует";
    }

    public static void register(HashMap<String, Command> commandMap, UserAuthenticator userAuthenticator) {
        RegisterCommand command = new RegisterCommand(userAuthenticator);
        commandMap.put(command.getName(), command);
    }
}