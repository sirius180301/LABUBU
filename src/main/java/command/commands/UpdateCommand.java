package command.commands;

import command.base.Command;
import command.base.Enviroment;
import command.exeptions.CommandException;
import command.managers.RouteCollection;
import model.Route;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.NoSuchElementException;

import static command.RouteReader.readRoute;

public class UpdateCommand extends Command {
    private final RouteCollection routeCollection;

    public UpdateCommand(RouteCollection routeCollection) {
        super("update");
        this.routeCollection = routeCollection;
    }

    @Override
    public void execute(Enviroment env, PrintStream out, InputStream in, String[] args) throws CommandException {
        if (args.length != 1) {
            throw new CommandException("Неверное количество аргументов для команды update. Требуется id.");
        }
        try {
            long id = Long.parseLong(args[0]);
            Route existingRoute = null;
            for (Route route : routeCollection.getRoute()) {
                if (route.getId() == id) {
                    existingRoute = route;
                    break;
                }
            }
            if (existingRoute == null) {
                throw new CommandException("Маршрут с указанным id не найден.");
            }

            Route updatedRoute = readRoute(in, out, routeCollection);
            updatedRoute.setId(id);

            routeCollection.getRoute().remove(existingRoute);
            routeCollection.getRoute().add(updatedRoute);
            routeCollection.sortRouteCollection();

            out.println("Элемент с id " + id + " успешно обновлен.");
        } catch (NumberFormatException e) {
            throw new CommandException("Неверный формат id. Id должен быть числом.");
        } catch (NoSuchElementException e) {
            throw new CommandException("Ошибка при обновлении элемента: " + e.getMessage());
        }
    }

    @Override
    public String getHelp() {
        return "обновить значение элемента коллекции, id которого равен заданному";
    }

    public static void register(HashMap<String, Command> commandMap, RouteCollection routeCollection) {
        UpdateCommand updateCommand = new UpdateCommand(routeCollection);
        commandMap.put(updateCommand.getName(), updateCommand);
    }
}
