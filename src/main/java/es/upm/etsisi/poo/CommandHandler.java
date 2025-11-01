package es.upm.etsisi.poo;

import java.util.List;

public class CommandHandler {
    private Store store;

    public CommandHandler(Store store) {
        this.store = store;
    }

    public void handle(String input) {
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        String[] parts = input.split(" ", 2);
        String command = parts[0];

        switch (command) {
            case "prod":
                handleProd(parts.length > 1 ? parts[1] : "");
                break;
            case "ticket":
                handleTicket(parts.length > 1 ? parts[1] : "");
                break;
            case "client":
                // TODO: Implement handleClient
                break;
            case "cash":
                handleCash(parts.length > 1 ? parts[1] : "");
                break;
            case "help":
                printHelp();
                break;
            case "echo":
                if (parts.length > 1) {
                    System.out.println(parts[0] + " " + parts[1]);
                }
                break;
            default:
                System.out.println("Command not recognized. Type 'help' for a list of commands.");
        }
    }

    private void handleProd(String args) {

        String[] prodCommand = args.split(" ", 2);
        if (prodCommand.length == 0) {
            return;
        }

        switch (prodCommand[0]) {
            case "add":
                String[] addCommands = prodCommand[1].split("\"");
                if (addCommands.length >= 3) {
                    try {
                        String id = addCommands[0].trim();
                        String name = addCommands[1];
                        ProductCategory category = ProductCategory.valueOf(addCommands[2].split(" ")[1].toUpperCase());
                        double price = Double.parseDouble(addCommands[2].split(" ")[2]);

                        Product prod = new Product(id, name, category, price);
                        if (store.getCatalog().addProduct(prod)) {
                            System.out.println(prod);
                            System.out.println("prod add: ok");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Error: Invalid number format for price.");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error: Invalid category specified.");
                    }
                }
                break;
            case "list":
                List<Product> productList = store.getCatalog().listProducts();
                System.out.println("Catalog:");
                for (Product prod : productList) {
                    System.out.println("  " + prod);
                }
                System.out.println("prod list: ok");
                break;
            case "update":
                addCommands = prodCommand[1].split(" ", 3);
                if (addCommands.length >= 3) {
                    try {
                        String productId = addCommands[0];
                        String field = addCommands[1];
                        String updateValue = addCommands[2].replace("\"", "");
                        if (store.getCatalog().updateProduct(productId, field, updateValue)) {
                            System.out.println(store.getCatalog().getProduct(productId));
                            System.out.println("prod update: ok");
                        }
                    } catch (Exception e) {
                        System.out.println("Error in prod update: " + e.getMessage());
                    }
                }
                break;
            case "remove":
                if (prodCommand.length >= 2) {
                    try {
                        String id = prodCommand[1];
                        Product removedProduct = store.getCatalog().removeProduct(id);
                        if (removedProduct != null) {
                            System.out.println(removedProduct);
                            System.out.println("prod remove: ok");
                        }
                    } catch (Exception e) {
                        System.out.println("Error in prod remove: " + e.getMessage());
                    }
                }
                break;
            default:
                System.out.println("Usage: prod add | list | update | remove");
        }
    }

    private void handleTicket(String args) {
        // This method needs to be completely refactored to work with the Store and the new commands.
        System.out.println("Ticket commands are not yet implemented correctly.");
    }

    private void handleCash(String args) {
        String[] parts = args.split(" ", 2);
        String command = parts[0];

        switch (command) {
            case "add":
                try {
                    String[] addParts = parts[1].split("\"");
                    String id = null;
                    String name;
                    String email;
                    if(addParts.length > 2){
                        id = addParts[0].trim();
                        name = addParts[1];
                        email = addParts[2].trim();
                    }else{
                        name = addParts[1];
                        email = addParts[2].trim();
                    }

                    store.addCashier(id, name, email);
                    System.out.println("cash add: ok");
                } catch (Exception e) {
                    System.out.println("Error adding cashier: " + e.getMessage());
                }
                break;
            default:
                System.out.println("Unknown cash command.");
        }
    }

    private void printHelp() {
        System.out.println("Commands:");
        System.out.println("  prod add <id> \"<name>\" <category> <price>");
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
        System.out.println("  prod addFood [id] \"<name>\" <price> <expiration: yyyy-MM-dd> <max_people>");
        System.out.println("  prod addMeeting [id] \"<name>\" <price> <expiration: yyyy-MM-dd> <max_people>");
        System.out.println("  echo \"<text>\"");
        System.out.println("  help");
        System.out.println("  exit");
        System.out.println();
        System.out.println("Categories: MERCH, STATIONERY, CLOTHES, BOOK, ELECTRONICS");
        System.out.println(
                "Discounts if there are ≥2 units in the category: MERCH 0%, STATIONERY 5%, CLOTHES 7%, BOOK 10%, ELECTRONICS 3%");
    }
}
