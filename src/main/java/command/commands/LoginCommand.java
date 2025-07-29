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

public class LoginCommand extends Command {

    private final UserAuthenticator userAuthenticator;

    public LoginCommand(UserAuthenticator userAuthenticator) {
        super("login");
        this.userAuthenticator = userAuthenticator;
    }

    @Override
    public void execute(Enviroment env, PrintStream out, InputStream in, String[] args) throws CommandException {
        // Команда больше не принимает аргументы
        if (args.length != 0) {
            throw new CommandException("Для входа в уже существующий аккаунт введите команду login. Для создания аккаунта введите команду register");
        }

        try {
            // === НАША НОВАЯ ПРОВЕРКА ===
            if (!userAuthenticator.hasAnyUsers()) {
                throw new CommandException("В базе данных нет ни одного зарегистрированного пользователя. Пожалуйста, используйте команду register для регистрации.");
            }
            // ============================

            Scanner scanner = new Scanner(in);
            String username;
            String password;

            // --- Получаем имя пользователя ---
            out.print("Введите имя пользователя: ");
            username = scanner.nextLine().trim();
            if (username.isEmpty()) {
                throw new CommandException("Имя пользователя не может быть пустым.");
            }

            // --- Получаем пароль ---
            out.print("Введите пароль: ");
            if (System.console() != null) {
                char[] passwordChars = System.console().readPassword();
                password = new String(passwordChars);
            } else {
                out.println("\n(Безопасный ввод пароля недоступен. Пароль будет виден при вводе.)");
                out.print("Введите пароль еще раз: ");
                password = scanner.nextLine().trim();
            }

            if (password.isEmpty()) {
                throw new CommandException("Пароль не может быть пустым.");
            }

            // --- Основная логика входа ---
            if (userAuthenticator.authenticateUser(username, password)) {
                out.println("Вход выполнен успешно для пользователя: " + username);
                env.setCurrentUser(username);
            } else {
                throw new CommandException("Неверное имя пользователя или пароль.");
            }
        } catch (SQLException e) {
            throw new CommandException("Ошибка при работе с базой данных: " + e.getMessage());
        }
    }

    @Override
    public String getHelp() {
        return "Войти в систему.";
    }

    public static void register(HashMap<String, Command> commandMap, UserAuthenticator userAuthenticator) {
        LoginCommand loginCommand = new LoginCommand(userAuthenticator);
        commandMap.put(loginCommand.getName(), loginCommand);
    }
}
