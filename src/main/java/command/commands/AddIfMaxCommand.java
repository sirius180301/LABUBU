package command.commands;

import command.RouteReader;
import command.base.Command;
import command.base.Enviroment;
import command.exeptions.CommandException;
import command.managers.RouteCollection;
import model.Route;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.NoSuchElementException;

public class AddIfMaxCommand extends Command {
    private final RouteCollection routeCollection;

    public AddIfMaxCommand(RouteCollection routeCollection) {
        super("add_if_max");
        this.routeCollection = routeCollection;
    }

    @Override
    public void execute(Enviroment env, PrintStream out, InputStream in, String[] args) throws CommandException {
        try {
            Route newRoute = RouteReader.readRoute(in, out, routeCollection);
            if (routeCollection.getRoute().isEmpty() || newRoute.compareTo(Collections.max(routeCollection.getRoute())) > 0) {
                routeCollection.add(newRoute);
                out.println("Элемент успешно добавлен в коллекцию (как максимальный).");
            } else {
                out.println("Новый элемент не является максимальным и не был добавлен.");
            }
        } catch (NoSuchElementException e) {
            throw new CommandException("Ошибка при добавлении элемента: " + e.getMessage());
        }
    }

    @Override
    public String getHelp() {
        return "добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции";
    }

    public static void register(HashMap<String, Command> commandMap, RouteCollection routeCollection) {
        AddIfMaxCommand addIfMaxCommand = new AddIfMaxCommand(routeCollection);
        commandMap.put(addIfMaxCommand.getName(), addIfMaxCommand);
    }
}
