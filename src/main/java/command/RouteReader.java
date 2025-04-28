package command;

import model.Coordinates;
import model.Location;
import model.Route;
import command.managers.RouteCollection;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Класс для чтения данных о маршруте из входного потока.
 * Обеспечивает интерактивное создание объектов Route через консольный ввод.
 */
@XmlRootElement
public class RouteReader {

    /**
     * Читает данные маршрута из входного потока и создает объект Route.
     *
     * @param in              входной поток для чтения данных
     * @param out             выходной поток для вывода подсказок
     * @param routeCollection коллекция маршрутов для генерации ID
     * @return новый объект Route
     * @throws NoSuchElementException если введены некорректные данные
     */

    public static Route readRoute(InputStream in, PrintStream out, RouteCollection routeCollection)
            throws NoSuchElementException {
        Scanner scanner = new Scanner(in);

        out.print("Введите имя маршрута: ");
        out.flush();
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            throw new NoSuchElementException("Имя маршрута не может быть пустым.");
        }

        Coordinates coordinates = readCoordinates(in, out);
        Location from = readLocation(in, out, "from");
        Location to = readLocation(in, out, "to");

        Float distance = calculateDistance(from, to);

        long id = routeCollection.getGeneratorID().generateId();
        return new Route(id, name, coordinates, from, to, distance);
    }

    /**
     * Читает координаты из входного потока
     *
     * @param in  входной поток
     * @param out выходной поток для подсказок
     * @return объект Coordinates
     * @throws NoSuchElementException если введены некорректные данные
     */

    private static Coordinates readCoordinates(InputStream in, PrintStream out)
            throws NoSuchElementException {
        Scanner scanner = new Scanner(in);

        out.print("Введите координату X: ");
        out.flush();
        int x;
        try {
            x = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            throw new NoSuchElementException("Неверный формат координаты X.");
        }

        out.print("Введите координату Y: ");
        out.flush();
        Double y;
        try {
            String yStr = scanner.nextLine().trim();
            if (yStr.isEmpty()) {
                y = null;
            } else {
                y = Double.parseDouble(yStr);
            }
        } catch (NumberFormatException e) {
            throw new NoSuchElementException("Неверный формат координаты Y.");
        }
        if (y == null) {
            throw new NoSuchElementException("Координата Y не может быть null.");
        }

        return new Coordinates(x, y);
    }

    /**
     * Читает данные локации из входного потока.
     *
     * @param in           входной поток
     * @param out          выходной поток для подсказок
     * @param locationName название локации(from/to)
     * @return объект Location
     * @throws NoSuchElementException если введены некорректные данные
     */

    private static Location readLocation(InputStream in, PrintStream out, String locationName)
            throws NoSuchElementException {
        Scanner scanner = new Scanner(in);

        long x = readLongCoordinate(in, out, "X", locationName);
        Double y = readDoubleCoordinate(in, out, "Y", locationName);
        out.print("Введите координату Z для Location " + locationName + ": ");
        out.flush();
        int z;
        try {
            z = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            throw new NoSuchElementException("Неверный формат координаты Z для Location " + locationName + ".");
        }

        return new Location(x, y, z);
    }

    /**
     * Читает координату типа long из входного потока.
     *
     * @param in             входной поток
     * @param out            выходной поток для подсказок
     * @param coordinateName название координаты(X/Y/Z)
     * @param locationName   название локации(from/to)
     * @return значение координаты
     * @throws NoSuchElementException если введены некорректные данные
     */

    private static long readLongCoordinate(InputStream in, PrintStream out,
                                           String coordinateName, String locationName)
            throws NoSuchElementException {
        Scanner scanner = new Scanner(in);
        out.print("Введите координату " + coordinateName + " для Location " + locationName + ": ");
        out.flush();
        try {
            return Long.parseLong(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            throw new NoSuchElementException("Неверный формат координаты " + coordinateName +
                    " для Location " + locationName + ".");
        }
    }

    /**
     * Читает координату типа Double из входного потока
     *
     * @param in             входной поток
     * @param out            выходной поток для подсказок
     * @param coordinateName название координаты(X/Y/Z)
     * @param locationName   название локации(from/to)
     * @return значение координаты
     * @throws NoSuchElementException если введены некорректные данные
     */

    private static Double readDoubleCoordinate(InputStream in, PrintStream out,
                                               String coordinateName, String locationName)
    throws NoSuchElementException {
        Scanner scanner = new Scanner(in);
        out.print("Введите координату " + coordinateName + " для Location " + locationName + ": ");
        out.flush();
        try {
            String yStr = scanner.nextLine().trim();
            if (yStr.isEmpty()) {
                return null;
            } else {
                return Double.parseDouble(yStr);
            }
        } catch (NumberFormatException e) {
            throw new NoSuchElementException("Неверный формат координаты " + coordinateName +
                    " для Location " + locationName + ".");
        }
    }

    /**
     * Вычисляет расстояние между двумя локациями.
     * @param from начальная локация
     * @param to   конечная локация
     * @return расстояние между локациями
     */
    private static Float calculateDistance(Location from, Location to) {
        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        double dz = to.getZ() - from.getZ();
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
