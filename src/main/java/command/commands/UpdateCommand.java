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
import java.util.stream.Collectors;

public class UpdateCommand extends Command {

    private final RouteCollection routeCollection;
    private static final Set<String> VALID_FIELDS = Set.of("name", "coordinates", "from", "to");

    public UpdateCommand(RouteCollection routeCollection) {
        super("update");
        this.routeCollection = routeCollection;
    }

    @Override
    public void execute(Enviroment env, PrintStream out, InputStream in, String[] args) throws CommandException {
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

        // --- ИСПРАВЛЕНИЕ ЗДЕСЬ ---
        // Создаем новую "эффективно финальную" переменную для использования в лямбде.
        final long searchId = id;

        // --- 2. ПОИСК ЭЛЕМЕНТА И ПРОВЕРКА ПРАВ ---
        Route existingRoute = routeCollection.getRoute().stream()
                .filter(r -> r.getId() == searchId) // Используем новую переменную
                .findFirst()
                .orElse(null);

        if (existingRoute == null) {
            throw new CommandException("Элемент с ID " + id + " не найден.");
        }

        if (!existingRoute.getUsername().equals(env.getCurrentUser())) {
            throw new CommandException("Вы можете изменять только свои элементы.");
        }

        out.println("Обновление элемента с ID " + id + ". Текущие значения:");
        out.println(existingRoute);
        //out.println("\nДоступные для обновления характеристики: name, coordinates, from, to. ");
        out.println("\n Чтобы изменить объект, введите название характеристики, что вы хотите изменить. Для изменения сразу всех характеристик введите 'all'");


        // --- 3. ОПРЕДЕЛЕНИЕ ПОЛЕЙ ДЛЯ ОБНОВЛЕНИЯ (ИЗ АРГУМЕНТОВ КОМАНДЫ) ---
        Set<String> fieldsToUpdate;
        if (args.length > 0) {
            fieldsToUpdate = Arrays.stream(args).collect(Collectors.toSet());
            if (fieldsToUpdate.contains("all")) {
                fieldsToUpdate = VALID_FIELDS;
            } else {
                List<String> invalidFields = fieldsToUpdate.stream()
                        .filter(field -> !VALID_FIELDS.contains(field))
                        .toList();
                if (!invalidFields.isEmpty()) {
                    throw new CommandException("Неизвестные поля: " + String.join(", ", invalidFields));
                }
            }
        } else {
            fieldsToUpdate = VALID_FIELDS;
        }

        // --- 4. ИНТЕРАКТИВНОЕ ОБНОВЛЕНИЕ ПОЛЕЙ ---
        boolean locationChanged = false;

        for (String field : fieldsToUpdate) {
            out.println("\n Обновление поля '" + field + "'");
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

        // --- 5. АВТОМАТИЧЕСКИЙ ПЕРЕСЧЕТ ПОЛЕЙ ---
        if (locationChanged) {
            Float newDistance = RouteReader.calculateDistance(existingRoute.getFrom(), existingRoute.getTo());
            existingRoute.setDistance(newDistance);
           // out.println("\nПоле 'distance' было автоматически пересчитано: " + newDistance);
        }

        out.println("\nЭлемент с ID " + id + " успешно обновлен. Новые значения:");
        out.println(existingRoute);
        out.println("Выполните команду 'save', чтобы сохранить изменения в базе данных.");
    }

    @Override
    public String getHelp() {
        return "обновить характеристики элемента коллекции";
    }

    public static void register(HashMap<String, Command> commandMap, RouteCollection routeCollection) {
        commandMap.put("update", new UpdateCommand(routeCollection));
    }
}