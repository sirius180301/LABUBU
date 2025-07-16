package command.commands;

import command.base.Command;
import command.base.Enviroment;
import command.base.database.UserAuthenticator;
import command.exeptions.CommandException;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;

public class RegisterCommand extends Command {
    private final UserAuthenticator userAuthenticator;

    public RegisterCommand(UserAuthenticator userAuthenticator) {
        super("register");
        this.userAuthenticator = userAuthenticator;
    }

    @Override
    public void execute(Enviroment env, PrintStream out, InputStream in, String[] args) throws CommandException {
        if (args.length != 2) {
            throw new CommandException("Неверное количество аргументов. Использование: register <логин> <пароль>");
        }

        String username = args[0];
        String password = args[1];

        try {
            if (userAuthenticator.registerUser(username, password)) {
                out.println("Пользователь " + username + " успешно зарегистрирован");
                env.setCurrentUser(username); // Автоматический вход после регистрации
            } else {
                throw new CommandException("Не удалось зарегистрировать пользователя");
            }
        } catch (Exception e) {
            throw new CommandException("Ошибка регистрации: " + e.getMessage());
        }
    }

    @Override
    public String getHelp() {
        return "зарегистрировать нового пользователя: register <логин> <пароль>";
    }

    public static void register(HashMap<String, Command> commandMap, UserAuthenticator userAuthenticator) {
        RegisterCommand command = new RegisterCommand(userAuthenticator);
        commandMap.put(command.getName(), command);
    }
}
