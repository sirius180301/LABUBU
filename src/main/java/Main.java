import command.base.Command;
import command.base.Enviroment;
import command.base.database.DatabaseManager;
import command.base.database.UserAuthenticator;
import command.commands.*;
import command.managers.RouteCollection;

import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    private static String connectionString = "jdbc:postgresql://pg:2222/studs?user=s465878&password=0pKaKsNIQxLhWHSt";
    private static String username = "s465878";
    private static String password = "0pKaKsNIQxLhWHSt";
    private static String db = "studs";
    private static int port = 2222;
    private static String host = "pg";

    private static DatabaseManager dbManager;
    private static RouteCollection routeCollection;
    private static Enviroment env;
    private static final Lock collectionLock = new ReentrantLock();
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RESET = "\u001B[0m";

    public static void main(String[] args) {
        try {
            for(String arg : args) {
                String parameterName = arg.substring(0, arg.indexOf("="));
                if(parameterName.equals("host")) {
                    host = arg.substring(arg.indexOf("=") + 1);
                }
                if(parameterName.equals("port")) {
                    port = Integer.parseInt(arg.substring(arg.indexOf("=") + 1));
                }
                if(parameterName.equals("db")) {
                    db = arg.substring(arg.indexOf("=") + 1);;
                }
                if(parameterName.equals("user")) {
                    username = arg;
                }
                if(parameterName.equals("password")) {
                    password = arg;
                }
            }
            connectionString = "jdbc:postgresql://" + host + ":" + port + "/" + db;
        } catch (Exception e) {
            System.err.println("Ошибка запуска приложения: " + e.getMessage());
        }

        try (Scanner scanner = new Scanner(System.in)) {
            // Шаг 1: Получение параметров подключения к БД
            System.out.println("=== Настройка подключения к PostgreSQL ===");
            String dbUrl = getDbUrl(scanner);
            String dbUser = getInput(scanner, "Имя пользователя PostgreSQL: ", false);
            String dbPassword = getInput(scanner, "Пароль PostgreSQL: ", true);

            // Шаг 2: Инициализация подключения
            initializeDatabase(scanner, dbUrl, dbUser, dbPassword);

            // Шаг 3: Инициализация аутентификатора пользователей
            UserAuthenticator userAuthenticator = new UserAuthenticator(dbManager);

            // Шаг 4: Регистрация команд
            HashMap<String, Command> commands = new HashMap<>();
            registerCommands(commands, routeCollection, collectionLock, dbManager, userAuthenticator);

            // Шаг 5: Создание окружения
            env = new Enviroment(commands);

            // Шаг 6: Запуск основного цикла
            System.out.println("\n=== Route Manager ===");
            System.out.println("Доступные команды: help, register, login, ...");
            runCommandLoop(scanner);

        } catch (Exception e) {
            System.err.println(ANSI_RED + "Фатальная ошибка: " + e.getMessage() + ANSI_RESET);
            e.printStackTrace();
        }
    }

    private static String getDbUrl(Scanner scanner) {
        //System.out.print("URL подключения к БД [по умолчанию jdbc:postgresql://localhost:5432/route]: ");
        String input =connectionString;
        return input.isEmpty() ? "jdbc:postgresql://localhost:5432/route" : input;
    }

    private static String getInput(Scanner scanner, String prompt, boolean isPassword) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }


    private static void initializeDatabase(Scanner scanner, String dbUrl, String dbUser, String dbPassword)
            throws Exception {
        while (true) {
            try {
                System.out.println("\nПопытка подключения к БД...");
                dbManager = new DatabaseManager(dbUrl, dbUser, dbPassword);

                // Проверка подключения
                 if (!dbManager.testConnection()) {
                    throw new Exception("Не удалось подключиться к БД");
                  }

                // Загрузка коллекции
                routeCollection = new RouteCollection();
                dbManager.loadCollection(routeCollection);
                System.out.println("Успешное подключение к БД!");
                break;

            } catch (Exception e) {
                System.err.println(ANSI_RED + "Ошибка подключения: " + e.getMessage() + ANSI_RESET);
                System.out.println("Проверьте параметры подключения и повторите попытку");

                System.out.print("Хотите изменить параметры подключения? (y/n): ");
                if (!scanner.nextLine().trim().equalsIgnoreCase("y")) {
                    throw new Exception("Не удалось подключиться к БД. Приложение завершает работу.");
                }

                // Повторный ввод параметров
                dbUrl = getDbUrl(scanner);
                dbUser = getInput(scanner, "Имя пользователя PostgreSQL: ", false);
                dbPassword = getInput(scanner, "Пароль PostgreSQL: ", true);
            }
        }
    }

    public static void registerCommands(HashMap<String, Command> commands,
                                        RouteCollection routeCollection,
                                        Lock collectionLock,
                                        DatabaseManager dbManager,
                                        UserAuthenticator userAuthenticator) {
        HelpCommand.register(commands);
        InfoCommand.register(commands, routeCollection);
        ShowCommand.register(commands, routeCollection);
        AddCommand.register(commands, routeCollection, collectionLock, dbManager);
        UpdateCommand.register(commands, routeCollection, dbManager);
        RemoveByIdCommand.register(commands, routeCollection, collectionLock, dbManager);
        ClearCommand.register(commands, routeCollection);
        ExitCommand.register(commands);
        AddIfMaxCommand.register(commands, routeCollection, collectionLock, dbManager);
        AddIfMinCommand.register(commands, routeCollection, collectionLock, dbManager);
        RemoveLowerCommand.register(commands, routeCollection, collectionLock, dbManager);
        MinByCreationDateCommand.register(commands, routeCollection);
        CountLessThanDistanceCommand.register(commands, routeCollection);
        PrintFieldAscendingDistanceCommand.register(commands, routeCollection, dbManager);
        RegisterCommand.register(commands, userAuthenticator);
        LoginCommand.register(commands, userAuthenticator);
        SaveCommand.register(commands, dbManager);
        ExecuteScriptCommand.register(commands);
    }

    private static void runCommandLoop(Scanner scanner) {
        while (true) {
            try {
                System.out.print(env.getCurrentUser() != null ?
                        env.getCurrentUser() + "> " : "> ");

                String input = scanner.nextLine().trim();
                if (input.isEmpty()) continue;

                String[] parts = input.split("\\s+");
                String commandName = parts[0];
                String[] args = new String[parts.length - 1];
                System.arraycopy(parts, 1, args, 0, args.length);


                // Проверка авторизации для команд, кроме login и register
                if (!commandName.equals("login") && !commandName.equals("register") &&
                        env.getCurrentUser() == null) {
                    System.out.println(ANSI_RED + "Ошибка: необходимо авторизоваться (команды login или register)"
                            + ANSI_RESET);
                    continue;
                }

                Command cmd = env.getCommands().get(commandName);
                if (cmd == null) {
                    System.out.println("Неизвестная команда. Введите 'help' для списка команд.");
                    continue;
                }

                cmd.execute(env, System.out, System.in, args);

            } catch (Exception e) {
                System.err.println(ANSI_RED + "Ошибка: " + e.getMessage() + ANSI_RESET);
            }
        }
    }
}
