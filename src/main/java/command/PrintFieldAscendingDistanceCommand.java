package command;

import command.base.Command;
import command.base.Enviroment;
import command.managers.RouteCollection;
import model.Route;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@XmlRootElement
class PrintFieldAscendingDistanceCommand extends Command {
    private final RouteCollection routeCollection;

    protected PrintFieldAscendingDistanceCommand(RouteCollection routeCollection) {
        super("print_field_ascending_distance");
        this.routeCollection = routeCollection;
    }

    @Override
    public void execute(Enviroment env, PrintStream out, InputStream in, String[] args) {
        List<Float> distances = routeCollection.getRoute().stream()
                .map(Route::getDistance)
                .filter(Objects::nonNull)
                .sorted()
                .toList();

        distances.forEach(out::println);
    }

    @Override
    public String getHelp() {
        return "вывести значения поля distance всех элементов в порядке возрастания";
    }

    public static void register(HashMap<String, Command> commandMap, RouteCollection routeCollection) {
        PrintFieldAscendingDistanceCommand printFieldAscendingDistanceCommand = new PrintFieldAscendingDistanceCommand(routeCollection);
        commandMap.put(printFieldAscendingDistanceCommand.getName(), printFieldAscendingDistanceCommand);
    }
}