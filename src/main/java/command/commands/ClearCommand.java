package command.commands;

import command.base.Command;
import command.base.Enviroment;
import command.exeptions.CommandException;
import command.managers.RouteCollection;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;

public class ClearCommand extends Command {
    private final RouteCollection routeCollection;

    public ClearCommand(RouteCollection routeCollection) {
        super("clear");
        this.routeCollection = routeCollection;
    }

    @Override
    public void execute(Enviroment env, PrintStream out, InputStream in, String[] args) throws CommandException {
        String currentUser = env.getCurrentUser();
        boolean removed = routeCollection.getRoute().removeIf(route -> route.getUsername().equals(currentUser));

        if (removed) {
            routeCollection.findAndSetNextId();
            out.println("Все ваши элементы были удалены из коллекции");
            out.println("Выполните команду 'save', чтобы применить изменения.");
        } else {
            out.println("У вас нет элементов в коллекции для удаления.");
        }
    }

    @Override
    public String getHelp() {
        return "очистить коллекцию от принадлежащих вам элементов";
    }

    public static void register(HashMap<String, Command> commandMap, RouteCollection routeCollection) {
        commandMap.put("clear", new ClearCommand(routeCollection));
    }
}