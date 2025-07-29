package command.commands;

import command.base.Command;
import command.base.Enviroment;
import command.managers.RouteCollection;
import model.Route;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class PrintFieldAscendingDistanceCommand extends Command {
    private final RouteCollection routeCollection;

    public PrintFieldAscendingDistanceCommand(RouteCollection routeCollection) {
        super("print_field_ascending_distance");
        this.routeCollection = routeCollection;
    }

    @Override
    public void execute(Enviroment env, PrintStream out, InputStream in, String[] args) {
        // Берем данные из коллекции в памяти, сортируем и выводим
        List<Float> distances = routeCollection.getRoute().stream()
                .map(Route::getDistance)
                .filter(Objects::nonNull)
                .sorted()
                .toList();

        if (distances.isEmpty()) {
            out.println("В коллекции нет элементов с указанным расстоянием.");
        } else {
            out.println("Значения поля distance в порядке возрастания:");
            distances.forEach(out::println);
        }
    }

    @Override
    public String getHelp() {
        return "вывести значения поля distance всех элементов в порядке возрастания";
    }

    public static void register(HashMap<String, Command> commandMap, RouteCollection routeCollection) {
        commandMap.put("print_field_ascending_distance", new PrintFieldAscendingDistanceCommand(routeCollection));
    }
}