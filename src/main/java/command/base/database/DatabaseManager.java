package command.base.database;

import command.managers.RouteCollection;
import model.Coordinates;
import model.Location;
import model.Route;
import java.sql.*;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DatabaseManager {
    private final String url;
    private final String user;
    private final String password;
    private final Lock connectionLock = new ReentrantLock();
    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found. Include it in your library path.");
            e.printStackTrace();
        }
    }

    public DatabaseManager(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
        initializeDatabase();
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    private void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Создание таблицы пользователей
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id SERIAL PRIMARY KEY, " +
                    "username VARCHAR(50) UNIQUE NOT NULL, " +
                    "password_hash VARCHAR(100) NOT NULL)");

            // Создание таблицы маршрутов
            stmt.execute("CREATE TABLE IF NOT EXISTS route (" +
                    "id BIGINT PRIMARY KEY DEFAULT nextval('route_id_seq'), " +
                    "name VARCHAR(100) NOT NULL, " +
                    "x_coord INTEGER NOT NULL, " +
                    "y_coord DOUBLE PRECISION NOT NULL, " +
                    "creation_date TIMESTAMP NOT NULL, " +
                    "from_x BIGINT NOT NULL, " +
                    "from_y DOUBLE PRECISION NOT NULL, " +
                    "from_z INTEGER NOT NULL, " +
                    "to_x BIGINT NOT NULL, " +
                    "to_y DOUBLE PRECISION NOT NULL, " +
                    "to_z INTEGER NOT NULL, " +
                    "distance FLOAT, " +
                    "username VARCHAR(50) NOT NULL REFERENCES users(username))");

            // Создание sequence для ID маршрутов
            stmt.execute("CREATE SEQUENCE IF NOT EXISTS route_id_seq START 1 INCREMENT 1");

        } catch (SQLException e) {
            System.err.println("Ошибка при инициализации базы данных: " + e.getMessage());
        }
    }

    public void loadCollection(RouteCollection routeCollection) throws SQLException {
        connectionLock.lock();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM route")) {

            LinkedHashSet<Route> routes = new LinkedHashSet<>();
            while (rs.next()) {
                Route route = new Route(
                        rs.getLong("id"),
                        rs.getString("name"),
                        new Coordinates(rs.getInt("x_coord"), rs.getDouble("y_coord")),
                        new Location(rs.getLong("from_x"), rs.getDouble("from_y"), rs.getInt("from_z")),
                        new Location(rs.getLong("to_x"), rs.getDouble("to_y"), rs.getInt("to_z")),
                        rs.getFloat("distance"),
                        rs.getString("username")
                );
                route.setCreationDate(rs.getTimestamp("creation_date").toLocalDateTime());
                routes.add(route);
            }
            routeCollection.setRoutes(routes);
            routeCollection.reassignIds();
        } finally {
            connectionLock.unlock();
        }
    }

    public long addRoute(Route route, String username) throws SQLException {
        connectionLock.lock();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO route (name, x_coord, y_coord, creation_date, from_x, from_y, from_z, to_x, to_y, to_z, distance, username) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id")) {


            stmt.setString(1, route.getName());
            stmt.setInt(2, route.getCoordinates().getX());
            stmt.setDouble(3, route.getCoordinates().getY());
            stmt.setTimestamp(4, Timestamp.valueOf(route.getCreationDate()));
            stmt.setLong(5, route.getFrom().getX());
            stmt.setDouble(6, route.getFrom().getY());
            stmt.setInt(7, route.getFrom().getZ());
            stmt.setLong(8, route.getTo().getX());
            stmt.setDouble(9, route.getTo().getY());
            stmt.setInt(10, route.getTo().getZ());
            stmt.setFloat(11, route.getDistance());
            stmt.setString(12, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
            throw new SQLException("Не удалось получить ID после вставки");
        } finally {
            connectionLock.unlock();
        }
    }

    public boolean removeRouteById(long id, String username) throws SQLException {
        connectionLock.lock();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM route WHERE id = ? AND username = ?")) {

            stmt.setLong(1, id);
            stmt.setString(2, username);
            return stmt.executeUpdate() > 0;
        } finally {
            connectionLock.unlock();
        }
    }

    public boolean updateRoute(Route route) throws SQLException {
        connectionLock.lock();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE route SET name = ?, x_coord = ?, y_coord = ?, from_x = ?, from_y = ?, from_z = ?, " +
                             "to_x = ?, to_y = ?, to_z = ?, distance = ? WHERE id = ? AND username = ?")) {

            stmt.setString(1, route.getName());
            stmt.setInt(2, route.getCoordinates().getX());
            stmt.setDouble(3, route.getCoordinates().getY());
            stmt.setLong(4, route.getFrom().getX());
            stmt.setDouble(5, route.getFrom().getY());
            stmt.setInt(6, route.getFrom().getZ());
            stmt.setLong(7, route.getTo().getX());
            stmt.setDouble(8, route.getTo().getY());
            stmt.setInt(9, route.getTo().getZ());
            stmt.setFloat(10, route.getDistance());
            stmt.setLong(11, route.getId());
            stmt.setString(12, route.getUsername());

            return stmt.executeUpdate() > 0;
        } finally {
            connectionLock.unlock();
        }
    }

    public void saveAllChanges() throws SQLException {
        // В нашей реализации изменения сохраняются сразу, поэтому этот метод может быть пустым
        // Или можно добавить логирование успешного сохранения
    }

    public List<Float> getSortedDistances() throws SQLException {
        connectionLock.lock();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT distance FROM route WHERE distance IS NOT NULL ORDER BY distance")) {

            List<Float> distances = new ArrayList<>();
            while (rs.next()) {
                distances.add(rs.getFloat("distance"));
            }
            return distances;
        } finally {
            connectionLock.unlock();
        }
    }
    public boolean testConnection() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            return stmt.execute("SELECT 1");
        } catch (SQLException e) {
            System.err.println("Ошибка подключения: " + e.getMessage());
            return false;
        }
    }
}
