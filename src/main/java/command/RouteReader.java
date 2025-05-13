

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

@XmlRootElement
public class RouteReader {

    public static Route readRoute(InputStream in, PrintStream out, RouteCollection routeCollection, String username)
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
        long id = routeCollection.nextId;

        return new Route(id, name, coordinates, from, to, distance, username);
    }


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

    private static Float calculateDistance(Location from, Location to) {
        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        double dz = to.getZ() - from.getZ();
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
