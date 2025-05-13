package command.base.database;

import model.Coordinates;
import model.Location;
import model.Route;
import command.exeptions.CommandException;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.MessageDigest;
import java.sql.*;
import java.util.Base64;
import java.util.LinkedHashSet;

public class DatabaseHandler {

    private final String dbHost;
    private final String dbName;
    private final String dbUser;
    private final String dbPassword;
    private Connection connection;

    public DatabaseHandler(String dbHost, String dbName, String dbUser, String dbPassword) {
        this.dbHost = dbHost;
        this.dbName = dbName;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String url = "jdbc:postgresql://" + dbHost + "/" + dbName;
            connection = DriverManager.getConnection(url, dbUser, dbPassword);
        }
        return connection;
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public boolean registerUser(String username, String password) throws SQLException, NoSuchAlgorithmException {
        String salt = generateSalt();
        String hashedPassword = hashPassword(password, salt);

        String sql = "INSERT INTO users (username, hashed_password, salt) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, hashedPassword);
            preparedStatement.setString(3, salt);
            return preparedStatement.executeUpdate() > 0;
        }
    }

    public boolean checkCredentials(String username, String password) throws SQLException, NoSuchAlgorithmException {
        String sql = "SELECT hashed_password, salt FROM users WHERE username = ?";
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String hashedPasswordFromDB = resultSet.getString("hashed_password");
                    String salt = resultSet.getString("salt");
                    String hashedPassword = hashPassword(password, salt);

                    return hashedPasswordFromDB.equals(hashedPassword);
                } else {
                    return false; // User not found
                }
            }
        }
    }

    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    private String hashPassword(String password, String salt) throws NoSuchAlgorithmException {
        String saltedPassword = salt + password;
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] hashedBytes = md.digest(saltedPassword.getBytes());

        StringBuilder sb = new StringBuilder();
        for (byte b : hashedBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public LinkedHashSet<Route> loadRoutes() throws SQLException {
        LinkedHashSet<Route> routes = new LinkedHashSet<>();
        String sql = "SELECT * FROM routes";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                int coordX = resultSet.getInt("coord_x");
                Double coordY = resultSet.getDouble("coord_y");
                Coordinates coordinates = new Coordinates(coordX, coordY);

                long locationX = resultSet.getLong("location_x");
                Double locationY = resultSet.getDouble("location_y");
                int locationZ = resultSet.getInt("location_z");
                Location from = new Location(locationX, locationY, locationZ);

                long toX = resultSet.getLong("to_x");
                Double toY = resultSet.getDouble("to_y");
                int toZ = resultSet.getInt("to_z");
                Location to = new Location(toX, toY, toZ);

                Float distance = resultSet.getFloat("distance");
                String username = resultSet.getString("username");

                Route route = new Route(id, name, coordinates, from, to, distance, username);
                routes.add(route);
            }
        }
        return routes;
    }

    public long getNextId() throws SQLException {
        String sql = "SELECT nextval('routes_id_seq')";
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getLong(1);
            } else {
                throw new SQLException("Failed to retrieve next ID from sequence.");
            }
        }
    }

    public Route addRoute(Route route, String username) throws SQLException {
        String sql = "INSERT INTO routes (name, coord_x, coord_y, location_x, location_y, location_z, to_x, to_y, to_z, distance, username) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, route.getName());
            preparedStatement.setInt(2, route.getCoordinates().getX());
            preparedStatement.setDouble(3, route.getCoordinates().getY());
            preparedStatement.setLong(4, route.getFrom().getX());
            preparedStatement.setDouble(5, route.getFrom().getY());
            preparedStatement.setInt(6, route.getFrom().getZ());
            preparedStatement.setLong(7, route.getTo().getX());
            preparedStatement.setDouble(8, route.getTo().getY());
            preparedStatement.setInt(9, route.getTo().getZ());
            preparedStatement.setFloat(10, route.getDistance());
            preparedStatement.setString(11, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    long id = resultSet.getLong("id");
                    route.setId(id); // Set the generated ID to the route object
                    return route; // Return the route object with the ID
                } else {
                    throw new SQLException("Failed to insert route into the database.");
                }
            }
        }
    }

    public boolean updateRoute(Route route, String username) throws SQLException {
        String sql = "UPDATE routes SET name = ?, coord_x = ?, coord_y = ?, location_x = ?, location_y = ?, location_z = ?, to_x = ?, to_y = ?, to_z = ?, distance = ? WHERE id = ? AND username = ?";
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, route.getName());
            preparedStatement.setInt(2, route.getCoordinates().getX());
            preparedStatement.setDouble(3, route.getCoordinates().getY());
            preparedStatement.setLong(4, route.getFrom().getX());
            preparedStatement.setDouble(5, route.getFrom().getY());
            preparedStatement.setInt(6, route.getFrom().getZ());
            preparedStatement.setLong(7, route.getTo().getX());
            preparedStatement.setDouble(8, route.getTo().getY());
            preparedStatement.setInt(9, route.getTo().getZ());
            preparedStatement.setFloat(10, route.getDistance());
            preparedStatement.setLong(11, route.getId());
            preparedStatement.setString(12, username);

            int rowsUpdated = preparedStatement.executeUpdate();
            return rowsUpdated > 0;
        }
    }

    public boolean removeRouteById(long id, String username) throws SQLException {
        String sql = "DELETE FROM routes WHERE id = ? AND username = ?";
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            preparedStatement.setString(2, username);
            int rowsDeleted = preparedStatement.executeUpdate();
            return rowsDeleted > 0;
        }
    }

    public boolean clearRoutes(String username) throws SQLException {
        String sql = "DELETE FROM routes WHERE username = ?";
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            int rowsDeleted = preparedStatement.executeUpdate();
            return rowsDeleted > 0;
        }
    }
}