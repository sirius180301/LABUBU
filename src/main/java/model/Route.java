package model;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import command.managers.LocalDateTimeAdapter;


@XmlRootElement(name = "route")
@XmlType(propOrder = {"id", "name", "coordinates", "creationDate", "from", "to", "distance"})
public class Route implements Comparable<Route> {

    private long id;

    private String name;
    private Coordinates coordinates;
    private LocalDateTime creationDate;
    private Location from;
    private Location to;
    private Float distance;

    private Route() {
    }

    public Route(long id, String name, Coordinates coordinates, Location from, Location to, Float distance) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "Имя не может быть null.");
        this.coordinates = Objects.requireNonNull(coordinates, "Координаты не могут быть null.");
        this.creationDate = LocalDateTime.now(); // Автоматически генерируем дату создания
        this.from = Objects.requireNonNull(from, "Местоположение отправления не может быть null.");
        this.to = Objects.requireNonNull(to, "Местоположение прибытия не может быть null.");
        this.distance = distance;
    }

    protected static class RoutebBlder {
        private static Location from;
        private static Location to;


    }

    @XmlElement(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @XmlElement(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "Имя не может быть null.");
    }

    // @XmlElement(name = "coordinates")
    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = Objects.requireNonNull(coordinates, "Координаты не могут быть null.");
    }


    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }


    @XmlElement(name = "from")
    public Location getFrom() {
        return from;
    }

    public void setFrom(Location from) {
        this.from = Objects.requireNonNull(from, "Местоположение отправления не может быть null.");
    }

    @XmlElement(name = "to")
    public Location getTo() {
        return to;
    }

    public void setTo(Location to) {
        this.to = Objects.requireNonNull(to, "Местоположение прибытия не может быть null.");
    }

    @XmlElement(name = "distance")
    public Float getDistance() {
        return distance;
    }


    @Override
    public String toString() {
        return "Route{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", from=" + from +
                ", to=" + to +
                ", distance=" + distance +
                '}';
    }


    @Override
    public int compareTo(Route o) {
        if (this.distance == null && o.getDistance() == null) {
            return 0;
        } else if (this.distance == null) {
            return -1; // null считается меньше
        } else if (o.getDistance() == null) {
            return 1;  // null считается меньше
        }
        return this.distance.compareTo(o.getDistance());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route route = (Route) o;
        return id == route.id;
    }


    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
