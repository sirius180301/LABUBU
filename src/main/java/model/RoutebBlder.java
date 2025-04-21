package model;

import javax.xml.bind.annotation.XmlRootElement;

public class RoutebBlder {

    public static class route {

        private static long id;
        private static String name;
        private static Coordinates coordinates;
        private static Location from;
        private static Location to;
        private static Float distance;


        public route(long id, String name, Coordinates coordinates, Location from, Location to, Float distance) {
            route.id = id;
            route.name = name;
            route.coordinates = coordinates;
            route.from = from;
            route.to = to;
            route.distance = distance;
        }

        public static Route build() {
            return new Route(id, name, coordinates, from, to, distance);
        }
    }
}

