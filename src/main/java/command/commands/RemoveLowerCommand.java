package command.commands;

import command.base.Command;
import command.base.Enviroment;
import command.base.database.DatabaseManager;
import command.exeptions.CommandException;
import command.managers.RouteCollection;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;

public class RemoveLowerCommand extends Command {
    private final RouteCollection routeCollection;

    public RemoveLowerCommand(RouteCollection routeCollection) {
        super("remove_lower");
        this.routeCollection = routeCollection;
    }



    @Override
    public void execute(Enviroment env, PrintStream out, InputStream in, String[] args) throws CommandException {
        out.print("Введите значение distance: ");
        out.flush();

        try {
            // Считываем значение distance из входного потока
            Scanner scanner = new Scanner(in);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                throw new CommandException("Значение distance не может быть пустым.");
            }

            Float distance = Float.parseFloat(input);

            boolean removed = routeCollection.getRoute().removeIf(route ->
                    route.getDistance() != null && route.getDistance() < distance);

            if (removed) {
                // Пересчитываем ID после удаления
                routeCollection.reassignIds();

                out.println("Элементы с distance меньше " + distance + " успешно удалены.");
            } else {
                out.println("Нет элементов с distance меньше " + distance + ".");
            }

        } catch (NumberFormatException e) {
            throw new CommandException("Неверный формат distance. Введите число с плавающей точкой.");
        }
    }

    @Override
    public String getHelp() {
        return "удалить из коллекции все элементы, у которых значение distance меньше заданного";
    }

    public static void register(HashMap<String, Command> commandMap, RouteCollection routeCollection, Lock collectionLock, DatabaseManager dbManager) {
        RemoveLowerCommand removeLowerCommand = new RemoveLowerCommand(routeCollection);
        commandMap.put(removeLowerCommand.getName(), removeLowerCommand);
    }
}
