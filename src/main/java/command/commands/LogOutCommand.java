package command.commands;

import command.base.Command;
import command.base.Enviroment;
import command.base.database.DatabaseManager;
import command.exeptions.CommandException;
import command.managers.RouteCollection;
import model.Route;

import java.io.InputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class LogOutCommand extends Command {

    private final RouteCollection routeCollection;
    private final DatabaseManager dbManager;

    public LogOutCommand(RouteCollection routeCollection, DatabaseManager dbManager) {
        super("logout");
        this.routeCollection = routeCollection;
        this.dbManager = dbManager;
    }

    @Override
    public void execute(Enviroment env, PrintStream out, InputStream in, String[] args) throws CommandException {
        String currentUser = env.getCurrentUser();
        if (currentUser == null) {
            throw new CommandException("Вы не авторизованы, чтобы выходить из системы.");
        }

        try {
            // 1. Получаем маршруты текущего пользователя из коллекции в памяти
            Set<Route> inMemoryUserRoutes = routeCollection.getRoute().stream()
                    .filter(route -> currentUser.equals(route.getUsername()))
                    .collect(Collectors.toSet());

            // 2. Получаем маршруты текущего пользователя из базы данных
            Set<Route> databaseUserRoutes = dbManager.getUserRoutes(currentUser);

            // 3. ПРАВИЛЬНОЕ СРАВНЕНИЕ
            boolean hasUnsavedChanges = false;
            if (inMemoryUserRoutes.size() != databaseUserRoutes.size()) {
                hasUnsavedChanges = true;
            } else {
                // Если размеры одинаковы, проверяем на наличие измененных элементов
                Map<Long, Route> dbRoutesMap = databaseUserRoutes.stream()
                        .collect(Collectors.toMap(Route::getId, route -> route));

                for (Route inMemoryRoute : inMemoryUserRoutes) {
                    Route dbRoute = dbRoutesMap.get(inMemoryRoute.getId());
                    if (dbRoute == null || !areRoutesSemanticallyEqual(inMemoryRoute, dbRoute)) {
                        hasUnsavedChanges = true;
                        break;
                    }
                }
            }

            // 4. Логика предупреждения и выхода
            if (hasUnsavedChanges) {
                out.println("Внимание! У вас есть несохраненные изменения (добавленные, измененные или удаленные маршруты).");
                out.println("Если вы выйдете сейчас, эти изменения будут потеряны.");
                out.print("Вы уверены, что хотите выйти без сохранения? (y/n): ");

                Scanner scanner = new Scanner(in);
                String confirmation = scanner.nextLine().trim();

                if (!confirmation.equalsIgnoreCase("y")) {
                    out.println("Выход отменен. Выполните команду 'save', чтобы сохранить изменения.");
                    return;
                }
            }

            env.setCurrentUser(null);
            out.println("Вы успешно вышли из системы.");
            out.println("Для продолжения работы войдите в систему ('login') или зарегистрируйтесь ('register').");

        } catch (SQLException e) {
            throw new CommandException("Ошибка при проверке несохраненных данных: " + e.getMessage());
        }
    }

    /**
     * Вспомогательный метод для полного сравнения двух объектов Route по всем значащим полям.
     */
    private boolean areRoutesSemanticallyEqual(Route r1, Route r2) {
        if (r1 == r2) return true;
        if (r1 == null || r2 == null) return false;

        // Сравниваем все поля, а не только ID
        return r1.getId() == r2.getId() &&
                Objects.equals(r1.getName(), r2.getName()) &&
                Objects.equals(r1.getCoordinates(), r2.getCoordinates()) &&
                Objects.equals(r1.getCreationDate(), r2.getCreationDate()) &&
                Objects.equals(r1.getFrom(), r2.getFrom()) &&
                Objects.equals(r1.getTo(), r2.getTo()) &&
                Objects.equals(r1.getDistance(), r2.getDistance());
    }

    @Override
    public String getHelp() {
        return "выйти из текущего аккаунта (с возможностью входа для другого пользователя)";
    }

    public static void register(HashMap<String, Command> commandMap, RouteCollection routeCollection, DatabaseManager dbManager) {
        commandMap.put("logout", new LogOutCommand(routeCollection, dbManager));
    }
}