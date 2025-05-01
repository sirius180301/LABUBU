package command.commands;

import command.base.Command;
import command.base.Enviroment;
import command.exeptions.CommandException;
import command.managers.RouteCollection;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.util.HashMap;

public class SaveCommand extends Command {
    private final String filePath;
    private final File file;
    private final RouteCollection routeCollection;
    private final JAXBContext context;
    private final Marshaller marshaller;

    public SaveCommand(RouteCollection routeCollection) throws CommandException {
        super("save");
        this.routeCollection = routeCollection;

        filePath = System.getenv("ROUTE_DATA_FILE");
        if (filePath == null) {
            throw new CommandException("Переменная окружения ROUTE_DATA_FILE не задана. Сохранение в файл невозможно.");
        }
        file = new File(filePath);

        try {
            context = JAXBContext.newInstance(RouteCollection.class);
            marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        } catch (JAXBException e) {
            throw new CommandException("Ошибка при сериализации/десериализации");
        }
    }

    @Override
    public void execute(Enviroment env, PrintStream out, InputStream in, String[] args) throws CommandException {
        prepareSaveFile();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(convertToXML(routeCollection));
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении");
        }
    }

    @Override
    public String getHelp() {
        return "сохранить коллекцию в файл";
    }

    public static void register(HashMap<String, Command> commandMap, RouteCollection routeCollection) throws CommandException {
        SaveCommand saveCommand = new SaveCommand(routeCollection);
        commandMap.put(saveCommand.getName(), saveCommand);
    }

    private void prepareSaveFile() throws CommandException {
        try {
            if (!file.exists()) {
                if (file.createNewFile()) {
                    System.out.println("Создан новый файл сохранения");
                }
            }
        } catch (IOException e) {
            throw new CommandException("Ошибка при создании файла");
        }
    }

    public String convertToXML(RouteCollection collection) throws CommandException {
        try(StringWriter stringWriter = new StringWriter()) {
            marshaller.marshal(collection, stringWriter);
            return stringWriter.toString();
        } catch (JAXBException | IOException e) {
            throw new CommandException("Ошибка при сериализации.");
        }
    }
}



