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

public class SaveCommand extends Command {
    private final DatabaseManager dbManager;
    private final RouteCollection routeCollection;

    public SaveCommand(RouteCollection routeCollection, DatabaseManager dbManager) {
        super("save");
        this.dbManager = dbManager;
        this.routeCollection = routeCollection;
    }

    @Override
    public void execute(Enviroment env, PrintStream out, InputStream in, String[] args) throws CommandException {
        String currentUser = env.getCurrentUser(); // Просто берем пользователя из env

        try {
            out.println("Синхронизация с базой данных для пользователя '" + currentUser + "'...");
            dbManager.syncUserRoutes(currentUser, routeCollection.getRoute());
            out.println("Изменения успешно сохранены.");
        } catch (SQLException e) {
            throw new CommandException("Ошибка при сохранении в базу данных: " + e.getMessage());
        }
    }

    @Override
    public String getHelp() {
        return "сохранить все изменения в базу данных";
    }

    public static void register(HashMap<String, Command> commandMap, RouteCollection routeCollection, DatabaseManager dbManager) {
        // Теперь мы используем общую коллекцию, а не создаем новую
        commandMap.put("save", new SaveCommand(routeCollection, dbManager));
    }
}