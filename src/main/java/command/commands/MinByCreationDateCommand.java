package command.commands;

import command.base.Command;
import command.base.Enviroment;
import command.managers.RouteCollection;
import model.Route;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Optional;

public class MinByCreationDateCommand extends Command {
    private final RouteCollection routeCollection;

    public MinByCreationDateCommand(RouteCollection routeCollection) {
        super("min_by_creation_date");
        this.routeCollection = routeCollection;
    }

    @Override
    public void execute(Enviroment env, PrintStream out, InputStream in, String[] args) {
        Optional<Route> minRoute = routeCollection.getRoute().stream()
                .min(Comparator.comparing(Route::getCreationDate));

        if (minRoute.isPresent()) {
            out.println("Элемент с минимальной датой создания: " + minRoute.get());
        } else {
            out.println("Коллекция пуста.");
        }
    }

    @Override
    public String getHelp() {
        return "вывести любой объект из коллекции, значение поля creationDate которого является минимальным";
    }

    public static void register(HashMap<String, Command> commandMap, RouteCollection routeCollection) {
        MinByCreationDateCommand minByCreationDateCommand = new MinByCreationDateCommand(routeCollection);
        commandMap.put(minByCreationDateCommand.getName(), minByCreationDateCommand);
    }
}
