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

    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RESET = "\u001B[0m";

    public static Route readRoute(InputStream in, PrintStream out, RouteCollection routeCollection) {
        Scanner scanner = new Scanner(in);
        String name = null;

        while (name == null) {
            try {
                out.print("Введите имя маршрута: ");
                out.flush();
                name = scanner.nextLine().trim();
                if (name.isEmpty()) {
                    out.println(ANSI_RED + "Имя маршрута не может быть пустым. Пожалуйста, введите имя маршрута" + ANSI_RESET);
                    name = null; // Reset name to prompt again
                }
            } catch (NoSuchElementException e) {
                out.println(ANSI_RED + "Ввод имени маршрута прерван." + ANSI_RESET);
                return null; // Or throw an exception, depending on desired behavior
            }
        }


        Coordinates coordinates = readCoordinates(in, out);
        Location from = readLocation(in, out, "from");
        Location to = readLocation(in, out, "to");

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
            out.print("Введите координату Y: ");
            out.flush();
            String yStr = scanner.nextLine().trim();

            if (yStr.isEmpty()) {
                out.println(ANSI_RED + "Координата Y не может быть null. Пожалуйста, введите координату еще раз." + ANSI_RESET);
            } else {
                try {
                    y = Double.parseDouble(yStr);
                } catch (NumberFormatException e) {
                    out.println(ANSI_RED + "Неверный формат координаты Y. Пожалуйста, введите число с плавающей точкой." + ANSI_RESET);


                }

            }
        }

        return new Coordinates(x, y);
    }


    private static Location readLocation(InputStream in, PrintStream out, String locationName) {
        long x = readLongCoordinate(in, out, "X", locationName);
        Double y = readDoubleCoordinate(in, out, "Y", locationName);
        int z = readIntCoordinate(in, out, "Z", locationName);

        return new Location(x, y, z);
    }

    private static Float calculateDistance(Location from, Location to) {
        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        double dz = to.getZ() - from.getZ();
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
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
                    out.println(ANSI_RED + "Координата " + coordinateName + " не может быть null. Пожалуйста, введите координату еще раз." + ANSI_RESET);
                } else {
                    coordinate = Double.parseDouble(input);
                }

            } catch (NumberFormatException e) {
                out.println(ANSI_RED + "Неверный формат координаты " + coordinateName + " для Location " + locationName + ". Пожалуйста, введите число с плавающей точкой." + ANSI_RESET);
            }
        }
        return coordinate;
    }

    private static int readIntCoordinate(InputStream in, PrintStream out, String coordinateName, String locationName) {
        Scanner scanner = new Scanner(in);
        int coordinate = 0;
        while (true) {
            try {
                out.print("Введите координату " + coordinateName + " для Location " + locationName + ": ");
                out.flush();
                coordinate = Integer.parseInt(scanner.nextLine().trim());
                break;
            } catch (NumberFormatException e) {
                out.println(ANSI_RED + "Неверный формат координаты " + coordinateName + " для Location " + locationName + ". Пожалуйста, введите целое число." + ANSI_RESET);
            }
        }
        return coordinate;
    }
}