package command.commands;

import command.base.Command;
import command.base.Enviroment;
import command.exeptions.CommandException;
import command.managers.RouteCollection;

import java.io.*;
import java.util.HashMap;
import java.util.Scanner;

public class ExecuteScriptCommand extends Command {
    private final RouteCollection routeCollection;

    public ExecuteScriptCommand(RouteCollection routeCollection) {
        super("execute_script");
        this.routeCollection = routeCollection;
    }

    @Override
    public void execute(Enviroment env, PrintStream out, InputStream stdin, String[] args) throws CommandException {
        if (args.length != 1) {
            throw new CommandException("Неверное количество аргументов для команды execute_script. Требуется имя файла.");
        }
        String fileName = args[0];
        try (Scanner fileScanner = new Scanner(new File(fileName))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] s = line.split(" ");
                String[] commandsArgs = new String[s.length - 1];
                System.arraycopy(s, 1, commandsArgs, 0, commandsArgs.length);
                if (env.getStringCommandHashMap().containsKey(s[0])) {
                    Command command = env.getStringCommandHashMap().get(s[0]);
                    try {
                        command.execute(env, out, new ByteArrayInputStream(new byte[0]), commandsArgs); // Пустой InputStream для скрипта
                    } catch (CommandException e) {
                        System.err.println("Ошибка при выполнении команды из скрипта: " + e.getMessage());
                    }
                } else {
                    System.err.println("Неизвестная команда в скрипте: " + s[0]);
                }
            }
            out.println("Скрипт " + fileName + " успешно выполнен.");
        } catch (FileNotFoundException e) {
            throw new CommandException("Файл " + fileName + " не найден.");
        }
    }

    @Override
    public String getHelp() {
        return "считать и исполнить скрипт из указанного файла";
    }

    public static void register(HashMap<String, Command> commandMap, RouteCollection routeCollection) {
        ExecuteScriptCommand executeScriptCommand = new ExecuteScriptCommand(routeCollection);
        commandMap.put(executeScriptCommand.getName(), executeScriptCommand);
    }
}
