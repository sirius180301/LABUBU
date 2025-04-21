package command.commands;

import command.base.Command;
import command.base.Enviroment;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;

public class HelpCommand extends Command {
    public HelpCommand() {
        super("help");
    }

    @Override
    public void execute(Enviroment env, PrintStream stdin, InputStream stdout, String[] commandsArgs) {
        HashMap<String, Command> stringCommandHashMap = env.getStringCommandHashMap();
        stringCommandHashMap.forEach((String key, Command value) -> {
            stdin.println(key + ":" + value.getHelp());
        });
    }

    @Override
    public String getHelp() {
        return "вывести справку по доступным командам";
    }

    public static void register(HashMap<String, Command> map) {
        HelpCommand helpCommand = new HelpCommand();
        map.put(helpCommand.getName(), helpCommand);

    }
}