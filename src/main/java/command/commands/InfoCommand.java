package command.commands;

import command.base.Command;
import command.base.Enviroment;
import command.managers.RouteCollection;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;

public class InfoCommand extends Command {
    private final RouteCollection routeCollection;

    protected InfoCommand(RouteCollection routeCollection) {
        super("info");
        this.routeCollection = routeCollection;
    }

    @Override
    public void execute(Enviroment env, PrintStream stdin, InputStream stdout, String[] commandsArgs) {
        stdin.println(routeCollection.toString());
    }

    @Override
    public String getHelp() {
        return "вывести информацию о коллекции";
    }

    public static void register(HashMap<String, Command> map, RouteCollection routeCollection) {
        InfoCommand infoCommand = new InfoCommand(routeCollection);
        map.put(infoCommand.getName(), infoCommand);
    }
}
