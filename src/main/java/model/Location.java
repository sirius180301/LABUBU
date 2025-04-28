package model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Objects;

/**
 * Класс Location представляет географическое местоположение
 * с координатами x (долгота), y (широта) и z (высота).
 * Координата y не может быть null.
 */
@XmlRootElement(name = "location")
@XmlType(propOrder = {"x", "y", "z"})
public class Location {

    /**
     * Координата x (долгота)
     */
    private long x;

    /**
     * Координата y (широта, не может быть null)
     */
    private Double y;

    /**
     * Координата z (высота)
     */
    private int z;

    /**
     * Создает новый объект Location с указанными координатами
     *
     * @param x координата x (долгота)
     * @param y координата y (широта, не может быть null)
     * @param z координата z (высота)
     * @throws NullPointerException если координата y равна null
     */
    public Location() {
    }

    public Location(long x, Double y, int z) {
        setX(x);
        setY(y);
        setZ(z);
    }

    /**
     * Возвращает координату x (долготу)
     *
     * @return координата x
     */
    @XmlElement
    public long getX() {
        return x;
    }

    /**
     * Устанавливает координату x (долготу)
     *
     * @param x новая координата x
     */
    public void setX(long x) {
        this.x = x;
    }

    /**
     * Возвращает координату y (широту)
     *
     * @return координата y
     */
    @XmlElement
    public Double getY() {
        return y;
    }

    /**
     * Устанавливает координату y (широту)
     *
     * @param y новая координата y
     * @throws NullPointerException если координата y равна null
     */
    public void setY(Double y) {
        this.y = Objects.requireNonNull(y, "Координата y не может быть null.");
    }

    /**
     * Возвращает координату z (высоту)
     *
     * @return координата z
     */
    @XmlElement
    public int getZ() {
        return z;
    }

    /**
     * Устанавливает координату z (высоту)
     *
     * @param z новая координата z
     */
    public void setZ(int z) {
        this.z = z;
    }

    /**
     * Возвращает строковое представление объекта Location
     *
     * @return строковое представление в формате "Location{x=..., y=..., z=...}"
     */
    @Override
    public String toString() {
        return "Location{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}