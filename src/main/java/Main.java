

import command.base.Command;
import command.base.Enviroment;
import command.commands.*;
import command.exeptions.CommandException;
import command.managers.RouteCollection;
import model.Coordinates;
import model.Location;
import model.Route;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Scanner;



public class Main {

    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RESET = "\u001B[0m";

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        HashMap<String, Command> map = new HashMap<>();
        RouteCollection routeCollection = new RouteCollection();


        HelpCommand.register(map);
        InfoCommand.register(map, routeCollection);
        ShowCommand.register(map, routeCollection);
        AddCommand.register(map, routeCollection);
        UpdateCommand.register(map, routeCollection);
        RemoveByIdCommand.register(map, routeCollection);
        ClearCommand.register(map, routeCollection);
        SaveCommand.register(map, routeCollection);  // Зарегистрирована команда save
        ExecuteScriptCommand.register(map, routeCollection);
        ExitCommand.register(map);
        AddIfMaxCommand.register(map, routeCollection);
        AddIfMinCommand.register(map, routeCollection);
        RemoveLowerCommand.register(map, routeCollection);
        MinByCreationDateCommand.register(map, routeCollection);
        CountLessThanDistanceCommand.register(map, routeCollection);
        PrintFieldAscendingDistanceCommand.register(map, routeCollection);

        Enviroment enviroment = new Enviroment(map);

        String filePath = "Nastya.xml";
        File file = null;
        if (filePath == null) {
            System.out.println(ANSI_RED + "Переменная окружения 'Nastya.xml' не установлена." + ANSI_RESET);
        } else {
            file = new File(filePath);
            if (file.exists()) {
                if (file.length() == 0) {
                    System.out.println(ANSI_RED + "Файл пуст: " + filePath + ANSI_RESET);
                } else {
                    try {
                        loadDataFromFile(filePath, routeCollection);
                        System.out.println("Данные успешно загружены из файла: " + filePath);
                    } catch (JAXBException | IOException e) {
                        System.err.println(ANSI_RED + "Ошибка при загрузке данных: " + e.getMessage() + ANSI_RESET);
                    }
                }
            } else {
                try {
                    file.createNewFile();
                    System.out.println("Создан новый файл: " + filePath);
                } catch (IOException e) {
                    System.err.println(ANSI_RED + "Не удалось создать файл: " + e.getMessage() + ANSI_RESET);
                }
            }
        }


        System.out.println("Программа управления коллекцией Route запущена. Введите 'help' для просмотра доступных команд.");
        System.out.println("*подсказка: команда add добавляет элемент в коллекцию. Советую начать с этого)");
        while (true) {
            System.out.print("> "); // Добавляем знак > перед вводом команды
            System.out.flush(); // Обеспечиваем немедленный вывод
            System.out.print("Введите команду: "); // Чтобы было понятно, что программа ждет ввод
            if (!in.hasNextLine()) {
                System.out.println("Завершение работы.");
                break; // Или System.exit(0); если нужно завершить программу при EOF
            }
            String line = in.nextLine();
            String[] s = line.split(" ");
            if (s.length == 0) {
                System.out.println(ANSI_RED + "Вы не ввели команду." + ANSI_RESET);
                continue;
            }

            String[] commandsArgs = new String[s.length - 1];
            if (s.length > 1) {
                System.arraycopy(s, 1, commandsArgs, 0, commandsArgs.length);
            }

            if (map.containsKey(s[0])) {
                Command command = map.get(s[0]);
                try {
                    command.execute(enviroment, System.out, System.in, commandsArgs);
                    // Сохраняем после каждой команды, которая изменяет коллекцию (добавление, изменение, удаление).
                    // Также сохраняем после команды clear, т.к. она очищает коллекцию.
                    if (command.getName().equals("add") || command.getName().equals("update") || command.getName().equals("remove_by_id") || command.getName().equals("clear") || command.getName().equals("save")) {
                        saveData(filePath, routeCollection); // Используем filePath из main
                    }

                } catch (CommandException e) {
                    System.err.println(ANSI_RED + e.getMessage() + ANSI_RESET);
                } catch (Exception e) { //  Обрабатываем другие исключения, включая JAXBException и IOException
                    System.err.println(ANSI_RED + "Непредвиденная ошибка при выполнении команды: " + e.getMessage() + ANSI_RESET);
                }
            } else {
                System.err.println(ANSI_RED + "Неизвестная команда: " + s[0] + ANSI_RESET);
                System.out.println("Введите 'help' для просмотра доступных команд.");
            }
        }
    }

    private static void loadDataFromFile(String filePath, RouteCollection routeCollection) throws JAXBException, IOException {
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0) {
            throw new FileNotFoundException("Файл не существует или пуст: " + filePath);
        }

        JAXBContext jaxbContext = JAXBContext.newInstance(RouteCollection.class, Route.class, Coordinates.class, Location.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(filePath), "UTF-8")) {
            RouteCollection loadedCollection = (RouteCollection) unmarshaller.unmarshal(reader);
            if (loadedCollection != null && loadedCollection.getRoute() != null) { // Проверка на null
                routeCollection.getRoute().addAll(loadedCollection.getRoute());
            }
            routeCollection.manageDHashSet(); // Валидация ID после загрузки
        }
    }



    private static void saveData(String filePath, RouteCollection routeCollection) {
        if (filePath == null) {
            System.out.println(ANSI_RED + "Переменная окружения не задана." + ANSI_RESET);
            return;
        }

        JAXBContext jaxbContext = null;
        Marshaller marshaller = null;
        BufferedOutputStream outputStream = null;

        try {
            jaxbContext = JAXBContext.newInstance(RouteCollection.class, Route.class, Coordinates.class, Location.class);
            marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

            outputStream = new BufferedOutputStream(Files.newOutputStream(Paths.get(filePath)));
            marshaller.marshal(routeCollection, new OutputStreamWriter(outputStream, "UTF-8"));
            System.out.println("Данные успешно сохранены в файл: " + filePath);

        } catch (JAXBException e) {
            System.err.println(ANSI_RED + "Ошибка при сериализации в XML: " + e.getMessage() + ANSI_RESET);
        } catch (IOException e) {
            System.err.println(ANSI_RED + "Ошибка при записи в файл: " + e.getMessage() + ANSI_RESET);
        } finally {
            // Ensure outputStream is closed even if exceptions occur
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    System.err.println(ANSI_RED + "Error closing output stream: " + e.getMessage() + ANSI_RESET);
                }
            }
        }
    }
}
