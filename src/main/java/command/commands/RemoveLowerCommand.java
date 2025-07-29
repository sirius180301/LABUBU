package command.commands;

import command.base.Command;
import command.base.Enviroment;
import command.exeptions.CommandException;
import command.managers.RouteCollection;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Scanner;

public class RemoveLowerCommand extends Command {
    private final RouteCollection routeCollection;

    public RemoveLowerCommand(RouteCollection routeCollection) {
        super("remove_lower");
        this.routeCollection = routeCollection;
    }

    @Override
    public void execute(Enviroment env, PrintStream out, InputStream in, String[] args) throws CommandException {
        out.print("Введите значение distance: ");
        Scanner scanner = new Scanner(in);

        try {
            Float distance = Float.parseFloat(scanner.nextLine().trim());
            String currentUser = env.getCurrentUser();

            boolean removed = routeCollection.getRoute().removeIf(route ->
                    route.getUsername().equals(currentUser) &&
                            route.getDistance() != null &&
                            route.getDistance() < distance);

            if (removed) {
                routeCollection.findAndSetNextId();
                out.println("Элементы удалены. Выполните 'save' для сохранения.");
            } else {
                out.println("Нет элементов, удовлетворяющих условию.");
            }
        } catch (NumberFormatException e) {
            throw new CommandException("Неверный формат distance.");
        }
    }

    @Override
    public String getHelp() {
        return "удалить из коллекции ваши элементы, значение distance которых меньше заданного";
    }

    public static void register(HashMap<String, Command> commandMap, RouteCollection routeCollection) {
        commandMap.put("remove_lower", new RemoveLowerCommand(routeCollection));
    }
}