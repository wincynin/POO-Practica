package es.upm.etsisi.poo;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Main application class for the UPM Store ticket module.
 * This class provides a command-line interface (Controller) to interact with the model classes (Store, Ticket, Product).
 */
public class App {
    // Model: Manages the product inventory.
    private static Store store;
    // Model: Manages the current shopping ticket.
    private static Ticket ticket;
    // View/Input: Reads user input from the console.
    private static Scanner scanner;

    /**
     * Main entry point. Initializes the application and runs the command loop.
     */
    public static void main(String[] args) {
        // Initialization of our main components
        store = new Store();
        ticket = new Ticket();
        scanner = new Scanner(System.in);

        System.out.println("Welcome to the ticket module App.");
        System.out.println("Ticket module. Type 'help' to see commands.");

        // Main command loop continues until the user types 'exit'
        while (true) {
            System.out.print("tUPM> ");
            String line = scanner.nextLine();

            // Check for the exit command to terminate the loop
            if (line.trim().equalsIgnoreCase("exit")) {
                System.out.println("Closing application.");
                System.out.println("Goodbye!");
                break;
            }

            // Only process the command if the line is not empty
            if (!line.trim().isEmpty()) {
                processCommand(line);
            }
        }
        // Close the scanner to release system resources
        scanner.close();
    }

    /**
     * Processes a single command line entered by the user.
     * It acts as a dispatcher, sending the command to the correct handler.
     */
    private static void processCommand(String line) {
        List<String> args = parseArguments(line);
        // If parsing results in no arguments, there's nothing to do.
        if (args.isEmpty()) {
            return;
        }

        String command = args.get(0).toLowerCase();

        // Use a traditional switch to delegate to the appropriate handler method.
        switch (command) {
            case "prod":
                handleProdCommand(args);
                break;
            case "ticket":
                handleTicketCommand(args);
                break;
            case "help":
                printHelp();
                break;
            case "echo":
                // The echo command simply prints back the original line.
                System.out.println(line);
                break;
            default:
                System.out.println("Error: Unknown command '" + command + "'");
                break;
        }
    }

    /**
     * Manually parses a command line into arguments, respecting quoted strings.
     * This method avoids using Regex, relying only on basic String manipulation.
     */
    private static List<String> parseArguments(String commandLine) {
        List<String> args = new ArrayList<>();
        StringBuilder currentArg = new StringBuilder();
        boolean inQuotes = false; // A flag to track if we are inside quotes

        // Iterate over each character of the command line
        for (int i = 0; i < commandLine.length(); i++) {
            char c = commandLine.charAt(i);
            
            if (c == '\"') {
                // When a quote is found, toggle the inQuotes flag
                inQuotes = !inQuotes;
            } else if (c == ' ' && !inQuotes) {
                // If a space is found AND we are not in quotes, it's a separator
                if (currentArg.length() > 0) {
                    args.add(currentArg.toString());
                    currentArg = new StringBuilder(); // Reset for the next argument
                }
            } else {
                // Otherwise, just append the character to the current argument
                currentArg.append(c);
            }
        }
        // Add the last argument if it exists
        if (currentArg.length() > 0) {
            args.add(currentArg.toString());
        }
        return args;
    }

