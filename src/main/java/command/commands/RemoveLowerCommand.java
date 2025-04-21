package command.commands;

import command.RouteReader;
import command.base.Command;
import command.base.Enviroment;
import command.exeptions.CommandException;
import command.managers.RouteCollection;
import model.Route;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.NoSuchElementException;

public class RemoveLowerCommand extends Command {
    private final RouteCollection routeCollection;

    protected RemoveLowerCommand(RouteCollection routeCollection) {
        super("remove_lower");
        this.routeCollection = routeCollection;
    }

    @Override
    public void execute(Enviroment env, PrintStream out, InputStream in, String[] args) throws CommandException {
        try {
            Route newRoute = RouteReader.readRoute(in, out, routeCollection); // Используем RouteReader
            routeCollection.getRoute().removeIf(route -> route.compareTo(newRoute) < 0); // Исправлено на newRoute
            out.println("Элементы, меньшие заданного, успешно удалены.");
        } catch (NoSuchElementException e) {
            throw new CommandException("Ошибка при удалении элементов: " + e.getMessage());
        }
    }

    @Override
    public String getHelp() {
        return "удалить из коллекции все элементы, меньшие, чем заданный";
    }

    public static void register(HashMap<String, Command> commandMap, RouteCollection routeCollection) {
        RemoveLowerCommand removeLowerCommand = new RemoveLowerCommand(routeCollection);
        commandMap.put(removeLowerCommand.getName(), removeLowerCommand);
    }
}

