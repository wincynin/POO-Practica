package es.upm.etsisi.poo;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Main application class for the ticket module.
 * <p>
 * This class provides a command-line interface for managing a product catalog and creating tickets (sales receipts).
 * Users can add, list, update, and remove products, as well as create new tickets, add/remove products to/from tickets,
 * and print tickets. Discounts are applied based on product categories and quantities.
 * </p>
 *
 * <ul>
 *   <li>prod add &mdash; Add a new product to the catalog.</li>
 *   <li>prod list &mdash; List all products in the catalog.</li>
 *   <li>prod update &mdash; Update a product's field (name, category, or price).</li>
 *   <li>prod remove &mdash; Remove a product from the catalog.</li>
 *   <li>ticket new &mdash; Start a new ticket.</li>
 *   <li>ticket add &mdash; Add a product to the current ticket.</li>
 *   <li>ticket remove &mdash; Remove a product from the current ticket.</li>
 *   <li>ticket print &mdash; Print the current ticket and reset it.</li>
 *   <li>help &mdash; Show available commands.</li>
 *   <li>exit &mdash; Exit the application.</li>
 * </ul>
 *
 * <p>
 * Supported product categories: MERCH, PAPELERIA, ROPA, LIBRO, ELECTRONICA.
 * Discounts are applied if there are two or more units in a category:
 * MERCH 0%, PAPELERIA 5%, ROPA 7%, LIBRO 10%, ELECTRONICA 3%.
 * </p>
 *
 * @author IWSIT21_02
 */
public class App {
    // Catalog of products available for sale.
    private final Catalog catalog;

    // Current ticket (sales receipt) being processed.
    private Ticket ticket;

    // Scanner for reading user input from the command line.
    private final Scanner scanner;

    /**
     * Constructs the App, initializing the catalog, ticket, and scanner.
     */
    public App() {
        this.catalog = new Catalog();
        this.ticket = new Ticket();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Runs the main application loop, reading and processing user commands.
     * The loop continues until the user enters 'exit'.
     */
    public void run() {
        // Try-with-resources ensures scanner is closed when done.
        try (scanner) {
            System.out.println("Welcome to the ticket module App.");
            System.out.println("Ticket module. Type 'help' to see commands.");

            // Main command loop.
            while (true) {
                System.out.print("tUPM> ");
                String line = scanner.nextLine();
                if (line.equalsIgnoreCase("exit")) {
                    System.out.println("Closing application.");
                    System.out.println("Goodbye!");
                    break;
                }
                if (!line.isBlank()) {
                    processCommand(line);
                }
            }
        }
    }

    /**
     * Parses and processes a single command line entered by the user.
     * Supports quoted arguments for names with spaces.
     *
     * @param line the command line entered by the user
     */
    private void processCommand(String line) {
        // Parse arguments, supporting quoted strings.
        List<String> argsList = new ArrayList<>();
        Pattern pattern = Pattern.compile("\"([^\"]*)\"|(\\S+)");
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                argsList.add(matcher.group(1));
            } else {
                argsList.add(matcher.group(2));
            }
        }
        String[] args = argsList.toArray(String[]::new);
        String command = args[0].toLowerCase();

