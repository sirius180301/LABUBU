package command.commands;

import command.RouteReader;
import command.base.Command;
import command.base.Enviroment;
import command.exeptions.CommandException;
import command.managers.RouteCollection;
import model.Coordinates;
import model.Location;
import model.Route;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;

public class UpdateCommand extends Command {

    private final RouteCollection routeCollection;
    private static final Set<String> VALID_FIELDS = Set.of("name", "coordinates", "from", "to");

    public UpdateCommand(RouteCollection routeCollection) {
        super("update");
        this.routeCollection = routeCollection;
    }

    @Override
    public void execute(Enviroment env, PrintStream out, InputStream in, String[] args) throws CommandException {
        // Создаем один Scanner для всей интерактивной сессии команды
        Scanner scanner = new Scanner(in);

        // --- 1. ИНТЕРАКТИВНЫЙ ВВОД ID ---
        long id;
        while (true) {
            try {
                out.print("Введите ID элемента, который хотите обновить: ");
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    out.println("ID не может быть пустым. Попробуйте еще раз.");
                    continue;
                }
                id = Long.parseLong(line);
                break;
            } catch (NumberFormatException e) {
                out.println("Неверный формат ID. Должно быть введено целое число.");
            } catch (NoSuchElementException e) {
                throw new CommandException("Ввод ID был прерван.");
            }
        }

        final long searchId = id;
        Route existingRoute = routeCollection.getRoute().stream()
                .filter(r -> r.getId() == searchId)
                .findFirst()
                .orElse(null);

        if (existingRoute == null) {
            throw new CommandException("Элемент с ID " + id + " не найден.");
        }
        if (!existingRoute.getUsername().equals(env.getCurrentUser())) {
            throw new CommandException("Вы можете изменять только свои элементы.");
        }

        out.println("\nОбновление элемента с ID " + id + ". Текущие значения:");
        out.println(existingRoute);

        // --- 2. ИНТЕРАКТИВНЫЙ ВЫБОР ПОЛЕЙ ДЛЯ ОБНОВЛЕНИЯ ---
        Set<String> fieldsToUpdate;
        while (true) {
            out.println("\nЧтобы изменить объект, введите названия характеристик через пробел.");
            out.println("Для изменения сразу всех характеристик введите 'all'.");
            out.print("Доступные поля: " + String.join(", ", VALID_FIELDS) + "\n> ");

            String fieldsLine = scanner.nextLine().trim();
            if (fieldsLine.isEmpty()) {
                out.println("Ввод не может быть пустым. Пожалуйста, повторите.");
                continue;
            }

            Set<String> inputFieldNames = new HashSet<>(Arrays.asList(fieldsLine.split("\\s+")));

            if (inputFieldNames.contains("all")) {
                fieldsToUpdate = new HashSet<>(VALID_FIELDS);
                break; // Выходим из цикла, поля выбраны
            }

            // Проверяем, что все введенные поля валидны
            List<String> invalidFields = inputFieldNames.stream()
                    .filter(field -> !VALID_FIELDS.contains(field))
                    .toList();

            if (!invalidFields.isEmpty()) {
                out.println("Ошибка: найдены неизвестные поля: " + String.join(", ", invalidFields));
                out.println("Пожалуйста, введите поля еще раз.");
            } else {
                fieldsToUpdate = inputFieldNames;
                break; // Выходим из цикла, поля выбраны и валидны
            }
        }

        // --- 3. ИНТЕРАКТИВНОЕ ОБНОВЛЕНИЕ ВЫБРАННЫХ ПОЛЕЙ ---
        boolean locationChanged = false;
        // Мы используем тот же самый 'scanner', который создали в начале
        for (String field : fieldsToUpdate) {
            out.println("\n--- Обновление поля '" + field + "' ---");
            switch (field) {
                case "name":
                    String newName;
                    do {
                        out.print("Введите новое имя маршрута: ");
                        newName = scanner.nextLine().trim();
                        if (newName.isEmpty()) {
                            out.println("Имя не может быть пустым.");
                        }
                    } while (newName.isEmpty());
                    existingRoute.setName(newName);
                    break;
                case "coordinates":
                    // RouteReader внутри создаст свой временный Scanner из потока 'in'
                    Coordinates newCoordinates = RouteReader.readCoordinates(in, out);
                    existingRoute.setCoordinates(newCoordinates);
                    break;
                case "from":
                    Location newFrom = RouteReader.readLocation(in, out, "from");
                    existingRoute.setFrom(newFrom);
                    locationChanged = true;
                    break;
                case "to":
                    Location newTo = RouteReader.readLocation(in, out, "to");
                    existingRoute.setTo(newTo);
                    locationChanged = true;
                    break;
            }
        }

        // --- 4. АВТОМАТИЧЕСКИЙ ПЕРЕСЧЕТ ПОЛЕЙ ---
        if (locationChanged) {
            Float newDistance = RouteReader.calculateDistance(existingRoute.getFrom(), existingRoute.getTo());
            existingRoute.setDistance(newDistance);
            out.println("\nПоле 'distance' было автоматически пересчитано: " + newDistance);
        }

        out.println("\nЭлемент с ID " + id + " успешно обновлен. Новые значения:");
        out.println(existingRoute);
        out.println("Выполните команду 'save', чтобы сохранить изменения в базе данных.");
    }

    @Override
    public String getHelp() {
        // Обновляем справку
        return "обновить характеристику элемента коллекции.";
    }

    public static void register(HashMap<String, Command> commandMap, RouteCollection routeCollection) {
        commandMap.put("update", new UpdateCommand(routeCollection));
    }
}