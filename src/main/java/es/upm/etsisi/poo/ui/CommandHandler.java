package es.upm.etsisi.poo.ui;

import java.util.Map;
import java.util.HashMap;
import es.upm.etsisi.poo.application.Store;

// This class acts as the Controller. It parses user input and calls Store methods.
public class CommandHandler {
    private final Map<String, Command> commands;

    public CommandHandler(Store store) {
        this.commands = new HashMap<>();
        commands.put("prod", new ProductCommand(store));
        commands.put("ticket", new TicketCommand(store));
        commands.put("client", new ClientCommand(store));
        commands.put("cash", new CashierCommand(store));
    }

    public void handle(String input) {
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        // Take first word as command, and the second word (if any) as the argument.
        String[] parts = input.split(" ", 2);
        String commandName = parts[0];
        String[] args = parts.length > 1 ? new String[]{parts[1]} : new String[0];

        try {
            if ("help".equals(commandName)) {
                printHelp();
            } else if ("echo".equals(commandName)) {
                System.out.println(input);
            } else {
                Command command = commands.get(commandName);
                if (command != null) {
                    command.execute(args);
                } else {
                    System.out.println("Command not recognized. Type 'help' for a list of commands.");
                }
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
        }
        // IllegalStateException catches the exception thrown by EventProduct validation and Ticket state checks
    }

    // Prints all available commands
    private void printHelp() {
        System.out.println("Commands:");
        System.out.println("  prod add \"<name>\" <category> <price> [<maxPers>]");
        System.out.println("  prod addFood \"<name>\" <price> <expiration: yyyy-MM-dd> <max_people>");
        System.out.println("  prod addMeeting \"<name>\" <price> <expiration: yyyy-MM-dd> <max_people>");
        System.out.println("  prod list");
        System.out.println("  prod update <id> NAME|CATEGORY|PRICE <value>");
        System.out.println("  prod remove <id>");
        System.out.println("  ticket new [id] <cashId> <userId>");
        System.out.println("  ticket add <ticketId> <cashId> <prodId> <amount> [--p <text>]");
        System.out.println("  ticket remove <ticketId> <cashId> <prodId>");
        System.out.println("  ticket print <ticketId> <cashId>");
        System.out.println("  ticket list");
        System.out.println("  client add \"<name>\" <dni> <email> <cashId>");
        System.out.println("  client remove <dni>");
        System.out.println("  client list");
        System.out.println("  cash add [id] \"<name>\" <email>");
        System.out.println("  cash remove <id>");
        System.out.println("  cash list");
        System.out.println("  cash tickets <id>");
        System.out.println("  echo \"<text>\"");
        System.out.println("  help");
        System.out.println("  exit");
        System.out.println();
        System.out.println("Categories: MERCH, STATIONERY, CLOTHES, BOOK, ELECTRONICS");
        System.out.println("Discounts if there are ≥2 units in the category: MERCH 0%, STATIONERY 5%, CLOTHES 7%, BOOK 10%, ELECTRONICS 3%");
    }
}