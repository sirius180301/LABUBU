package command.exeptions;


public class RouteDequueExeption extends RuntimeException {
    private static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";

    public RouteDequueExeption(String description) {
        super(ANSI_RED + "Ошибка в RouteDeque. " + description + ANSI_RESET);
    }
}