    /**
     * Handles product-related subcommands (add, list, update, remove).
     */
    private static void handleProdCommand(List<String> args) {
        if (args.size() < 2) {
            System.out.println("Error: Missing arguments for 'prod' command.");
            return;
        }
        String subCommand = args.get(1).toLowerCase();

        switch (subCommand) {
            case "add":
                // Check for the correct number of arguments for 'prod add'
                if (args.size() != 6) {
                    System.out.println("Usage: prod add <id> \"<name>\" <category> <price>");
                    return;
                }
                // Use try-catch for parsing numbers, as NumberFormatException can occur.
                try {
                    // Convert string arguments to their correct types
                    int id = Integer.parseInt(args.get(2));
                    String name = args.get(3);
                    ProductCategory category = ProductCategory.valueOf(args.get(4).toUpperCase());
                    double price = Double.parseDouble(args.get(5));

                    // Use the factory method from the Product class for creation and validation
                    Product newProduct = Product.createInstance(id, name, category, price);
                    // Check if the product was created successfully (not null) and added to the store
                    if (newProduct != null && store.addProduct(newProduct)) {
                        System.out.println(newProduct);
                        System.out.println("prod add: ok");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error: Invalid number format for id or price.");
                } catch (IllegalArgumentException e) {
                    // This catches errors from ProductCategory.valueOf() if the category is invalid
                    System.out.println("Error: Invalid category specified.");
                }
                break;

            case "list":
                System.out.println("Catalog:");
                // Loop through all products from the store and print them
                for (Product p : store.getAllProducts()) {
                    System.out.println(p);
                }
                System.out.println("prod list: ok");
                break;

            case "update":
                if (args.size() != 5) {
                    System.out.println("Usage: prod update <id> <field> <value>");
                    return;
                }
                try {
                    int id = Integer.parseInt(args.get(2));
                    String field = args.get(3);
                    String value = args.get(4);
                    // The update logic is encapsulated in the Store class. We just check the boolean result.
                    if (store.updateProduct(id, field, value)) {
                        System.out.println(store.findProductById(id));
                        System.out.println("prod update: ok");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error: Invalid number format for id.");
                }
                break;

            case "remove":
                if (args.size() != 3) {
                    System.out.println("Usage: prod remove <id>");
                    return;
                }
                try {
                    int id = Integer.parseInt(args.get(2));
                    // The remove logic is in the Store class. We check if it returned the removed product.
                    Product removed = store.removeProduct(id);
                    if (removed != null) {
                        System.out.println(removed);
                        System.out.println("prod remove: ok");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error: Invalid number format for id.");
                }
                break;

            default:
                System.out.println("Error: Unknown 'prod' subcommand '" + subCommand + "'");
                break;
        }
    }

    /**
     * Handles ticket-related subcommands (new, add, remove, print).
     */
    private static void handleTicketCommand(List<String> args) {
        if (args.size() < 2) {
            System.out.println("Error: Missing arguments for 'ticket' command.");
            return;
        }
        String subCommand = args.get(1).toLowerCase();

        switch (subCommand) {
            case "new":
                // Reset the current ticket
                ticket.clear();
                System.out.println("ticket new: ok");
                break;

            case "add":
                if (args.size() != 4) {
                    System.out.println("Usage: ticket add <prodId> <quantity>");
                    return;
                }
                try {
                    int prodId = Integer.parseInt(args.get(2));
                    int quantity = Integer.parseInt(args.get(3));
                    // First, find the product in the store
                    Product product = store.findProductById(prodId);
                    if (product == null) {
                        System.out.println("Error: Product with ID " + prodId + " not found.");
                        return;
                    }
                    // If the product is found, try to add it to the ticket
                    if (ticket.addProduct(product, quantity)) {
                        System.out.println(ticket.getTicketDetails());
                        System.out.println("ticket add: ok");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error: Invalid number format for id or quantity.");
                }
                break;

            case "remove":
                // Check for correct number of arguments for 'ticket remove'
                if (args.size() != 3) {
                    System.out.println("Usage: ticket remove <prodId>");
                    return;
                }
                try {
                    // Parse the product ID from arguments
                    int prodId = Integer.parseInt(args.get(2));
                    // Find the product in the store by ID
                    Product product = store.findProductById(prodId);
                    if (product == null) {
                        // If product not found, print error and return
                        System.out.println("Error: Product with ID " + prodId + " not found.");
                        return;
                    }
                    // Remove the product from the ticket
                    ticket.removeProduct(product);
                    // Print the updated ticket details
                    System.out.println(ticket.getTicketDetails());
                    System.out.println("ticket remove: ok");
                } catch (NumberFormatException e) {
                    // Handle invalid number format for product ID
                    System.out.println("Error: Invalid number format for id.");
                }
                break;

            case "print":
                // Display the final ticket details
                System.out.println(ticket.getTicketDetails());
                System.out.println("ticket print: ok");
                // As per requirements, printing a ticket also starts a new one
                ticket.clear();
                break;

            default:
                System.out.println("Error: Unknown 'ticket' subcommand '" + subCommand + "'");
                break;
        }
    }

    /**
     * Prints help information about available commands and categories.
     */
    private static void printHelp() {
        System.out.println("Commands:");
        System.out.println("  prod add <id> \"<name>\" <category> <price>");
        System.out.println("  prod list");
        System.out.println("  prod update <id> NAME/CATEGORY/PRICE <value>");
        System.out.println("  prod remove <id>");
        System.out.println("  ticket new");
        System.out.println("  ticket add <prodId> <quantity>");
        System.out.println("  ticket remove <prodId>");
        System.out.println("  ticket print");
        System.out.println("  echo \"<text>\"");
        System.out.println("  help");
        System.out.println("  exit");
        System.out.println("Categories: MERCH, STATIONERY, CLOTHES, BOOK, ELECTRONICS");
        System.out.println("Discounts if there are >=2 units in the category: MERCH 0%, STATIONERY 5%, CLOTHES 7%, BOOK 10%, ELECTRONICS 3%.");
    }
}