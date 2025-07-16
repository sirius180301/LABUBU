package command.commands;

import command.base.Command;
import command.base.Enviroment;
import command.base.database.UserAuthenticator;
import command.exeptions.CommandException;


import java.io.InputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.HashMap;

public class LoginCommand extends Command {

    private final UserAuthenticator userAuthenticator;

    public LoginCommand(UserAuthenticator userAuthenticator) {
        super("login");
        this.userAuthenticator = userAuthenticator;
    }

    @Override
    public void execute(Enviroment env, PrintStream out, InputStream in, String[] args) throws CommandException {
        if (args.length != 2) {
            throw new CommandException("Неверное количество аргументов для команды login. Требуется имя пользователя и пароль.");
        }

        String username = args[0];
        String password = args[1];

        try {
            if (userAuthenticator.authenticateUser(username, password)) {
                out.println("Вход выполнен успешно для пользователя: " + username);
            } else {
                throw new CommandException("Неверные учетные данные.");
            }
        } catch (CommandException e) {
            throw new CommandException("Ошибка при входе: " + e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
