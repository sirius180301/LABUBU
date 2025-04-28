package model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Objects;

@XmlRootElement(name = "distance")
@XmlType(propOrder = {"x"})
public class Distance {
    private int x;

    public Distance(){}
    public Distance(float x) {
        setX((int) x);
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
    @XmlElement
    public int getX() {
        return x;
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "x=" + x +
                '}';
    }


    /*@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return x == that.x && Objects.equals(y, that.y);
    }*/

    /**
     * Возвращает хэш-код объекта.
     *
     * @return хэш-код координат
     */
    @Override
    public int hashCode() {
        return Objects.hash(x);
    }
}



