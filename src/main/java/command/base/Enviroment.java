package command.base;

import java.util.HashMap;

public class Enviroment {
    private final HashMap<String, Command> commands;
    private String currentUser;

    public Enviroment(HashMap<String, Command> commands) {
        this.commands = commands;
    }

    public HashMap<String, Command> getCommands() {
        return commands;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    public HashMap<String, Command> getStringCommandHashMap() {
        return null;
    }
}
