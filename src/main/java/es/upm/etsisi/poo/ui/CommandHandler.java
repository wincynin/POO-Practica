package es.upm.etsisi.poo.ui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import es.upm.etsisi.poo.application.Store;
import es.upm.etsisi.poo.domain.exceptions.UPMStoreDomainException;

public class CommandHandler {
    private final Map<String, Command> commands = new HashMap<>();
    private final Store store;

    public CommandHandler(Store store) {
        this.store = store;
        initializeCommands();
    }

    private void initializeCommands() {
        commands.put("prod", new ProductCommand(store));
        commands.put("client", new ClientCommand(store));
        commands.put("cash", new CashierCommand(store));
        commands.put("ticket", new TicketCommand(store));
    }

    public void handle(String input) {
        String[] parts = input.trim().split("\\s+", 2);
        if (parts.length == 0 || parts[0].isEmpty()) return;

        String commandName = parts[0].toLowerCase();
        String[] args = parts.length > 1 ? new String[]{parts[1]} : new String[0];

        if ("echo".equals(commandName)) {
            System.out.println(String.join(" ", args));
            return;
        }
        if ("help".equals(commandName)) {
            System.out.println("Available commands: prod, client, cash, ticket, echo, exit");
            return;
        }

        Command command = commands.get(commandName);
        if (command != null) {
            try {
                command.execute(args);
            } catch (UPMStoreDomainException e) {
                // Logic: Catch our custom errors and show clean message.
                System.out.println("Error: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                // Safety: Catch unexpected crashes.
                System.out.println("Unexpected Error: " + e.getMessage());
            }
        } else {
            System.out.println("Unknown command: " + commandName);
        }
    }
}