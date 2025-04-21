package command.base;

import command.exeptions.CommandException;

import java.io.InputStream;
import java.io.PrintStream;

/**
 • Абстрактный класс Command, представляющий базовую команду.
 */
public abstract class Command {
    private final String name;

    /**
     * Конструктор класса Command.
     *
     * @param name Имя команды.
     */
    protected Command(String name) {
        this.name = name;
    }

    /**
     * Возвращает имя команды.
     *
     * @return Имя команды.
     */
    public String getName() {
        return name;
    }

    /**
     * Абстрактный метод для выполнения команды.
     *
     * @param env Окружение, в котором выполняется команда.
     * @param stdin Поток вывода для отображения результатов выполнения команды.
     * @param stdout Поток ввода для получения данных от пользователя.
     * @param commandsArgs Аргументы команды.
     * @throws CommandException Если во время выполнения команды произошла ошибка.
     */
    public abstract void execute(Enviroment env, PrintStream stdin, InputStream stdout, String[] commandsArgs) throws CommandException;

    /**
     * Абстрактный метод для получения справки по команде.
     *
     * @return Строка справки по команде.
     */
    public abstract String getHelp();
}

