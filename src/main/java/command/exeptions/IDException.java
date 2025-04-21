package command.exeptions;

public class IDException extends RouteDequueExeption {
    public IDException(String message) {
        super("Ошибка с ID. " + message);
    }
}
