package command.managers;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Random;

/**
 * Класс GeneratorID отвечает за генерацию уникальных идентификаторов.
 * Генерируемые идентификаторы являются положительными случайными числами типа long.
 */
@XmlRootElement
public class GeneraterID {
    private final Random random = new Random();

    /**
     * Генерирует уникальный идентификатор.
     *
     * @return Случайное положительное число типа long.
     */
    public long generateId() {
        return random.nextLong() & Long.MAX_VALUE; // Генерирует положительное случайное число
    }
}



