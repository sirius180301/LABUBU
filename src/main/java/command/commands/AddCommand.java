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

public class AddCommand extends Command {
    private final RouteCollection routeCollection;
    private String username;

    public AddCommand(RouteCollection routeCollection) {
        super("add");
        this.routeCollection = routeCollection;
    }

    @Override
    public void execute(Enviroment env, PrintStream out, InputStream in, String[] args) throws CommandException {
        try {
            Route newRoute = RouteReader.readRoute(in, out, routeCollection);
            routeCollection.add(newRoute);
            out.println("Элемент успешно добавлен в коллекцию.");
        } catch (NoSuchElementException e) {
            throw new CommandException("Ошибка при добавлении элемента: " + e.getMessage());
        }
    }

    @Override
    public String getHelp() {
        return "добавить новый элемент в коллекцию";
    }

    public static void register(HashMap<String, Command> commandMap, RouteCollection routeCollection) {
        AddCommand addCommand = new AddCommand(routeCollection);
        commandMap.put(addCommand.getName(), addCommand);
    }
}
