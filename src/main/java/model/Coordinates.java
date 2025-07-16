package model;


import java.util.Objects;

/**
 * Класс, представляющий координаты в двумерном пространстве.
 * Используется для хранения координат маршрута.
 * Поддерживает сериализацию в XML через JAXB.
 */

public class Coordinates {

    /**
     * Координата по оси X
     */
    private int x;

    /**
     * Координата по оси Y (не может быть null)
     */
    private Double y;

    /**
     * Конструктор класса Coordinates.
     *
     *
     * @throws NullPointerException если параметр y равен null
     */
    public Coordinates(){}
    public Coordinates(int x, Double y) {
        setX(x);
        setY(y);
    }

    /**
     * Устанавливает координату X.
     *
     * @param x значение координаты X
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Возвращает координату X.
     *
     * @return координата X
     */

    public int getX() {
        return x;
    }

    /**
     * Устанавливает координату Y.
     *
     * @param y значение координаты Y (не может быть null)
     * @throws NullPointerException если параметр y равен null
     */
    public void setY(Double y) {
        this.y = Objects.requireNonNull(y, "Координата y не может быть null.");
    }

    /**
     * Возвращает координату Y.
     *
     * @return координата Y
     */

    public Double getY() {
        return y;
    }

    /**
     * Возвращает строковое представление объекта Coordinates.
     *
     * @return строковое представление координат
     */
    @Override
    public String toString() {
        return "Coordinates{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    /**
     * Сравнивает данный объект Coordinates с другим объектом.
     *
     * @param o объект для сравнения
     * @return true если объекты равны, false в противном случае
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return x == that.x && Objects.equals(y, that.y);
    }

    /**
     * Возвращает хэш-код объекта.
     *
     * @return хэш-код координат
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
