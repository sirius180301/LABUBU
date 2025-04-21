package command.base;

import java.util.HashMap;


public class Enviroment {
    private final HashMap<String, Command> stringCommandHashMap;


    public Enviroment(HashMap<String, Command> stringCommandHashMap) {
        this.stringCommandHashMap = stringCommandHashMap;
    }

    public HashMap<String, Command> getStringCommandHashMap() {
        return stringCommandHashMap;
    }
}

