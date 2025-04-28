package command.managers;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Адаптер для преобразования между LocalDateTime и его строковым представлением в формате ISO.
 */
@XmlRootElement
public class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Преобразует строку в объект LocalDateTime.
     *
     * @param v строковое представление даты и времени
     * @return объект LocalDateTime
     * @throws Exception если строка не может быть разобрана
     */
    @Override
    public LocalDateTime unmarshal(String v) throws Exception {
        return LocalDateTime.parse(v, formatter);
    }

    /**
     * Преобразует объект LocalDateTime в строку.
     *
     * @param v объект LocalDateTime
     * @return строковое представление даты и времени
     * @throws Exception если объект не может быть отформатирован
     */
    @Override
    public String marshal(LocalDateTime v) throws Exception {
        return v.format(formatter);
    }
}
