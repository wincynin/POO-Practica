package es.upm.etsisi.poo.ui;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.Collections;
import java.time.format.DateTimeFormatter;

import es.upm.etsisi.poo.domain.product.*;
import es.upm.etsisi.poo.application.Store;
import es.upm.etsisi.poo.domain.user.Client;
import es.upm.etsisi.poo.domain.user.Cashier;
import es.upm.etsisi.poo.domain.ticket.Ticket;

// This class acts as the Controller. It parses user input and calls Store methods.
public class CommandHandler {
    private final Store store;

    public CommandHandler(Store store) {
        this.store = store;
    }

    public void handle(String input) {
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        // Take first word as command, and the second word (if any) as the argument.
        String[] parts = input.split(" ", 2);
        String command = parts[0];
        String args = parts.length > 1 ? parts[1] : "";

        try {
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
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
        }
        // IllegalStateException catches the exception thrown by EventProduct validation and Ticket state checks
    }

    // Parses arguments, respecting quoted strings
    private List<String> parseArgs(String args) {
        @SuppressWarnings("Convert2Diamond")
        List<String> argList = new ArrayList<String>();
        boolean inQuotes = false;
        StringBuilder currentArg = new StringBuilder();

        for (char c : args.toCharArray()) {
            if (c == '"') {
                // Toggle the inQuotes flag when a quote is found
                inQuotes = !inQuotes;
            } else if (c == ' ' && !inQuotes) {
                // If it's a space and we're NOT in quotes, end the current argument
                if (currentArg.length() > 0) {
                    argList.add(currentArg.toString());
                    currentArg = new StringBuilder();
                }
            } else {
                // Add any other character to the current argument
                currentArg.append(c);
            }
        }
        // Add the last argument
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
                // E3: prod add <expiration: yyyy-MM-dd> <category>
                if (argList.size() == 2) {
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        LocalDate expirationDate = LocalDate.parse(argList.get(0), formatter);
                        ServiceType serviceType = ServiceType.valueOf(argList.get(1).toUpperCase());
                        Service service = new Service(expirationDate.atStartOfDay(), serviceType);
                        store.addProduct(service);
                        System.out.println(service);
                        System.out.println("prod add: ok");
                        break;
                    } catch (Exception e) {
                        // Not a service, fall through to standard product creation
                    }
                }

                Integer id = null;
                try {
                    id = Integer.valueOf(argList.get(0));
                    argList.remove(0);
                } catch (NumberFormatException e) {
                    // It's a name, not an ID.
                }
                String name;
                double price;
                int maxPers = -1;           // -1 means not customizable
                ProductCategory category;

                name = argList.get(0);
                category = ProductCategory.valueOf(argList.get(1).toUpperCase());
                price = Double.parseDouble(argList.get(2));
                
                // E2: Check for optional customizable parameter
                if (argList.size() > 3) {
                    maxPers = Integer.parseInt(argList.get(3));
                }

                Product prod;
                if (id != null) {
                    if (maxPers != -1) {
                        prod = new CustomizableProduct(id, name, category, price, maxPers);
                    } else {
                        prod = new StandardProduct(id, name, category, price);
                    }
                } else {
                    if (maxPers != -1) {
                        prod = new CustomizableProduct(name, category, price, maxPers);
                    } else {
                        prod = new StandardProduct(name, category, price);
                    }
                }

                store.addProduct(prod);
                System.out.println(prod);
                System.out.println("prod add: ok");
                break;
            case "addFood":
            case "addMeeting":
                Integer eventId = null;
                try {
                    // Try to parse first arg as ID
                    eventId = Integer.valueOf(argList.get(0));
                    // If successful, it's an ID, so remove it from list
                    argList.remove(0);
                } catch (NumberFormatException e) {
                    // It's a name, not an ID.
                }

                int maxPeople;
                String eventName;
                double eventPrice;
                LocalDate expirationDate;

                // E2 Requirement: Date format yyyy-MM-dd
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                eventName = argList.get(0);
                eventPrice = Double.parseDouble(argList.get(1));
                expirationDate = LocalDate.parse(argList.get(2), formatter);
                maxPeople = Integer.parseInt(argList.get(3));

                EventType eventType = command.equals("addFood") ? EventType.FOOD : EventType.MEETING;
                
                EventProduct eventProduct;
                if (eventId != null) {
                    eventProduct = new EventProduct(eventId, eventName, eventPrice, expirationDate.atStartOfDay(), maxPeople, eventType);
                } else {
                    eventProduct = new EventProduct(eventName, eventPrice, expirationDate.atStartOfDay(), maxPeople, eventType);
                }