        try {
            // Dispatch command to appropriate handler.
            switch (command) {
                case "prod" -> handleProdCommand(args);
                case "ticket" -> handleTicketCommand(args);
                case "help" -> printHelp();
                case "echo" -> System.out.println(line);
                default -> System.out.println("Error: Unknown command '" + command + "'");
            }
        } catch (Exception e) {
            // Print user-friendly error messages.
            System.out.println(e.getMessage());
        }
    }

    /**
     * Handles product-related commands: add, list, update, remove.
     *
     * @param args the parsed command arguments
     * @throws Exception if arguments are missing or invalid
     */
    private void handleProdCommand(String[] args) throws Exception {
        if (args.length < 2) throw new Exception("Error: Missing arguments for 'prod' command.");
        String subCommand = args[1].toLowerCase();

        switch (subCommand) {
            case "add" -> {
                // Add a new product to the catalog.
                if (args.length != 5) throw new Exception("Usage: prod add <id> \"<name>\" <category> <price>");
                int id = Integer.parseInt(args[2]);
                String name = args[3];
                ProductCategory category = ProductCategory.valueOf(args[4].toUpperCase());
                double price = Double.parseDouble(args[5]);
                Product newProduct = new Product(id, name, category, price);
                catalog.addProduct(newProduct);
                System.out.println(newProduct);
                System.out.println("prod add: ok");
            }
            case "list" -> {
                // List all products in the catalog.
                System.out.println("Catalog:");
                for (Product p : catalog.getAllProducts()) {
                    System.out.println(p);
                }
                System.out.println("prod list: ok");
            }
            case "update" -> {
                // Update a product's field.
                if (args.length != 5) throw new Exception("Usage: prod update <id> <field> <value>");
                int id = Integer.parseInt(args[2]);
                String field = args[3];
                String value = args[4];
                Product updatedProduct = catalog.updateProduct(id, field, value);
                System.out.println(updatedProduct);
                System.out.println("prod update: ok");
            }
            case "remove" -> {
                // Remove a product from the catalog.
                if (args.length != 3) throw new Exception("Usage: prod remove <id>");
                int id = Integer.parseInt(args[2]);
                Product removedProduct = catalog.removeProduct(id);
                System.out.println(removedProduct);
                System.out.println("prod remove: ok");
            }
            default -> throw new Exception("Error: Unknown 'prod' subcommand '" + subCommand + "'");
        }
    }

    /**
     * Handles ticket-related commands: new, add, remove, print.
     *
     * @param args the parsed command arguments
     * @throws Exception if arguments are missing or invalid
     */
    private void handleTicketCommand(String[] args) throws Exception {
        if (args.length < 2) throw new Exception("Error: Missing arguments for 'ticket' command.");
        String subCommand = args[1].toLowerCase();

        switch (subCommand) {
            case "new" -> {
                // Start a new ticket.
                ticket = new Ticket();
                System.out.println("ticket new: ok");
            }
            case "add" -> {
                // Add a product to the current ticket.
                if (args.length != 4) throw new Exception("Usage: ticket add <prodId> <quantity>");
                int prodId = Integer.parseInt(args[2]);
                int quantity = Integer.parseInt(args[3]);
                Product product = catalog.findProductById(prodId);
                if (product == null) throw new Exception("Error: Product with ID " + prodId + " not found.");
                ticket.addProduct(product, quantity);
                System.out.println(ticket.getTicketDetails());
                System.out.println("ticket add: ok");
            }
            case "remove" -> {
                // Remove a product from the current ticket.
                if (args.length != 3) throw new Exception("Usage: ticket remove <prodId>");
                int prodId = Integer.parseInt(args[2]);
                Product product = catalog.findProductById(prodId);
                if (product == null) throw new Exception("Error: Product with ID " + prodId + " not found.");
                ticket.removeProduct(product);
                System.out.println(ticket.getTicketDetails());
                System.out.println("ticket remove: ok");
            }
            case "print" -> {
                // Print the current ticket and reset it.
                System.out.println(ticket.getTicketDetails());
                System.out.println("ticket print: ok");
                ticket = new Ticket();
            }
            default -> throw new Exception("Error: Unknown 'ticket' subcommand '" + subCommand + "'");
        }
    }

    /**
     * Prints help information about available commands and categories.
     */
    private void printHelp() {
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
        System.out.println("Categories: MERCH, PAPELERIA, ROPA, LIBRO, ELECTRONICA");
        System.out.println("Discounts if there are >=2 units in the category: MERCH 0%, PAPELERIA 5%, ROPA 7%, LIBRO 10%, ELECTRONICA 3%.");
    }

    /**
     * Main entry point. Starts the application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        App app = new App();
        app.run();
    }
}