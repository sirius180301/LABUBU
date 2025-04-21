package command.base;

import command.exeptions.CommandException;

import java.io.InputStream;
import java.io.PrintStream;


public abstract class Command {
    private final String name;

    protected Command(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public abstract void execute(Enviroment env, PrintStream stdin, InputStream stdout, String[] commandsArgs) throws CommandException;
    public abstract String getHelp();
}

