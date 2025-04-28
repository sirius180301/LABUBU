package command.commands;

import command.base.Command;
import command.base.Enviroment;
import command.exeptions.CommandException;
import command.managers.RouteCollection;
import model.Coordinates;
import model.Location;
import model.Route;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class SaveCommand extends Command {
    private final RouteCollection routeCollection;

    protected SaveCommand(RouteCollection routeCollection) {
        super("save");
        this.routeCollection = routeCollection;
    }

    @Override
    public void execute(Enviroment env, PrintStream out, InputStream in, String[] args) throws CommandException {
        String filePath = System.getenv("ROUTE_DATA_FILE");
        if (filePath == null) {
            throw new CommandException("Переменная окружения ROUTE_DATA_FILE не задана. Сохранение в файл невозможно.");
        }
        try {
            saveDataToFile(filePath, routeCollection);
            out.println("Коллекция успешно сохранена в файл: " + filePath);
        } catch (JAXBException | IOException e) {
            throw new CommandException("Ошибка при сохранении данных в файл: " + e.getMessage() + (e.getCause() != null ? ". Причина: " + e.getCause() : ""));
        }

    }

    @Override
    public String getHelp() {
        return "сохранить коллекцию в файл";
    }

    public static void register(HashMap<String, Command> commandMap, RouteCollection routeCollection) {
        SaveCommand saveCommand = new SaveCommand(routeCollection);
        commandMap.put(saveCommand.getName(), saveCommand);
    }

    private static void saveDataToFile(String filePath, RouteCollection routeCollection)
            throws JAXBException, IOException {

        // 1. Создаём JAXB-контекст (указываем ВСЕ классы для сериализации)
        JAXBContext jaxbContext = JAXBContext.newInstance(
                RouteCollection.class,
                Route.class,
                Coordinates.class,
                Location.class


        );

        // 2. Настраиваем Marshaller
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);  // Красивый XML
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");       // Кодировка

        // 3. Записываем в файл через BufferedOutputStream
        try (BufferedOutputStream outputStream = new BufferedOutputStream(
                Files.newOutputStream(Paths.get(filePath)));
             OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8")) {

            marshaller.marshal(routeCollection, writer);// Сериализуем и пишем в файл
        }
        System.out.println("Выполняется marshaller.marshal...");
    }
}


