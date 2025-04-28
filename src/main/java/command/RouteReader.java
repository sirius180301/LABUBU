

package command;

import model.Coordinates;
import model.Location;
import model.Route;
import command.managers.RouteCollection;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

@XmlRootElement
public class RouteReader {

    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RESET = "\u001B[0m";

    public static Route readRoute(InputStream in, PrintStream out, RouteCollection routeCollection) {
        Scanner scanner = new Scanner(in);
        String name = null;
        Coordinates coordinates = null;
        Location from = null;
        Location to = null;

        while (name == null) {
            out.print("Введите имя маршрута: ");
            out.flush();
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                out.println(ANSI_RED + "Имя маршрута не может быть пустым. Пожалуйста, введите имя еще раз." + ANSI_RESET);
            } else {
                name = input;
            }
        }

        while (coordinates == null) {
            coordinates = readCoordinates(in, out);
        }

        while (from == null) {
            from = readLocation(in, out, "from");
        }

        while (to == null) {
            to = readLocation(in, out, "to");
        }

        Float distance = calculateDistance(from, to);
        long id = routeCollection.nextId;

        return new Route(id, name, coordinates, from, to, distance);
    }

    private static Coordinates readCoordinates(InputStream in, PrintStream out) {
        Scanner scanner = new Scanner(in);
        int x = 0;
        Double y = null;

        while (true) {
            try {
                out.print("Введите координату X: ");
                out.flush();
                x = Integer.parseInt(scanner.nextLine().trim());
                break;
            } catch (NumberFormatException e) {
                out.println(ANSI_RED + "Неверный формат координаты X. Пожалуйста, введите целое число." + ANSI_RESET);
            }
        }

        while (y == null) {
            try {
                out.print("Введите координату Y: ");
                out.flush();
                String yStr = scanner.nextLine().trim();
                if (yStr.isEmpty()) {
                    out.println(ANSI_RED + "Координата Y не может быть null." + ANSI_RESET);
                } else {
                    y = Double.parseDouble(yStr);
                }
            } catch (NumberFormatException e) {
                out.println(ANSI_RED + "Неверный формат координаты Y. Пожалуйста, введите число с плавающей точкой." + ANSI_RESET);
            }
            if (y == null) {
                out.println(ANSI_RED + "Координата Y не может быть null. Пожалуйста, введите координату еще раз." + ANSI_RESET);
            }
        }

        return new Coordinates(x, y);
    }

    private static Location readLocation(InputStream in, PrintStream out, String locationName) {
        long x = readLongCoordinate(in, out, "X", locationName);
        Double y = readDoubleCoordinate(in, out, "Y", locationName);
        Scanner scanner = new Scanner(in);
        int z = 0;

        while (true) {
            try {
                out.print("Введите координату Z для Location " + locationName + ": ");
                out.flush();
                z = Integer.parseInt(scanner.nextLine().trim());
                break;
            } catch (NumberFormatException e) {
                out.println(ANSI_RED + "Неверный формат координаты Z для Location " + locationName + ". Пожалуйста, введите целое число." + ANSI_RESET);
            }
        }

        return new Location(x, y, z);
    }

    private static long readLongCoordinate(InputStream in, PrintStream out, String coordinateName, String locationName) {
        Scanner scanner = new Scanner(in);
        long coordinate = 0;
        while (true) {
            try {
                out.print("Введите координату " + coordinateName + " для Location " + locationName + ": ");
                out.flush();
                coordinate = Long.parseLong(scanner.nextLine().trim());
                break;
            } catch (NumberFormatException e) {
                out.println(ANSI_RED + "Неверный формат координаты " + coordinateName + " для Location " + locationName + ". Пожалуйста, введите целое число." + ANSI_RESET);
            }
        }
        return coordinate;
    }

    private static Double readDoubleCoordinate(InputStream in, PrintStream out, String coordinateName, String locationName) {
        Scanner scanner = new Scanner(in);
        Double coordinate = null;
        while (coordinate == null) {
            try {
                out.print("Введите координату " + coordinateName + " для Location " + locationName + ": ");
                out.flush();
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    out.println(ANSI_RED + "Координата Y не может быть null." + ANSI_RESET);
                } else {
                    coordinate = Double.parseDouble(input);
                }
            } catch (NumberFormatException e) {
                out.println(ANSI_RED + "Неверный формат координаты " + coordinateName + " для Location " + locationName + ". Пожалуйста, введите число с плавающей точкой." + ANSI_RESET);
            }
            if (coordinate == null) {
                out.println(ANSI_RED + "Координата Y не может быть null. Пожалуйста, введите координату еще раз." + ANSI_RESET);
            }
        }
        return coordinate;
    }

    private static Float calculateDistance(Location from, Location to) {
        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        double dz = to.getZ() - from.getZ();
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
