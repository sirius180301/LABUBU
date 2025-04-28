package command.base;

import command.exeptions.CommandException;

import java.io.InputStream;
import java.io.PrintStream;

/**
 * Абстрактный класс Command представляет собой базовый класс для всех команд.
 * Он содержит имя команды и определяет методы, которые должны быть реализованы в подклассах.
 */
public abstract class Command {
    private final String name;

    /**
     * Конструктор для создания команды с заданным именем.
     *
     * @param name имя команды
     */
    protected Command(String name) {
        this.name = name;
    }

    /**
     * Получает имя команды.
     *
     * @return имя команды
     */
    public String getName() {
        return name;
    }

    /**
     * Выполняет команду с заданной средой и аргументами.
     *
     * @param env         среда выполнения команды
     * @param stdin       поток ввода
     * @param stdout      поток вывода
     * @param commandsArgs аргументы команды
     * @throws CommandException если произошла ошибка при выполнении команды
     */
    public abstract void execute(Enviroment env, PrintStream stdin, InputStream stdout, String[] commandsArgs) throws CommandException;

    /**
     * Получает справочную информацию о команде.
     * @return строка с помощью команды
     */
    public abstract String getHelp();
}
