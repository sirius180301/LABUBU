package command.commands;

import command.RouteReader;
import command.base.Command;
import command.base.Enviroment; // Исправлено название класса
import command.base.database.DatabaseManager;
import command.exeptions.CommandException; // Исправлено название пакета
import command.managers.RouteCollection;
import model.Route;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Lock;

/**
 * Класс AddIfMaxCommand представляет собой команду для добавления нового маршрута в коллекцию,
 * если его значение превышает значение наибольшего элемента в этой коллекции.
 */
public class AddIfMaxCommand extends Command {
    private final RouteCollection routeCollection;
    //private String username;

    /**
     * Конструктор для создания команды AddIfMaxCommand.
     *
     * @param routeCollection коллекция маршрутов, в которую будет добавлен новый маршрут
     */
    public AddIfMaxCommand(RouteCollection routeCollection) {
        super("add_if_max");
        this.routeCollection = routeCollection;
    }

    /**
     * Выполняет команду добавления нового маршрута, если он максимален.
     *
     * @param env  среда выполнения команды
     * @param out  поток вывода
     * @param in   поток ввода
     * @param args аргументы команды
     * @throws CommandException если произошла ошибка при выполнении команды
     */
    @Override
    public void execute(Enviroment env, PrintStream out, InputStream in, String[] args) throws CommandException {
        try {
            Route newRoute = RouteReader.readRoute(in, out, routeCollection );
            if (routeCollection.getRoute().isEmpty() || newRoute.compareTo(Collections.max(routeCollection.getRoute())) > 0) {
                routeCollection.add(newRoute);
                out.println("Элемент успешно добавлен в коллекцию (как максимальный).");
            } else {
                out.println("Новый элемент не является максимальным и не был добавлен.");
            }
        } catch (NoSuchElementException e) {
            throw new CommandException("Ошибка при добавлении элемента: " + e.getMessage());
        }
    }

    /**
     * Получает справочную информацию о команде.
     *
     * @return строка с помощью команды
     */
    @Override
    public String getHelp() {
        return "добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции";
    }

    /**
     * Регистрирует команду в указанной карте команд.
     *
     * @param commandMap      карта команд
     * @param routeCollection коллекция маршрутов
     * @param collectionLock
     * @param dbManager
     */
    public static void register(HashMap<String, Command> commandMap, RouteCollection routeCollection, Lock collectionLock, DatabaseManager dbManager) {
        AddIfMaxCommand addIfMaxCommand = new AddIfMaxCommand(routeCollection);
        commandMap.put(addIfMaxCommand.getName(), addIfMaxCommand);
    }
}
