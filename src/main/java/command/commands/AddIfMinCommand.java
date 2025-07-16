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
import java.util.Collections;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Lock;

public class AddIfMinCommand extends Command {
    private final RouteCollection routeCollection;
    private String username;

    public AddIfMinCommand(RouteCollection routeCollection) {
        super("add_if_min");
        this.routeCollection = routeCollection;
    }

    @Override
    public void execute(Enviroment env, PrintStream out, InputStream in, String[] args) throws CommandException {
        try {
            Route newRoute = RouteReader.readRoute(in, out, routeCollection );
            if (routeCollection.getRoute().isEmpty() || newRoute.compareTo(Collections.min(routeCollection.getRoute())) < 0) {
                routeCollection.add(newRoute);
                out.println("Элемент успешно добавлен в коллекцию (как минимальный).");
            } else {
                out.println("Новый элемент не является минимальным и не был добавлен.");
            }
        } catch (NoSuchElementException e) {
            throw new CommandException("Ошибка при добавлении элемента: " + e.getMessage());
        }
    }

    @Override
    public String getHelp() {
        return "добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции";
    }

    public static void register(HashMap<String, Command> commandMap, RouteCollection routeCollection, Lock collectionLock, DatabaseManager dbManager) {
        AddIfMinCommand addIfMinCommand = new AddIfMinCommand(routeCollection);
        commandMap.put(addIfMinCommand.getName(), addIfMinCommand);
    }
}
