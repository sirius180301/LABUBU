package command.commands;

import command.base.Command;
import command.base.Enviroment;
import command.base.database.DatabaseManager;
import command.exeptions.CommandException;
import command.managers.RouteCollection;

import java.io.InputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.HashMap;

import static java.lang.System.out;

public class SaveCommand extends Command {
    private final DatabaseManager dbManager;
    private final RouteCollection routeCollection;

    public SaveCommand(RouteCollection routeCollection, DatabaseManager dbManager) {
        super("save");
        this.dbManager = dbManager;
        this.routeCollection = routeCollection;
    }

    /*@Override
    public void execute(Enviroment env, PrintStream out, String[] args) throws CommandException {
        if (env.getCurrentUser() == null) {
            throw new CommandException("Вы не авторизованы. Используйте команду login.");
        }

        try {
            dbManager.saveAllChanges();
            out.println("Все изменения успешно сохранены в базу данных");
        } catch (SQLException e) {
            throw new CommandException("Ошибка при сохранении в базу данных: " + e.getMessage());
        }*/
    //}

    @Override
    public void execute(Enviroment env, PrintStream stdin, InputStream stdout, String[] commandsArgs) throws CommandException {
        if (env.getCurrentUser() == null) {
            throw new CommandException("Вы не авторизованы. Используйте команду login.");
        }

        try {
            dbManager.saveAllChanges();
            out.println("Все изменения успешно сохранены в базу данных");
        } catch (SQLException e) {
            throw new CommandException("Ошибка при сохранении в базу данных: " + e.getMessage());
        }
    }

    @Override
    public String getHelp() {
        return "сохранить все изменения в базу данных";
    }

    public static void register(HashMap<String, Command> commandMap, DatabaseManager dbManager) {
        commandMap.put("save", new SaveCommand(new RouteCollection(),dbManager));
    }
}
