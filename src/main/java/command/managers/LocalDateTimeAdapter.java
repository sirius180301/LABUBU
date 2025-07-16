package command.managers;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Адаптер для преобразования между LocalDateTime и его строковым представлением в формате ISO.
 */

public class LocalDateTimeAdapter  {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Преобразует строку в объект LocalDateTime.
     *
     * @param v строковое представление даты и времени
     * @return объект LocalDateTime
     * @throws Exception если строка не может быть разобрана
     */

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

    public String marshal(LocalDateTime v) throws Exception {
        return v.format(formatter);
    }
}
