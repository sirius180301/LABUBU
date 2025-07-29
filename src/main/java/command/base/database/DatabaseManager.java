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
                    "name VARCHAR(50) UNIQUE NOT NULL, " +
                    "password VARCHAR(100) NOT NULL)");

            // Создание таблицы маршрутов
            stmt.execute("CREATE TABLE IF NOT EXISTS route (" +
                    "id BIGINT PRIMARY KEY, " + // ID теперь назначается в программе, а не в БД
                    "name VARCHAR(100) NOT NULL, " +
                    "x_coord INTEGER NOT NULL, " +
                    "y_coord DOUBLE PRECISION NOT NULL, " +
                    "from_x BIGINT NOT NULL, " +
                    "from_y DOUBLE PRECISION NOT NULL, " +
                    "from_z INTEGER NOT NULL, " +
                    "to_x BIGINT NOT NULL, " +
                    "to_y DOUBLE PRECISION NOT NULL, " +
                    "to_z INTEGER NOT NULL, " +
                    "distance FLOAT, " +
                    "creation_date TIMESTAMP NOT NULL,"+
                    "username VARCHAR(50) NOT NULL REFERENCES users(name))"); // ИСПРАВЛЕНО: ссылка на users(name)

            // Создание sequence для ID маршрутов (используется для генерации новых ID в памяти)
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

            LinkedHashSet<model.Route> routes = new LinkedHashSet<>();
            while (rs.next()) {
                // ИСПРАВЛЕНО: Вызываем новый, полный конструктор
                model.Route route = new model.Route(
                        rs.getLong("id"),
                        rs.getString("name"),
                        new model.Coordinates(rs.getInt("x_coord"), rs.getDouble("y_coord")),
                        rs.getTimestamp("creation_date").toLocalDateTime(),
                        new model.Location(rs.getLong("from_x"), rs.getDouble("from_y"), rs.getInt("from_z")),
                        new model.Location(rs.getLong("to_x"), rs.getDouble("to_y"), rs.getInt("to_z")),
                        rs.getFloat("distance"),
                        rs.getString("username")
                );
                routes.add(route);
            }

            routeCollection.setRoutes(routes);
            routeCollection.findAndSetNextId();

        } finally {
            connectionLock.unlock();
        }
    }

    /**
     * Загружает из базы данных маршруты, принадлежащие конкретному пользователю.
     * Необходим для команды logout для проверки несохраненных изменений.
     * @param username имя пользователя.
     * @return Множество (Set) маршрутов этого пользователя.
     * @throws SQLException в случае ошибки SQL.
     */
    public Set<model.Route> getUserRoutes(String username) throws SQLException {
        String sql = "SELECT * FROM route WHERE username = ?";
        Set<model.Route> userRoutes = new HashSet<>();

        connectionLock.lock();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    model.Route route = new model.Route(
                            rs.getLong("id"),
                            rs.getString("name"),
                            new model.Coordinates(rs.getInt("x_coord"), rs.getDouble("y_coord")),
                            rs.getTimestamp("creation_date").toLocalDateTime(),
                            new model.Location(rs.getLong("from_x"), rs.getDouble("from_y"), rs.getInt("from_z")),
                            new model.Location(rs.getLong("to_x"), rs.getDouble("to_y"), rs.getInt("to_z")),
                            rs.getFloat("distance"),
                            rs.getString("username")
                    );
                    userRoutes.add(route);
                }
            }
        } finally {
            connectionLock.unlock();
        }
        return userRoutes;
    }

    /**
     * Полностью синхронизирует маршруты для указанного пользователя.
     * Сначала удаляет все его старые маршруты, затем вставляет все маршруты из переданной коллекции.
     * Операция выполняется в одной транзакции для обеспечения целостности данных.
     */
    public void syncUserRoutes(String username, java.util.Collection<model.Route> routes) throws SQLException {
        String deleteSql = "DELETE FROM route WHERE username = ?";
        String insertSql = "INSERT INTO route (id, name, creation_date, x_coord, y_coord, from_x, from_y, from_z, to_x, to_y, to_z, distance, username) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        connectionLock.lock();
        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Начало транзакции

            // Шаг 1: Удаляем все старые маршруты пользователя
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setString(1, username);
                deleteStmt.executeUpdate();
            }

            // Шаг 2: Вставляем все маршруты из коллекции в памяти
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                for (model.Route route : routes) {
                    if (!username.equals(route.getUsername())) continue; // Вставляем только маршруты текущего пользователя

                    insertStmt.setLong(1, route.getId());
                    insertStmt.setString(2, route.getName());
                    insertStmt.setTimestamp(3, java.sql.Timestamp.valueOf(route.getCreationDate()));
                    insertStmt.setInt(4, route.getCoordinates().getX());
                    insertStmt.setDouble(5, route.getCoordinates().getY());
                    insertStmt.setLong(6, route.getFrom().getX());
                    insertStmt.setDouble(7, route.getFrom().getY());
                    insertStmt.setInt(8, route.getFrom().getZ());
                    insertStmt.setLong(9, route.getTo().getX());
                    insertStmt.setDouble(10, route.getTo().getY());
                    insertStmt.setInt(11, route.getTo().getZ());
                    insertStmt.setFloat(12, route.getDistance());
                    insertStmt.setString(13, route.getUsername());

                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
            }

            conn.commit(); // Подтверждение транзакции

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // Откат транзакции в случае ошибки
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
            connectionLock.unlock();
        }
    }

    // --- Вспомогательные методы (могут быть неактуальны при стратегии полной синхронизации, но не мешают) ---

    public boolean testConnection() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            return stmt.execute("SELECT 1");
        } catch (SQLException e) {
            System.err.println("Ошибка подключения: " + e.getMessage());
            return false;
        }
    }

    public boolean doesUserExist(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE name = ?";
        connectionLock.lock();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } finally {
            connectionLock.unlock();
        }
        return false;
    }

    public int countUsers() throws SQLException {
        String sql = "SELECT COUNT(*) FROM users";
        connectionLock.lock();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } finally {
            connectionLock.unlock();
        }
        return 0;
    }
}