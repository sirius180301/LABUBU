package command.commands;

import command.base.Command;
import command.base.Enviroment;

import command.base.database.DatabaseManager;
import command.exeptions.CommandException;
import command.managers.RouteCollection;

import java.io.InputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class PrintFieldAscendingDistanceCommand extends Command {
    private final RouteCollection routeCollection;
    private final DatabaseManager dbManager;

    public PrintFieldAscendingDistanceCommand(RouteCollection routeCollection, DatabaseManager dbManager) {
        super("print_field_ascending_distance");
        this.routeCollection = routeCollection;
        this.dbManager = dbManager;
    }


    public void execute(Enviroment env, PrintStream out, String[] args) throws CommandException {
        try {
            // Получаем отсортированные расстояния из базы данных
            List<Float> distances = dbManager.getSortedDistances();

            if (distances.isEmpty()) {
                out.println("Коллекция не содержит элементов с расстояниями.");
                return;
            }

            out.println("Значения поля distance в порядке возрастания:");
            distances.forEach(out::println);

        } catch (SQLException e) {
            throw new CommandException("Ошибка при получении данных из базы: " + e.getMessage());
        }
    }

    @Override
    public void execute(Enviroment env, PrintStream stdin, InputStream stdout, String[] commandsArgs) throws CommandException {

    }

    @Override
    public String getHelp() {
        return "вывести значения поля distance всех элементов в порядке возрастания";
    }

    public static void register(HashMap<String, Command> commandMap,
                                RouteCollection routeCollection,
                                DatabaseManager dbManager) {
        commandMap.put("print_field_ascending_distance",
                new PrintFieldAscendingDistanceCommand(routeCollection, dbManager));
    }
}
