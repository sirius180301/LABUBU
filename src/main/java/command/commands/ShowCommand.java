package command.commands;

import command.base.Command;
import command.base.Enviroment;
import command.exeptions.CommandException;
import command.managers.RouteCollection;
// Убираем зависимость от DatabaseManager, так как он больше не нужен здесь
// import command.base.database.DatabaseManager;

import java.io.InputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.HashMap;

public class ShowCommand extends Command {
    private final RouteCollection routeCollection;

    // Конструктор теперь проще, ему не нужен DatabaseManager
    public ShowCommand(RouteCollection routeCollection) {
        super("show");
        this.routeCollection = routeCollection;
    }

    @Override
    public void execute(Enviroment env, PrintStream out, InputStream in, String[] args) throws CommandException {
        // Просто выводим содержимое коллекции, которая уже находится в памяти.
        // Никаких обращений к БД здесь больше нет.
        if (routeCollection.getRoute().isEmpty()) {
            out.println("Коллекция пуста.");
        } else {
            // Сортируем для более наглядного вывода (опционально)
            routeCollection.getRoute().stream()
                    .sorted((r1, r2) -> Long.compare(r1.getId(), r2.getId()))
                    .forEach(out::println);
        }
    }

    @Override
    public String getHelp() {
        // Обновляем описание команды
        return "вывести все элементы коллекции, загруженные в память";
    }

    // Обновляем метод регистрации
    public static void register(HashMap<String, Command> map, RouteCollection routeCollection) {
        map.put("show", new ShowCommand(routeCollection));
    }
}