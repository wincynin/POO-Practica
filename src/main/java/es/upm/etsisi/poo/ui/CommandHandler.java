package es.upm.etsisi.poo.ui;

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
            printHelp();
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

    private void printHelp() {
        System.out.println("Available commands:");
        
        // --- USERS (Client & Cashier) ---
        System.out.println("  client add \"<name>\" <DNI|NIF> <email> <cashId>");
        System.out.println("  client remove <id>");
        System.out.println("  client list");
        System.out.println("  cash add [<id>] \"<name>\" <email>");
        System.out.println("  cash remove <id>");
        System.out.println("  cash list");
        System.out.println("  cash tickets <id>");

        // --- PRODUCTS (Standard, Event, & E3 Services) ---
        System.out.println("  prod add [<id>] \"<name>\" <category> <price> [<maxPers>]");
        System.out.println("  prod add <expiration:yyyy-MM-dd> <category>"); // E3 Service
        System.out.println("  prod update <id> NAME|CATEGORY|PRICE <value>");
        System.out.println("  prod remove <id>");
        System.out.println("  prod list");
        System.out.println("  prod addFood [<id>] \"<name>\" <price> <expiration:yyyy-MM-dd> <max_people>");
        System.out.println("  prod addMeeting [<id>] \"<name>\" <price> <expiration:yyyy-MM-dd> <max_people>");

        // --- TICKETS (E3 Strategy Logic) ---
        System.out.println("  ticket new [<id>] <cashId> <userId> -[c/p/s]"); // E3 Flags
        System.out.println("  ticket add <ticketId> <cashId> <prodId> <amount> [--p<txt> --p<txt>]");
        System.out.println("  ticket remove <ticketId> <cashId> <prodId>");
        System.out.println("  ticket print <ticketId> <cashId>");
        System.out.println("  ticket list");

        // --- SYSTEM ---
        System.out.println("  help");
        System.out.println("  echo \"<text>\"");
        System.out.println("  exit");
    }
}