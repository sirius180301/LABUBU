package command.commands;

import command.RouteReader;
import command.base.Command;
import command.base.Enviroment;
import command.base.database.DatabaseManager;
import command.exeptions.CommandException;
import command.managers.RouteCollection;
import model.Route;

import java.io.InputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Lock;

public class AddCommand extends Command {
    private final RouteCollection routeCollection;
    private final DatabaseManager dbManager;

    public AddCommand(RouteCollection routeCollection, DatabaseManager dbManager) {
        super("add");
        this.routeCollection = routeCollection;
        this.dbManager = dbManager;
    }


    @Override
    public void execute(Enviroment env, PrintStream out, InputStream in, String[] args) throws CommandException {
        if (env.getCurrentUser() == null) {
            throw new CommandException("Вы не авторизованы. Используйте команду login.");
        }

        try {
            Route newRoute = RouteReader.readRoute(in, out, routeCollection);
            newRoute.setUsername(env.getCurrentUser());

            long newId = dbManager.addRoute(newRoute, env.getCurrentUser());
            newRoute.setId(newId); // Устанавливаем ID, полученный из БД

            routeCollection.add(newRoute);
            out.println("Элемент успешно добавлен в коллекцию.");
        } catch (NoSuchElementException e) {
            throw new CommandException("Ошибка при добавлении элемента: " + e.getMessage());
        } catch (SQLException e) {
            throw new CommandException("Ошибка базы данных: " + e.getMessage());
        }
    }

    @Override
    public String getHelp() {
        return "добавить новый элемент в коллекцию";
    }

    public static void register(HashMap<String, Command> commandMap,
                                RouteCollection routeCollection,
                                Lock collectionLock, DatabaseManager dbManager) {
        commandMap.put("add", new AddCommand(routeCollection, dbManager));
    }
}
