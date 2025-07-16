package command.commands;

import command.RouteReader;
import command.base.Command;
import command.base.Enviroment;

import command.base.database.DatabaseManager;
import command.exeptions.CommandException;
import command.managers.RouteCollection;
import model.Route;

import java.io.InputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.HashMap;

public class UpdateCommand extends Command {
    private final RouteCollection routeCollection;
    static DatabaseManager dbManager;
    //private static final DatabaseManager dbManager = null;

    public UpdateCommand(RouteCollection routeCollection, DatabaseManager dbManager) {
        super("update");
        this.routeCollection = routeCollection;
        this.dbManager = dbManager;
    }

    @Override
    public void execute(Enviroment env, PrintStream out, InputStream in, String[] args) throws CommandException {
        if (env.getCurrentUser() == null) {
            throw new CommandException("Authentication required. Use 'login'.");
        }

        if (args.length != 1) {
            throw new CommandException("Usage: update <id>");
        }

        try {
            long id = Long.parseLong(args[0]);
            Route existingRoute = findRouteById(id);

            if (existingRoute == null) {
                throw new CommandException("Route with ID " + id + " not found");
            }

            if (!existingRoute.getUsername().equals(env.getCurrentUser())) {
                throw new CommandException("You can only update your own routes");
            }

            out.println("Updating route #" + id);
            Route updatedRoute = RouteReader.readRoute(in, out, routeCollection);
            updatedRoute.setId(id);
            updatedRoute.setUsername(env.getCurrentUser());

            if (dbManager.updateRoute(updatedRoute)) {
                routeCollection.reassignIds();
                out.println("Route #" + id + " updated successfully");
            } else {
                throw new CommandException("Failed to update route in database");
            }

        } catch (NumberFormatException e) {
            throw new CommandException("Invalid ID format");
        } catch (SQLException e) {
            throw new CommandException("Database error: " + e.getMessage());
        }
    }

    private Route findRouteById(long id) {
        return routeCollection.getRoute().stream()
                .filter(r -> r.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public String getHelp() {
        return "update an existing route by ID";
    }

    public static void register(HashMap<String, Command> commandMap,
                                RouteCollection routeCollection, DatabaseManager dbManager) {
        UpdateCommand UpdateCommand = new UpdateCommand(routeCollection, command.commands.UpdateCommand.dbManager);


        //commandMap.put("update", new UpdateCommand(routeCollection, dbManager));
    }
}
