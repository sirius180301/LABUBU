package command.managers;

import model.Route;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashSet;

public class RouteCollection {
    private LinkedHashSet<Route> routes;
    private final LocalDateTime creationDate;

    // Возвращаем наш счетчик ID
    public long nextId = 1;

    public RouteCollection() {
        this.creationDate = LocalDateTime.now();
        this.routes = new LinkedHashSet<>();
    }

    public LinkedHashSet<Route> getRoute() {
        return routes;
    }

    public void setRoutes(LinkedHashSet<Route> routes) {
        this.routes = routes;
    }

    // Метод add теперь снова назначает ID
    public void add(Route route) {
        route.setId(nextId++);
        this.routes.add(route);
    }

    public void removeById(long id) {
        routes.removeIf(route -> route.getId() == id);
    }

    public void clear() {
        this.routes.clear();
        this.nextId = 1;
    }

    /**
     * НОВЫЙ И САМЫЙ ВАЖНЫЙ МЕТОД
     * находит максимальный ID в текущей коллекции и устанавливает
     * счетчик nextId в значение maxId + 1.
     */
    public void findAndSetNextId() {
        if (routes.isEmpty()) {
            this.nextId = 1;
            return;
        }

        // Находим максимальный ID с помощью Stream API
        long maxId = routes.stream()
                .mapToLong(Route::getId)
                .max()
                .orElse(0); // orElse(0) на случай, если все ID отрицательные (хотя их не будет)

        this.nextId = maxId + 1;
        System.out.println("Следующий доступный ID для новых элементов: " + this.nextId);
    }

    @Override
    public String toString() {
        return "RouteCollection{" +
                "creationDate=" + creationDate +
                ", type=" + routes.getClass().getName() +
                ", count=" + routes.size() +
                '}';
    }
}