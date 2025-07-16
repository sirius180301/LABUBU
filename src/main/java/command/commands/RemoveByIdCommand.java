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
import java.util.concurrent.locks.Lock;

public class RemoveByIdCommand extends Command {
    private final RouteCollection routeCollection;
    private final Lock collectionLock;
    private final DatabaseManager dbManager;

    public RemoveByIdCommand(RouteCollection routeCollection, Lock collectionLock, DatabaseManager dbManager) {
        super("remove_by_id");
        this.routeCollection = routeCollection;
        this.collectionLock = collectionLock;
        this.dbManager = dbManager;
    }


    public void execute(Enviroment env, PrintStream out, String[] args) throws CommandException {
        if (env.getCurrentUser() == null) {
            throw new CommandException("Вы не авторизованы. Используйте команду login.");
        }

        if (args.length != 1) {
            throw new CommandException("Неверное количество аргументов. Использование: remove_by_id <id>");
        }

        try {
            long id = Long.parseLong(args[0]);
            collectionLock.lock();
            try {
                if (dbManager.removeRouteById(id, env.getCurrentUser())) {
                    routeCollection.removeById(id);
                    out.println("Элемент с ID " + id + " успешно удален.");
                } else {
                    throw new CommandException("Элемент не найден или вам не принадлежит");
                }
            } finally {
                collectionLock.unlock();
            }
        } catch (NumberFormatException e) {
            throw new CommandException("Неверный формат ID. Должно быть число.");
        } catch (SQLException e) {
            throw new CommandException("Ошибка базы данных: " + e.getMessage());
        }
    }

    @Override
    public void execute(Enviroment env, PrintStream stdin, InputStream stdout, String[] commandsArgs) throws CommandException {

    }

    @Override
    public String getHelp() {
        return "удалить элемент из коллекции по его id";
    }

    public static void register(HashMap<String, Command> commandMap,
                                RouteCollection routeCollection,
                                Lock collectionLock,
                                DatabaseManager dbManager) {
        commandMap.put("remove_by_id",
                new RemoveByIdCommand(routeCollection, collectionLock, dbManager));
    }
}
