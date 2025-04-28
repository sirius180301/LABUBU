import command.base.Command;
import command.base.Enviroment;
import command.commands.*;
import command.commands.PrintFieldAscendingDistanceCommand;
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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Scanner;

import command.managers.GeneraterID;


public class Main {

    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);
        HashMap<String, Command> map = new HashMap<>();
        RouteCollection routeCollection = new RouteCollection();
        GeneraterID generaterID = new GeneraterID();

        HelpCommand.register(map);
        InfoCommand.register(map, routeCollection);
        ShowCommand.register(map, routeCollection);
        AddCommand.register(map, routeCollection);
        UpdateCommand.register(map, routeCollection);
        RemoveByIdCommand.register(map, routeCollection);
        ClearCommand.register(map, routeCollection);
        SaveCommand.register(map, routeCollection);
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
            System.out.println("Переменная окружения 'Nastya.xml' не установлена.");
        } else {
            file = new File(filePath);
            if (!file.exists()) {
                System.out.println("Файл не существует: " + filePath);
            } else if (file.length() == 0) {
                System.out.println("Файл пуст: " + filePath);
            } else {
                try {
                    loadDataFromFile(filePath, routeCollection);
                    System.out.println("Данные успешно загружены из файла: " + filePath);
                } catch (JAXBException | IOException e) {
                    System.err.println("Ошибка при загрузке данных: " + e.getMessage());
                }
            }
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
                System.out.println("Создан новый файл: " + filePath);
            } catch (IOException e) {
                System.err.println("Не удалось создать файл: " + e.getMessage());
            }
        }


        System.out.println("Программа управления коллекцией Route запущена. Введите 'help' для просмотра доступных команд.");
        System.out.println("Введите команду add для добавления маршрута");
        //System.out.println("|");
        System.out.print("> ");



        while (in.hasNextLine()) {
            String line = in.nextLine();
            String[] s = line.split(" ");
            String[] commandsArgs = new String[s.length - 1];
            System.arraycopy(s, 1, commandsArgs, 0, commandsArgs.length);
            if (map.containsKey(s[0])) {
                Command command = map.get(s[0]);
                try {
                    command.execute(enviroment, System.out, System.in, commandsArgs);
                    if (command.getName().equals("add") || command.getName().equals("update") || command.getName().equals("remove_by_id") || command.getName().equals("clear")) {
                        saveData(filePath, routeCollection);
                    }
                } catch (CommandException e) {
                    System.err.println(e.getMessage());
                } catch (JAXBException | IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                System.err.println("Неизвестная команда: " + s[0]);
                System.out.println("Введите 'help' для просмотра доступных команд.");
            }
        }
    }

    private static void loadDataFromFile(String filePath, RouteCollection routeCollection) throws JAXBException, IOException {
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0) {
            throw new FileNotFoundException("Файл не существует или пуст: " + filePath);
        }

        JAXBContext jaxbContext = JAXBContext.newInstance(RouteCollection.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(filePath), "UTF-8")) {
            RouteCollection loadedCollection = (RouteCollection) unmarshaller.unmarshal(reader);
            routeCollection.getRoute().addAll(loadedCollection.getRoute());
            routeCollection.manageDHashSet(); // Валидация ID после загрузки
        }
    }


    private static void saveData(String filePath, RouteCollection routeCollection) throws JAXBException, IOException {
        if (filePath == null) {
            System.out.println("Переменная окружения не задана.");
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

            outputStream = new BufferedOutputStream(Files.newOutputStream(Path.of(filePath)));
            marshaller.marshal(routeCollection, new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            System.out.println("Данные успешно сохранены в файл: " + filePath);
        } catch (JAXBException e) {
            System.err.println("Ошибка при сериализации в XML: " + e.getMessage());
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    System.err.println("Ошибка при закрытии потока: " + e.getMessage());
                }
            }
        }
    }
}
