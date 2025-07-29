package command.commands;

import command.base.Command;
import command.base.Enviroment;
import command.exeptions.CommandException;
import command.managers.RouteCollection;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class RemoveByIdCommand extends Command {
    private final RouteCollection routeCollection;

    public RemoveByIdCommand(RouteCollection routeCollection) {
        super("remove_by_id");
        this.routeCollection = routeCollection;
    }

    @Override
    public void execute(Enviroment env, PrintStream out, InputStream in, String[] args) throws CommandException {
        if (args.length != 0) {
            throw new CommandException("Использование: просто введите 'remove_by_id' без аргументов.");
        }

        Scanner scanner = new Scanner(in);
        out.print("Введите ID элемента, который хотите удалить: ");

        long id;
        try {
            id = Long.parseLong(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            throw new CommandException("Неверный формат ID. Должно быть введено целое число.");
        } catch (NoSuchElementException e) {
            throw new CommandException("Ввод ID был прерван.");
        }

        java.util.Optional<model.Route> routeToRemove = routeCollection.getRoute().stream()
                .filter(r -> r.getId() == id)
                .findFirst();

        if (routeToRemove.isEmpty()) {
            throw new CommandException("Элемент с ID " + id + " не найден в коллекции.");
        }

        if (!routeToRemove.get().getUsername().equals(env.getCurrentUser())) {
            throw new CommandException("Ошибка: вы не можете удалить этот элемент, так как он вам не принадлежит.");
        }

        routeCollection.removeById(id);
        routeCollection.findAndSetNextId();
        out.println("Элемент с ID " + id + " успешно удален");
        out.println("Выполните команду 'save', чтобы сохранить изменения.");
    }

    @Override
    public String getHelp() {
        return "удалить элемент из коллекции по его id";
    }

    public static void register(HashMap<String, Command> commandMap, RouteCollection routeCollection) {
        commandMap.put("remove_by_id", new RemoveByIdCommand(routeCollection));
    }
}