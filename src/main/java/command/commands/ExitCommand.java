package command.commands;

import command.base.Command;
import command.base.Enviroment;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;

public class ExitCommand extends Command {
    protected ExitCommand() {
        super("exit");
    }

    @Override
    public void execute(Enviroment env, PrintStream stdin, InputStream stdout, String[] args) {
        System.exit(0);
    }

    @Override
    public String getHelp() {
        return "завершить программу (без сохранения в файл)";
    }

    public static void register(HashMap<String, Command> map) {
        ExitCommand exitCommand = new ExitCommand();
        map.put(exitCommand.getName(), exitCommand);
    }
}
