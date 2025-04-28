
package model;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import command.managers.LocalDateTimeAdapter;

/**
 * Класс Route представляет маршрут с уникальным идентификатором, названием,
 * координатами, датой создания, начальной и конечной точками, а также расстоянием.
 * Реализует интерфейс Comparable для сравнения маршрутов по расстоянию.
 */
@XmlRootElement(name = "route")
@XmlType(propOrder = {"id", "name", "coordinates", "creationDate", "from", "to", "distance"})
public class Route implements Comparable<Route> {

    /**
     * Уникальный идентификатор маршрута (должен быть больше 0)
     */
    private long id;

    /** Название маршрута (не может быть null или пустым) */
    private String name;

    /** Координаты маршрута (не могут быть null) */
    private Coordinates coordinates;

    /** Дата создания маршрута (генерируется автоматически) */
    private LocalDateTime creationDate;

    /** Начальная точка маршрута (не может быть null) */
    private Location from;

    /** Конечная точка маршрута (не может быть null) */
    private Location to;

    /** Расстояние маршрута (может быть null) */
    private Float distance;

    /**
     * Приватный конструктор без параметров (требуется для JAXB)
     */
    private Route() {
    }

    /**
     * Основной конструктор для создания маршрута
     * @param id уникальный идентификатор
     * @param name название маршрута
     * @param coordinates координаты
     * @param from начальная точка
     * @param to конечная точка
     * @param distance расстояние
     * @throws NullPointerException если name, coordinates, from или to равны null
     */
    public Route(long id, String name, Coordinates coordinates, Location from, Location to, Float distance) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "Имя не может быть null.");
        this.coordinates = Objects.requireNonNull(coordinates, "Координаты не могут быть null.");
        this.creationDate = LocalDateTime.now();
        this.from = Objects.requireNonNull(from, "Местоположение отправления не может быть null.");
        this.to = Objects.requireNonNull(to, "Местоположение прибытия не может быть null.");
        this.distance = distance;
    }

    /**
     * Внутренний класс для построения маршрута (не используется в текущей реализации)
     */
    protected static class RoutebBlder {
        private static Location from;
        private static Location to;
    }

    /**
     * Возвращает идентификатор маршрута
     * @return идентификатор маршрута
     */
    @XmlElement(name = "id")
    public long getId() {
        return id;
    }

    /**
     * Устанавливает идентификатор маршрута
     * @param id новый идентификатор
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Возвращает название маршрута
     * @return название маршрута
     */
    @XmlElement(name = "name")
    public String getName() {
        return name;
    }

    /**
     * Устанавливает название маршрута
     * @param name новое название
     * @throws NullPointerException если name равен null
     */
    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "Имя не может быть null.");
    }

    /**
     * Возвращает координаты маршрута
     * @return объект Coordinates
     */
    @XmlElement(name = "coordinates")
    public Coordinates getCoordinates() {
        return coordinates;
    }

    /**
     * Устанавливает координаты маршрута
     * @param coordinates новые координаты
     * @throws NullPointerException если coordinates равен null
     */
    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = Objects.requireNonNull(coordinates, "Координаты не могут быть null.");
    }

    /**
     * Возвращает дату создания маршрута
     * @return дата создания
     */
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    /**
     * Устанавливает дату создания маршрута
     * @param creationDate новая дата создания
     */
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Возвращает начальную точку маршрута
     * @return объект Location
     */
    @XmlElement(name = "from")
    public Location getFrom() {
        return from;
    }

    /**
     * Устанавливает начальную точку маршрута
     * @param from новая начальная точка
     * @throws NullPointerException если from равен null
     */
    public void setFrom(Location from) {
        this.from = Objects.requireNonNull(from, "Местоположение отправления не может быть null.");
    }

    /**
     * Возвращает конечную точку маршрута
     * @return объект Location
     */
    @XmlElement(name = "to")
    public Location getTo() {
        return to;
    }

    /**
     * Устанавливает конечную точку маршрута
     * @param to новая конечная точка
     * @throws NullPointerException если to равен null
     */
    public void setTo(Location to) {
        this.to = Objects.requireNonNull(to, "Местоположение прибытия не может быть null.");
    }

    /**
     * Возвращает расстояние маршрута
     * @return расстояние (может быть null)
     */
    @XmlElement(name = "distance")
    public Float getDistance() {
        return distance;
    }

    /**
     * Устанавливает расстояние маршрута
     * @param distance новое расстояние
     */
    public void setDistance(Float distance) {
        this.distance = distance;
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
            return -1;
        } else if (o.getDistance() == null) {
            return 1;
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
