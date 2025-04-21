package command.commands;

import command.base.Command;
import command.base.Enviroment;
import command.exeptions.CommandException;
import command.managers.RouteCollection;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;

public class RemoveByIdCommand extends Command {
    private final RouteCollection routeCollection;

    public RemoveByIdCommand(RouteCollection routeCollection) {
        super("remove_by_id");
        this.routeCollection = routeCollection;
    }

    @Override
    public void execute(Enviroment env, PrintStream out, InputStream in, String[] args) throws CommandException {
        if (args.length != 1) {
            throw new CommandException("Неверное количество аргументов для команды remove_by_id. Требуется id.");
        }
        try {
            long id = Long.parseLong(args[0]);
            routeCollection.removeById(id);
            out.println("Элемент с id " + id + " успешно удален.");
        } catch (NumberFormatException e) {
            throw new CommandException("Неверный формат id. Id должен быть числом.");
        }
    }

    @Override
    public String getHelp() {
        return "удалить элемент из коллекции по его id";
    }

    public static void register(HashMap<String, Command> commandMap, RouteCollection routeCollection) {
        RemoveByIdCommand removeByIdCommand = new RemoveByIdCommand(routeCollection);
        commandMap.put(removeByIdCommand.getName(), removeByIdCommand);
    }
}