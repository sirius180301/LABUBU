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
    // 1. Оставляем только те зависимости, что реально нужны
    private final RouteCollection routeCollection;

    // 2. Упрощаем конструктор
    public AddCommand(RouteCollection routeCollection) {
        super("add");
        this.routeCollection = routeCollection;
    }


    @Override
    public void execute(Enviroment env, PrintStream out, InputStream in, String[] args) throws CommandException {
        try {
            Route newRoute = RouteReader.readRoute(in, out, routeCollection);
            newRoute.setUsername(env.getCurrentUser());

            // Просто вызываем add. Он сам возьмет правильный nextId
            routeCollection.add(newRoute);

            out.println("Элемент успешно добавлен в коллекцию" + newRoute.getId());
            out.println("Не забудьте выполнить 'save' для сохранения.");
        } catch (Exception e) {
            throw new CommandException("Ошибка при добавлении элемента: " + e.getMessage());
        }
    }

    @Override
    public String getHelp() {
        return "добавить новый элемент в коллекцию";
    }

    // 3. Упрощаем статический метод register
    public static void register(HashMap<String, Command> commandMap,
                                RouteCollection routeCollection) {
        commandMap.put("add", new AddCommand(routeCollection));
    }
}