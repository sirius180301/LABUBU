package command.base;

import java.util.HashMap;

/**
 * Класс Enviroment представляет собой окружение, содержащее набор команд.
 * Он использует HashMap для хранения команд, сопоставленных с их строковыми представлениями.
 */
public class Enviroment {
    private final HashMap<String, Command> stringCommandHashMap;

    /**
     * Конструктор класса Enviroment.
     *
     * @param stringCommandHashMap HashMap, содержащий команды, сопоставленные с их строковыми представлениями.
     */
    public Enviroment(HashMap<String, Command> stringCommandHashMap) {
        this.stringCommandHashMap = stringCommandHashMap;
    }

    /**
     * Получает HashMap команд.
     *
     * @return HashMap, содержащий команды и их строковые представления.
     */
    public HashMap<String, Command> getStringCommandHashMap() {
        return stringCommandHashMap;
    }
}
