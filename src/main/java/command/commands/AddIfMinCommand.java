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

public class AddIfMinCommand extends Command {
    private final RouteCollection routeCollection;

    public AddIfMinCommand(RouteCollection routeCollection) {
        super("add_if_min");
        this.routeCollection = routeCollection;
    }

    @Override
    public void execute(Enviroment env, PrintStream out, InputStream in, String[] args) throws CommandException {
        try {
            Route newRoute = RouteReader.readRoute(in, out, routeCollection);

            // Решение принимаем на основе данных в памяти
            if (routeCollection.getRoute().isEmpty() || newRoute.compareTo(Collections.min(routeCollection.getRoute())) < 0) {
                // --- ИСПРАВЛЕНИЕ ЗДЕСЬ ---
                newRoute.setUsername(env.getCurrentUser()); // Устанавливаем владельца
                routeCollection.add(newRoute);              // И только потом добавляем

                out.println("Элемент успешно добавлен. Не забудьте 'save'.");
            } else {
                out.println("Новый элемент не является минимальным и не был добавлен.");
            }
        } catch (NoSuchElementException e) {
            throw new CommandException("Ошибка при добавлении элемента: " + e.getMessage());
        }
    }

    @Override
    public String getHelp() {
        return "добавить новый элемент, если его значение меньше наименьшего";
    }

    public static void register(HashMap<String, Command> commandMap, RouteCollection routeCollection) {
        commandMap.put("add_if_min", new AddIfMinCommand(routeCollection));
    }
}