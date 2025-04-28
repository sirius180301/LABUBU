
    import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

    public class XmlHandler {

        public static void processXmlFile(String pathname) {
            File xmlFile = new File(pathname);
            Document doc = null;

            try {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

                if (xmlFile.exists()) {
                    try {
                        doc = dBuilder.parse(xmlFile);
                        doc.getDocumentElement().normalize();
                        System.out.println("XML файл успешно загружен.");
                    } catch (SAXException | IOException e) {
                        System.err.println("Ошибка при парсинге XML: " + e.getMessage() + ". Создается новый документ.");
                        doc = dBuilder.newDocument();
                        Element rootElement = doc.createElement("root");
                        doc.appendChild(rootElement);
                        saveDocument(doc, xmlFile); // Сохраняем новый пустой документ
                    }
                } else {
                    System.out.println("XML файл не существует, создается новый.");
                    doc = dBuilder.newDocument();
                    Element rootElement = doc.createElement("root");
                    doc.appendChild(rootElement);
                    saveDocument(doc, xmlFile); // Сохраняем новый пустой документ
                }
            } catch (ParserConfigurationException e) {
                System.err.println("Ошибка при создании DocumentBuilder: " + e.getMessage());
            }
        }

        private static void saveDocument(Document doc, File xmlFile) {
            System.out.println("Сохранение XML документа в файл " + xmlFile.getAbsolutePath()); //Заглушка
        }}

        // Пример использования



