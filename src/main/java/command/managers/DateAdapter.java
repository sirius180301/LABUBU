package command.managers;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Адаптер для преобразования между строковым представлением даты и объектом Date.
 * Используется для сериализации и десериализации даты в XML.
 */
@XmlRootElement
@XmlType(propOrder = {"dateFormat"})
public class DateAdapter extends XmlAdapter<String, Date> {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    /**
     * Преобразует строку в объект Date.
     *
     * @param s Строковое представление даты в формате "dd-MM-yyyy HH:mm:ss".
     * @return Объект Date, соответствующий переданной строке.
     * @throws Exception Если строка не может быть разобрана в дату.
     */
    @Override
    public Date unmarshal(String s) throws Exception {
        return dateFormat.parse(s);
    }

    /**
     * Преобразует объект Date в строку.
     *
     * @param date Объект Date, который нужно преобразовать в строку.
     * @return Строковое представление даты в формате "dd-MM-yyyy HH:mm:ss".
     * @throws Exception Если дата не может быть отформатирована в строку.
     */
    @Override
    public String marshal(Date date) throws Exception {
        return dateFormat.format(date);
    }
}
