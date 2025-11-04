package es.upm.etsisi.poo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
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
        String args = parts.length > 1 ? parts[1] : "";

        switch (command) {
            case "prod":
                handleProd(args);
                break;
            case "ticket":
                handleTicket(args);
                break;
            case "client":
                handleClient(args);
                break;
            case "cash":
                handleCash(args);
                break;
            case "help":
                printHelp();
                break;
            case "echo":
                System.out.println(input);
                break;
            default:
                System.out.println("Command not recognized. Type 'help' for a list of commands.");
        }
    }

    private List<String> parseArgs(String args) {
        List<String> argList = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder currentArg = new StringBuilder();

        for (char c : args.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ' ' && !inQuotes) {
                if (currentArg.length() > 0) {
                    argList.add(currentArg.toString());
                    currentArg = new StringBuilder();
                }
            } else {
                currentArg.append(c);
            }
        }
        if (currentArg.length() > 0) {
            argList.add(currentArg.toString());
        }
        return argList;
    }

    private void handleProd(String args) {
        List<String> argList = parseArgs(args);
        if (argList.isEmpty()) {
            return;
        }
        String command = argList.get(0);
        argList.remove(0);

        switch (command) {
            case "add":
                try {
                    int id = 0;
                    String name;
                    ProductCategory category;
                    double price;
                    int maxPers = -1;

                    if (argList.size() > 3 && !argList.get(0).contains("\"")) { // ID provided
                        id = Integer.parseInt(argList.get(0));
                        argList.remove(0);
                    }
                    name = argList.get(0);
                    category = ProductCategory.valueOf(argList.get(1).toUpperCase());
                    price = Double.parseDouble(argList.get(2));

                    if (argList.size() > 3) { // maxPers is present
                        maxPers = Integer.parseInt(argList.get(3));
                    }

                    Product prod;
                    if (maxPers != -1) {
                        prod = new CustomizableProduct(id, name, category, price, maxPers);
                    } else {
                        prod = new Product(id, name, category, price);
                    }

                    store.addProduct(prod);
                    System.out.println(prod);
                    System.out.println("prod add: ok");
                } catch (NumberFormatException e) {
                    System.out.println("Error: Invalid number format for ID, price or max_people.");
                } catch (IllegalArgumentException e) {
                    System.out.println("Error: " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("Error in prod add: " + e.getMessage());
                }
                break;
            case "addFood":
            case "addMeeting":
                try {
                    int id = 0;
                    String name;
                    double price;
                    LocalDate expirationDate;
                    int maxPeople;

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                    if (argList.size() > 3 && !argList.get(0).contains("\"")) { // ID provided
                        id = Integer.parseInt(argList.get(0));
                        argList.remove(0);
                    }
                    name = argList.get(0);
                    price = Double.parseDouble(argList.get(1));
                    expirationDate = LocalDate.parse(argList.get(2), formatter);
                    maxPeople = Integer.parseInt(argList.get(3));

                    if (command.equals("addFood")) {
                        Food food = new Food(id, name, price, expirationDate.atStartOfDay(), maxPeople);
                        store.addProduct(food);
                        System.out.println(food);
                        System.out.println("prod addFood: ok");
                    } else {
                        Meeting meeting = new Meeting(id, name, price, expirationDate.atStartOfDay(), maxPeople);
                        store.addProduct(meeting);
                        System.out.println(meeting);
                        System.out.println("prod addMeeting: ok");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error: Invalid number format for ID, price or max_people.");
                } catch (IllegalArgumentException e) {
                    System.out.println("Error: " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("Error in prod addFood/addMeeting: " + e.getMessage());
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
                if (argList.size() >= 3) {
                    try {
                        int productId = Integer.parseInt(argList.get(0));
                        String field = argList.get(1);
                        String updateValue = argList.get(2);
                        if (store.getCatalog().updateProduct(productId, field, updateValue)) {
                            System.out.println(store.getCatalog().getProduct(productId));
                            System.out.println("prod update: ok");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Error: Invalid number format for product ID.");
                    } catch (Exception e) {
                        System.out.println("Error in prod update: " + e.getMessage());
                    }
                }
                break;
            case "remove":
                if (!argList.isEmpty()) {
                    try {
                        int id = Integer.parseInt(argList.get(0));
                        Product removedProduct = store.getCatalog().removeProduct(id);
                        if (removedProduct != null) {
                            System.out.println(removedProduct);
                            System.out.println("prod remove: ok");
                        } else {
                            System.out.println("Error: Product not found.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Error: Invalid number format for product ID.");
                    } catch (Exception e) {
                        System.out.println("Error in prod remove: " + e.getMessage());
                    }
                }
                break;
            default:
                System.out.println("Usage: prod add | addFood | addMeeting | list | update | remove");
        }
    }

    private void handleTicket(String args) {
        List<String> argList = parseArgs(args);
        if (argList.isEmpty()) {
            return;
        }
        String command = argList.get(0);
        argList.remove(0);

        switch (command) {
            case "new":
                try {
                    String ticketId = null;
                    String cashierId;
                    String userId;

                    if (argList.size() > 2) { // ID provided
                        ticketId = argList.get(0);
                        argList.remove(0);
                    }
                    cashierId = argList.get(0);
                    userId = argList.get(1);

                    Ticket newTicket = store.createTicket(ticketId, cashierId, userId);
                    System.out.println("ticket new: ok");
                } catch (Exception e) {
                    System.out.println("Error creating new ticket: " + e.getMessage());
                }
                break;
            case "add":
                try {
                    String ticketId = argList.get(0);
                    String cashierId = argList.get(1);
                    int prodId = Integer.parseInt(argList.get(2));
                    int amount = Integer.parseInt(argList.get(3));

                    // Handle custom texts
                    List<String> customTexts = new ArrayList<>();
                    for (int i = 4; i < argList.size(); i++) {
                        if (argList.get(i).startsWith("--p")) {
                            customTexts.add(argList.get(i).substring(3));
                        }
                    }

                    store.addProductToTicket(ticketId, cashierId, prodId, amount, customTexts);
                    System.out.println("ticket add: ok");
                } catch (NumberFormatException e) {
                    System.out.println("Error: Invalid number format for product ID or amount.");
                } catch (Exception e) {
                    System.out.println("Error adding product to ticket: " + e.getMessage());
                }
                break;
            case "remove":
                try {
                    if (argList.size() != 3) {
                        throw new IllegalArgumentException("Usage: ticket remove <ticketId> <cashId> <prodId>");
                    }
                    String ticketId = argList.get(0);
                    String cashierId = argList.get(1);
                    int prodId = Integer.parseInt(argList.get(2));

                    store.removeProductFromTicket(ticketId, cashierId, prodId);
                    System.out.println("ticket remove: ok");
                } catch (NumberFormatException e) {
                    System.out.println("Error: Invalid number format for product ID.");
                } catch (IllegalArgumentException e) {
                    System.out.println("Error removing product from ticket: " + e.getMessage());
                }
                break;

            case "print":
                try {
                    if (argList.size() != 2) {
                        throw new IllegalArgumentException("Usage: ticket print <ticketId> <cashId>");
                    }
                    String ticketId = argList.get(0);
                    String cashierId = argList.get(1);

                    store.printTicket(ticketId, cashierId);
                } catch (Exception e) {
                    System.out.println("Error printing ticket: " + e.getMessage());
                }
                break;
            case "list":
                List<Ticket> allTickets = store.getTickets();
                // Sort tickets by cashier ID, then by ticket ID
                allTickets.sort(new Comparator<Ticket>() {
                    @Override
                    public int compare(Ticket t1, Ticket t2) {
                        int cashierIdCompare = t1.getCashierId().compareToIgnoreCase(t2.getCashierId());
                        if (cashierIdCompare != 0) {
                            return cashierIdCompare;
                        }
                        return t1.getId().compareToIgnoreCase(t2.getId());
                    }
                });
                System.out.println("Tickets:");
                for (Ticket ticket : allTickets) {
                    System.out.println("  ID: " + ticket.getId() + ", Cashier: " + ticket.getCashierId() + ", Client: " + ticket.getUserId() + ", State: " + ticket.getState());
                }
                System.out.println("ticket list: ok");
                break;
            default:
                System.out.println("Unknown ticket command.");
        }
    }

    private void handleClient(String args) {
        List<String> argList = parseArgs(args);
        if (argList.isEmpty()) {
            return;
        }
        String command = argList.get(0);
        argList.remove(0);

        switch (command) {
            case "add":
                try {
                    String name = argList.get(0);
                    String dni = argList.get(1);
                    String email = argList.get(2);
                    String cashierId = argList.get(3);

                    store.addClient(new Client(dni, name, email, cashierId));
                    System.out.println("client add: ok");
                } catch (Exception e) {
                    System.out.println("Error adding client: " + e.getMessage());
                }
                break;
            case "remove":
                if (!argList.isEmpty()) {
                    String dni = argList.get(0);
                    if (store.removeClient(dni)) {
                        System.out.println("client remove: ok");
                    } else {
                        System.out.println("Error: Client not found.");
                    }
                } else {
                    System.out.println("Usage: client remove <DNI>");
                }
                break;
            case "list":
                List<Client> clientList = store.getClients();
                // Sort clients by name
                clientList.sort(new Comparator<Client>() {
                    @Override
                    public int compare(Client c1, Client c2) {
                        return c1.getName().compareToIgnoreCase(c2.getName());
                    }
                });
                System.out.println("Clients:");
                for (Client client : clientList) {
                    System.out.println("  " + client);
                }
                System.out.println("client list: ok");
                break;
            default:
                System.out.println("Unknown client command.");
        }
    }

    private void handleCash(String args) {
        List<String> argList = parseArgs(args);
        if (argList.isEmpty()) {
            return;
        }
        String command = argList.get(0);
        argList.remove(0);

        switch (command) {
            case "add":
                try {
                    String id = null;
                    String name;
                    String email;

                    if (argList.size() > 2) { // ID provided
                        id = argList.get(0);
                        argList.remove(0);
                    }
                    name = argList.get(0);
                    email = argList.get(1);

                    store.addCashier(id, name, email);
                    System.out.println("cash add: ok");
                } catch (Exception e) {
                    System.out.println("Error adding cashier: " + e.getMessage());
                }
                break;
            case "remove":
                if (!argList.isEmpty()) {
                    String cashierId = argList.get(0);
                    if (store.removeCashier(cashierId)) {
                        System.out.println("cash remove: ok");
                    } else {
                        System.out.println("Error: Cashier not found.");
                    }
                } else {
                    System.out.println("Usage: cash remove <id>");
                }
                break;
            case "list":
                List<Cashier> cashierList = store.getCashiers();
                // Sort cashiers by name
                cashierList.sort(new Comparator<Cashier>() {
                    @Override
                    public int compare(Cashier c1, Cashier c2) {
                        return c1.getName().compareToIgnoreCase(c2.getName());
                    }
                });
                System.out.println("Cashiers:");
                for (Cashier cashier : cashierList) {
                    System.out.println("  " + cashier);
                }
                System.out.println("cash list: ok");
                break;
            case "tickets":
                if (!argList.isEmpty()) {
                    String cashierId = argList.get(0);
                    Cashier cashier = store.findCashierById(cashierId);
                    if (cashier != null) {
                        List<Ticket> cashierTickets = cashier.getTickets();
                        // Sort tickets by ID
                        cashierTickets.sort(new Comparator<Ticket>() {
                            @Override
                            public int compare(Ticket t1, Ticket t2) {
                                return t1.getId().compareToIgnoreCase(t2.getId());
                            }
                        });
                        System.out.println("Tickets for Cashier " + cashierId + ":");
                        for (Ticket ticket : cashierTickets) {
                            System.out.println("  ID: " + ticket.getId() + ", State: " + ticket.getState());
                        }
                        System.out.println("cash tickets: ok");
                    } else {
                        System.out.println("Error: Cashier not found.");
                    }
                } else {
                    System.out.println("Usage: cash tickets <id>");
                }
                break;
            default:
                System.out.println("Unknown cash command.");
        }
    }

    private void printHelp() {
        System.out.println("Commands:");
        System.out.println("  prod add [id] \"<name>\" <category> <price> [<maxPers>]");
        System.out.println("  prod addFood [id] \"<name>\" <price> <expiration: yyyy-MM-dd> <max_people>");
        System.out.println("  prod addMeeting [id] \"<name>\" <price> <expiration: yyyy-MM-dd> <max_people>");
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