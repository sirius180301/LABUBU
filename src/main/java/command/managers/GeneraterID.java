package command.managers;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Random;

@XmlRootElement
public class GeneraterID {
    private final Random random = new Random();

    public long generateId() {
        return random.nextLong() & Long.MAX_VALUE; // Генерирует положительное случайное число
    }
}


