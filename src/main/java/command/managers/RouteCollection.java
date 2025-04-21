package command.managers;

import model.Route;

import javax.xml.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@XmlRootElement(name = "routes") // Добавлено имя для XML
@XmlAccessorType(XmlAccessType.FIELD)
public class RouteCollection {
    @XmlElement(name = "route")
    private LinkedHashSet<Route> route;


    private final LocalDateTime creationDate;

    private final GeneraterID generaterID;

    public RouteCollection() {
        this.creationDate = LocalDateTime.now();
        this.generaterID = new GeneraterID();
        this.route = new LinkedHashSet<>();
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public GeneraterID getGeneraterID() {
        return generaterID;
    }

    public LinkedHashSet<Route> getRoute() {
        return (LinkedHashSet<Route>) route;
    }

    public void setRoute(LinkedHashSet<Route> route) {
        this.route = route;
    }


    public void add(Route route) {
        this.route.add(route);
    }

    public void removeById(long id) {
        this.route.removeIf(route -> route.getId() == id);
    }


    public void clear() {
        this.route.clear();
    }

    @Override
    public String toString() {
        return "RouteCollection{" +
                "creationDate=" + creationDate +
                ", type=" + route.getClass().getName() +
                ", count=" + route.size() +
                '}';
    }


    public void sortRouteCollection() {
        List<Route> routeList = new ArrayList<>(route);
        Collections.sort(routeList);
        this.route = new LinkedHashSet<>(routeList);
    }

    public Iterator<Route> getIterator() {
        return route.iterator();
    }

    public void manageDHashSet() {
        Set<Long> existingIds = new HashSet<>();
        List<Route> routesToRemove = new ArrayList<>();

        for (Route route : route) {
            if (existingIds.contains(route.getId())) {
                System.err.println("Обнаружен дублирующийся ID: " + route.getId() + ". Перегенерирую...");
                routesToRemove.add(route);
            } else {
                existingIds.add(route.getId());
            }
        }

        routesToRemove.forEach(route::remove);

        for (Route routeToRemove : routesToRemove) {
            long newId = generaterID.generateId();
            Route newRoute = new Route(newId, routeToRemove.getName(), routeToRemove.getCoordinates(), routeToRemove.getFrom(), routeToRemove.getTo(), routeToRemove.getDistance());
            newRoute.setCreationDate(routeToRemove.getCreationDate()); // Сохраняем дату создания
            route.add(newRoute);
            System.out.println("Перегенерирован ID для маршрута " + routeToRemove.getName() + ". Новый ID: " + newId);
        }
    }

}

