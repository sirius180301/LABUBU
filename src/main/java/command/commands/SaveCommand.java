package command.commands;

import command.base.Command;
import command.exeptions.CommandException;
import command.managers.RouteCollection;

import java.util.HashMap;

/**
 * Команда для сохранения коллекции в файл.
 */
public class SaveCommand extends Command {
    private final RouteCollection routecollection;

    /**
     * Создает объект Save с заданным CollectionManager.
     *
     * @param routecollection Менеджер коллекции.
     */
    public SaveCommand(RouteCollection routecollection) {
        super("save");
        this.routecollection = routecollection;
    }

    public static void register(HashMap<String, Command> map, RouteCollection routeCollection) {
    }

    /**
     * Выполняет команду сохранения коллекции.
     *
     * @param args Аргументы команды (не используются).
     * @throws CommandException Если происходит ошибка при сохранении коллекции.
     */
    @Override
    public void execute(String[] args) throws CommandException {
        try {
            routecollection.getRoute();
            System.out.println(" ");
        } catch (Exception e) {
            throw new CommandException("Ошибка при сохранении коллекции: " + e.getMessage(), e);
        }
    }

    /**
     * Возвращает строку справки для команды save.
     *
     * @return Строка справки.
     */
    @Override
    public String getHelp() {
        return "save - сохранить коллекцию в файл";
    }
}
