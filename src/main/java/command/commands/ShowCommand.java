package command.commands;

import command.base.Command;
import command.base.Enviroment;
import command.managers.RouteCollection;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;

public class ShowCommand extends Command {
    private final RouteCollection routeCollection;

    protected ShowCommand(RouteCollection routeCollection) {
        super("show");
        this.routeCollection = routeCollection;
    }

    @Override
    public void execute(Enviroment env, PrintStream stdin, InputStream stdout, String[] commandsArgs) {
        if (routeCollection.getRoute().isEmpty()) {
            System.out.println("Коллекция не содержит данных.");}
        else{
            routeCollection.getRoute().forEach(stdin::println);}
    }

    @Override
    public String getHelp() {
        return "вывести все элементы коллекции";
    }

    public static void register(HashMap<String, Command> map, RouteCollection routeCollection) {
        ShowCommand showCommand = new ShowCommand(routeCollection);
        map.put(showCommand.getName(), showCommand);
    }
}
