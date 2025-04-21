package command.commands;

import command.base.Command;
import command.base.Enviroment;
import command.managers.RouteCollection;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;

public class ClearCommand extends Command {
    private final RouteCollection routeCollection;

    public ClearCommand(RouteCollection routeCollection) {
        super("clear");
        this.routeCollection = routeCollection;
    }

    @Override
    public void execute(Enviroment env, PrintStream out, InputStream in, String[] args) {
        routeCollection.clear();
        out.println("Коллекция успешно очищена.");
    }

    @Override
    public String getHelp() {
        return "очистить коллекцию";
    }

    public static void register(HashMap<String, Command> commandMap, RouteCollection routeCollection) {
        ClearCommand clearCommand = new ClearCommand(routeCollection);
        commandMap.put(clearCommand.getName(), clearCommand);
    }
}