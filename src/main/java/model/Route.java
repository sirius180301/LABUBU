
package model;

import java.time.LocalDateTime;
import java.util.Objects;



import command.managers.LocalDateTimeAdapter;

/**
 * Класс Route представляет маршрут с уникальным идентификатором, названием,
 * координатами, датой создания, начальной и конечной точками, а также расстоянием.
 * Реализует интерфейс Comparable для сравнения маршрутов по расстоянию.
 */

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
    private String username;

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
        this.username = username;
    }



    public Route(long id, String name, Coordinates coordinates, LocalDateTime creationDate, Location from, Location to, Float distance, String username) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "Имя не может быть null.");
        this.coordinates = Objects.requireNonNull(coordinates, "Координаты не могут быть null.");
        this.creationDate = Objects.requireNonNull(creationDate, "Дата создания не может быть null.");
        this.from = Objects.requireNonNull(from, "Местоположение отправления не может быть null.");
        this.to = Objects.requireNonNull(to, "Местоположение прибытия не может быть null.");
        this.distance = distance;
        this.username = Objects.requireNonNull(username, "Имя пользователя не может быть null.");
    }

    //public Route(long id, String name, Coordinates coordinates, Location from, Location to, Float distance, String username) {
   // }

    /**
     * Внутренний класс для построения маршрута (не используется в текущей реализации)
     */


    /**
     * Возвращает идентификатор маршрута
     * @return идентификатор маршрута
     */

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя пользователя (владельца) не может быть пустым.");
        }
        this.username = username;
    }

}
