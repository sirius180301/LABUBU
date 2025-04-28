package command.managers;

import model.Route;

import javax.xml.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Класс RouteCollection представляет собой коллекцию маршрутов.
 * Он хранит информацию о времени создания коллекции и предоставляет методы для управления маршрутами.
 */
@XmlRootElement(name = "routes") // Имя корневого элемента XML
//@XmlAccessorType(XmlAccessType.FIELD)
public class RouteCollection {
    //@XmlElement(name = "route")
    private LinkedHashSet<Route> routes; // Переименовано для ясности

    private final LocalDateTime creationDate;
    private final GeneraterID generatorID; // Исправлено название класса

    /**
     * Конструктор, который инициализирует коллекцию маршрутов и устанавливает дату создания.
     */
    public RouteCollection() {
        this.creationDate = LocalDateTime.now();
        this.generatorID = new GeneraterID();
        this.routes = new LinkedHashSet<>();
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public GeneraterID getGeneratorID() {
        return generatorID;
    }

    public LinkedHashSet<Route> getRoute() {
        return new LinkedHashSet<>(routes); // Возвращаем копию для защиты от изменений
    }

    public void setRoutes(LinkedHashSet<Route> routes) {
        this.routes = routes;
    }

    public void add(Route route) {
        this.routes.add(route);
    }

    public void removeById(long id) {
        this.routes.removeIf(route -> route.getId() == id);
    }

    public void clear() {
        this.routes.clear();
    }

    @Override
    public String toString() {
        return "RouteCollection{" +
                "creationDate=" + creationDate +
                ", type=" + routes.getClass().getName() +
                ", count=" + routes.size() +
                '}';
    }

    /**
     * Сортирует коллекцию маршрутов.
     */
    public void sortRouteCollection() {
        List<Route> routeList = new ArrayList<>(routes);
        Collections.sort(routeList);
        this.routes = new LinkedHashSet<>(routeList);
    }

    public Iterator<Route> getIterator() {
        return routes.iterator();
    }

    /**
     * Управляет дублирующимися ID в коллекции маршрутов.
     * Если обнаружены дублирующиеся ID, они будут перегенерированы.
     */
    public void manageDHashSet() {
        Set<Long> existingIds = new HashSet<>();
        List<Route> routesToRemove = new ArrayList<>();

        for (Route route : routes) {
            if (existingIds.contains(route.getId())) {
                System.err.println("Обнаружен дублирующийся ID: " + route.getId() + ". Перегенерирую...");
                routesToRemove.add(route);
            } else {
                existingIds.add(route.getId());
            }
        }

        // Удаляем дублирующиеся маршруты
        routesToRemove.forEach(routes::remove);

        for (Route routeToRemove : routesToRemove) {
            long newId = generatorID.generateId();
            Route newRoute = new Route(newId, routeToRemove.getName(),
                    routeToRemove.getCoordinates(),
                    routeToRemove.getFrom(),
                    routeToRemove.getTo(),
                    routeToRemove.getDistance());
            newRoute.setCreationDate(routeToRemove.getCreationDate()); // Сохраняем дату создания
            routes.add(newRoute);
            System.out.println("Перегенерирован ID для маршрута " + routeToRemove.getName() + ". Новый ID: " + newId);
        }
    }
}