                eventProduct.validate();
                
                store.addProduct(eventProduct);
                System.out.println(eventProduct);
                System.out.println("prod " + command + ": ok");
                break;
            case "list":
                List<Product> productList = store.getProducts();
                System.out.println("Catalog:");
                for (Product p : productList) {
                    System.out.println("  " + p);
                }
                System.out.println("prod list: ok");
                break;
            case "update":
                if (argList.size() >= 3) {
                    int productId = Integer.parseInt(argList.get(0));
                    String field = argList.get(1);
                    String updateValue = argList.get(2);
                    store.updateProduct(productId, field, updateValue);
                    System.out.println(store.getCatalog().getProduct(productId)); // Keeping this one for now as there's no `store.getProduct(id)`
                    System.out.println("prod update: ok");
                }
                break;
            case "remove":
                if (!argList.isEmpty()) {
                    int removeId = Integer.parseInt(argList.get(0));
                    Product removedProduct = store.removeProduct(removeId);
                    System.out.println(removedProduct);
                    System.out.println("prod remove: ok");
                }
                break;
            default:
                System.out.println("Usage: prod add | addFood | addMeeting | list | update | remove");
        }
    }

    @SuppressWarnings("Convert2Lambda")
    private void handleTicket(String args) {
        List<String> argList = parseArgs(args);
        if (argList.isEmpty()) {
            return;
        }
        String command = argList.get(0);
        argList.remove(0);

        switch (command) {
            case "new":
                String ticketId = null;
                String cashierId;
                String clientId;
                char flag = 'p'; // Default flag

                // Check for flag at the end
                if (argList.size() > 2 && argList.get(argList.size() - 1).startsWith("-")) {
                    flag = argList.get(argList.size() - 1).charAt(1);
                    argList.remove(argList.size() - 1);
                }

                // E2: Check if optional Ticket ID is provided
                if (argList.size() > 2) {
                    ticketId = argList.get(0);
                    argList.remove(0);
                }
                cashierId = argList.get(0);
                clientId = argList.get(1);

                store.createTicket(ticketId, cashierId, clientId, flag);
                System.out.println("ticket new: ok");
                break;
            case "add":
                String addTicketId = argList.get(0);
                String addCashierId = argList.get(1);
                int prodId = Integer.parseInt(argList.get(2));
                int amount = Integer.parseInt(argList.get(3));

                // E2: Parse optional customization flags
                @SuppressWarnings("Convert2Diamond")
                List<String> customTexts = new ArrayList<String>();
                // Iterate through the remaining arguments to find custom text flags.
                for (int i = 4; i < argList.size(); i++) {
                    // Check if it STARTS with --p
                    if (argList.get(i).startsWith("--p")) {
                        // Extract the text after --p and add it to the customTexts list
                        String text = argList.get(i).substring(3);
                        if (!text.isEmpty()) {
                            customTexts.add(text);
                        }
                    }
                }

                store.addProductToTicket(addTicketId, addCashierId, prodId, amount, customTexts);
                System.out.println("ticket add: ok");
                break;
            case "remove":
                if (argList.size() != 3) {
                    throw new IllegalArgumentException("Usage: ticket remove <ticketId> <cashId> <prodId>");
                }
                String removeTicketId = argList.get(0);
                String removeCashierId = argList.get(1);
                int removeProdId = Integer.parseInt(argList.get(2));

                store.removeProductFromTicket(removeTicketId, removeCashierId, removeProdId);
                System.out.println("ticket remove: ok");
                break;
            case "print":
                if (argList.size() != 2) {
                    throw new IllegalArgumentException("Usage: ticket print <ticketId> <cashId>");
                }
                String printTicketId = argList.get(0);
                String printCashierId = argList.get(1);

                String receipt = store.printTicket(printTicketId, printCashierId);
                System.out.print(receipt);
                break;

            case "list":
                List<Ticket<?>> allTickets = store.getTickets();
                // E2 Requirement: Sort by cashier ID, then by ticket ID
                allTickets.sort(new TicketCashierComparator(store));

                System.out.println("Tickets:");
                for (Ticket<?> ticket : allTickets) {
                    // Find owners for printing
                    String cId = store.findCashierIdByTicket(ticket);
                    String uId = store.findClientIdByTicket(ticket);
                    
                    System.out.println("  ID: " + ticket.getId() + ", Cashier: " + cId + ", Client: "
                            + uId + ", State: " + ticket.getState());
                }
                System.out.println("ticket list: ok");
                break;
            default:
                System.out.println("Unknown ticket command.");
        }
    }
    
    private boolean isDNI(String id) {
        if (id == null || id.length() != 9) return false;
        for (int i = 0; i < 8; i++) {
            if (!Character.isDigit(id.charAt(i))) return false;
        }
        return Character.isLetter(id.charAt(8));
    }

    private boolean isNIF(String id) {
        if (id == null || id.length() != 9) return false;
        if (!Character.isLetter(id.charAt(0))) return false;
        for (int i = 1; i < 9; i++) {
            if (!Character.isDigit(id.charAt(i))) return false;
        }
        return true;
    }

    // Handles "client" sub-commands
    @SuppressWarnings("Convert2Lambda")
    private void handleClient(String args) {
        List<String> argList = parseArgs(args);
        if (argList.isEmpty()) {
            return;
        }
        String command = argList.get(0);
        argList.remove(0);

        switch (command) {
            case "add":
                String name = argList.get(0);
                String id = argList.get(1);
                String email = argList.get(2);
                String cashierId = argList.get(3);

                Client client;
                if(isDNI(id)) {
                    client = new es.upm.etsisi.poo.domain.user.IndividualClient(id, name, email, cashierId);
                } else if (isNIF(id)) {
                    client = new es.upm.etsisi.poo.domain.user.CompanyClient(id, name, email, cashierId);
                } else {
                    throw new IllegalArgumentException("Error: Invalid client ID format.");
                }
                store.addClient(client);
                System.out.println("client add: ok");

                break;
            case "remove":
                if (!argList.isEmpty()) {
                    String dniToRemove = argList.get(0);
                    store.removeClient(dniToRemove);
                    System.out.println("client remove: ok");
                } else {
                    throw new IllegalArgumentException("Usage: client remove <DNI>");
                }
                break;
            case "list":
                List<Client> clientList = store.getClients();
                
                // E2 Requirement: Sort by name
                Collections.sort(clientList);
                System.out.println("Clients:");
                for (Client c : clientList) {
                    System.out.println("  " + c);
                }
                System.out.println("client list: ok");
                break;
            default:
                System.out.println("Unknown client command.");
        }
    }

    @SuppressWarnings("Convert2Lambda")
    private void handleCash(String args) {
        List<String> argList = parseArgs(args);
        if (argList.isEmpty()) {
            return;
        }
        String command = argList.get(0);
        argList.remove(0);

        switch (command) {
            case "add":
                String id = null;
                String name;
                String email;

                // E2: Check if optional Cashier ID is provided
                if (argList.size() > 2) {
                    id = argList.get(0);
                    argList.remove(0);
                }
                name = argList.get(0);
                email = argList.get(1);

                store.addCashier(id, name, email);
                System.out.println("cash add: ok");
                break;
            case "remove":
                if (!argList.isEmpty()) {
                    String cashierId = argList.get(0);
                    store.removeCashier(cashierId);
                    System.out.println("cash remove: ok");
                } else {
                    throw new IllegalArgumentException("Usage: cash remove <id>");
                }
                break;
            case "list":
                List<Cashier> cashierList = store.getCashiers();
                // E2 Requirement: Sort by name
                Collections.sort(cashierList);
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
                    if (cashier == null) {
                        throw new IllegalArgumentException("Error: Cashier with ID " + cashierId + " not found.");
                    }
                    List<Ticket<?>> cashierTickets = cashier.getTickets();

                    // --- NEW (Anonymous Inner Class - Professor Approved) ---
                    Collections.sort(cashierTickets, new java.util.Comparator<Ticket<?>>() {
                        @Override
                        public int compare(Ticket<?> t1, Ticket<?> t2) {
                            return t1.getId().compareToIgnoreCase(t2.getId());
                        }
                    });

                    System.out.println("Tickets for Cashier " + cashierId + ":");
                    for (Ticket<?> ticket : cashierTickets) {
                        // E2 Requirement: Show only ID and state
                        System.out.println("  ID: " + ticket.getId() + ", State: " + ticket.getState());
                    }
                    System.out.println("cash tickets: ok");
                } else {
                    throw new IllegalArgumentException("Usage: cash tickets <id>");
                }
                break;
            default:
                System.out.println("Unknown cash command.");
        }
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