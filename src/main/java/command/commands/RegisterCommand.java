package command.commands;

import command.base.Command;
import command.base.Enviroment;
import command.base.database.UserAuthenticator;
import command.exeptions.CommandException;


import java.io.InputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.HashMap;

public class RegisterCommand extends Command {

    public RegisterCommand(UserAuthenticator userAuthenticator) {
        super("register");
    }

    @Override
    public void execute(Enviroment env, PrintStream out, InputStream in, String[] args) throws CommandException {
        if (args.length != 2) {
            throw new CommandException("Неверное количество аргументов для команды register. Требуется имя пользователя и пароль.");
        }
        String username = args[0];
        String password = args[0];}

    @Override
    public String getHelp() {
        return null;
    }
}

