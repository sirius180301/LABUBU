package command.commands;

import command.base.Command;
import command.base.Enviroment;
import command.exeptions.CommandException;
import command.managers.RouteCollection;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;

public class CountLessThanDistanceCommand extends Command {
    private final RouteCollection routeCollection;

    public CountLessThanDistanceCommand(RouteCollection routeCollection) {
        super("count_less_than_distance");
        this.routeCollection = routeCollection;
    }

    @Override
    public void execute(Enviroment env, PrintStream out, InputStream in, String[] args) throws CommandException {
        if (args.length != 1) {
            throw new CommandException("Неверное количество аргументов для команды count_less_than_distance. Требуется distance.");
        }
        try {
            Float distance = Float.parseFloat(args[0]);
            long count = routeCollection.getRoute().stream()
                    .filter(route -> route.getDistance() != null && route.getDistance() < distance)
                    .count();
            out.println("Количество элементов, у которых distance меньше " + distance + ": " + count);
        } catch (NumberFormatException e) {
            throw new CommandException("Неверный формат distance. Distance должно быть числом.");
        }
    }

    @Override
    public String getHelp() {
        return "вывести количество элементов, значение поля distance которых меньше заданного";
    }

    public static void register(HashMap<String, Command> commandMap, RouteCollection routeCollection) {
        CountLessThanDistanceCommand countLessThanDistanceCommand = new CountLessThanDistanceCommand(routeCollection);
        commandMap.put(countLessThanDistanceCommand.getName(), countLessThanDistanceCommand);
    }
}
