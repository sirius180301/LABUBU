package command.commands;

import command.base.Command;
import command.base.Enviroment;
import command.exeptions.CommandException;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ExecuteScriptCommand extends Command {

    private final HashMap<String, Command> commandMap;
    private final Set<String> executingScripts;  // Для предотвращения рекурсии

    public ExecuteScriptCommand(HashMap<String, Command> commandMap) {
        super("execute_script");
        this.commandMap = commandMap;
        this.executingScripts = new HashSet<>();
    }

    @Override
    public void execute(Enviroment env, PrintStream out, InputStream in, String[] args) throws CommandException {
        if (args.length != 1) {
            throw new CommandException("Команда execute_script требует один аргумент - путь к файлу скрипта.");
        }

        String scriptFilePath = args[0];
        File scriptFile = new File(scriptFilePath);

        if (!scriptFile.exists() || !scriptFile.isFile()) {
            throw new CommandException("Файл скрипта не найден: " + scriptFilePath);
        }

        String absolutePath = scriptFile.getAbsolutePath();

        if (executingScripts.contains(absolutePath)) {
            throw new CommandException("Обнаружена рекурсия вызова скрипта: " + scriptFilePath);
        }

        executingScripts.add(absolutePath);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(scriptFile), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue; // Пропускаем пустые строки и комментарии
                }

                out.println("Выполняется команда из скрипта: " + line);

                String[] parts = line.split("\\s+");
                String commandName = parts[0];
                String[] commandArgs = new String[parts.length - 1];
                if (parts.length > 1) {
                    System.arraycopy(parts, 1, commandArgs, 0, commandArgs.length);
                }

                Command command = commandMap.get(commandName);
                if (command == null) {
                    out.println("Неизвестная команда в скрипте: " + commandName);
                    continue;
                }

                try {
                    // Передаем System.in для интерактивного ввода с консоли
                    command.execute(env, out, System.in, commandArgs);
                } catch (Exception e) {
                    out.println("Ошибка при выполнении команды '" + commandName + "': " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new CommandException("Ошибка при чтении файла скрипта: " + e.getMessage());
        } finally {
            executingScripts.remove(absolutePath);
        }
    }

    @Override
    public String getHelp() {
        return "считать и исполнить скрипт из указанного файла";
    }

    public static void register(HashMap<String, Command> commandMap) {
        ExecuteScriptCommand executeScriptCommand = new ExecuteScriptCommand(commandMap);
        commandMap.put(executeScriptCommand.getName(), executeScriptCommand);
    }
}
