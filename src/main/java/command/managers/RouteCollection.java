package command.managers;

import model.Route;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Класс RouteCollection представляет собой коллекцию маршрутов.
 * Он хранит информацию о времени создания коллекции и предоставляет методы для управления маршрутами.
 */

public class RouteCollection {

    private LinkedHashSet<Route> routes;
    private final LocalDateTime creationDate;
    public long nextId = 1; // Следующий доступный ID

    public RouteCollection() {
        this.creationDate = LocalDateTime.now();
        this.routes = new LinkedHashSet<>();
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public LinkedHashSet<Route> getRoute() {
        return (routes);
    }

    public void setRoutes(LinkedHashSet<Route> routes) {
        this.routes = routes;
    }

    public void add(Route route) {
        route.setId(nextId++);
        this.routes.add(route);
    }

    public void removeById(long id) {
        routes.removeIf(route -> route.getId() == id);
        reassignIds();
    }

    public void clear() {
        this.routes.clear();
        nextId = 1; // Сбрасываем ID при очистке
    }

    @Override
    public String toString() {
        return "RouteCollection{" +
                "creationDate=" + creationDate +
                ", type=" + routes.getClass().getName() +
                ", count=" + routes.size() +
                '}';
    }

    public void sortRouteCollection() {
        List<Route> routeList = new ArrayList<>(routes);
        Collections.sort(routeList);
        this.routes = new LinkedHashSet<>(routeList);
    }

    public Iterator<Route> getIterator() {
        return routes.iterator();
    }

    public void reassignIds() {
        nextId = 1;

        List<Route> routeList = new ArrayList<>(routes);
        routeList.sort(Comparator.comparingLong(Route::getId));
        LinkedHashSet<Route> newRoutes = new LinkedHashSet<>();
        for (Route route : routeList) {
            route.setId(nextId++);
            newRoutes.add(route);
        }
        routes = newRoutes;
    }



    /**
     * Управляет дублирующимися ID в коллекции маршрутов.
     * Если обнаружены дублирующиеся ID, они будут перегенерированы.
     */
    public void manageDHashSet() {
        nextId = 1;
        LinkedHashSet<Route> newRoutes = new LinkedHashSet<>();
        for (Route route : routes) {
            route.setId(nextId++);
            newRoutes.add(route);
        }
        routes = newRoutes;
    }
}
