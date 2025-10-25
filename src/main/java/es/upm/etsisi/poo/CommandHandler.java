package es.upm.etsisi.poo;

import java.util.Collections; // Needed for customization parsing
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandHandler {
    private Store store;
    private UserManager userManager;
    private TicketManager ticketManager; // <-- Use TicketManager

    // Constructor updated
    public CommandHandler(Store store, UserManager userManager, TicketManager ticketManager) { // <-- Use TicketManager
        this.store = store;
        this.userManager = userManager;
        this.ticketManager = ticketManager; // Store the real TicketManager
    }

    // Main command dispatcher
    public void handle(String entrada) {
        if (entrada == null || entrada.trim().isEmpty()) {
            return;
        }

        String[] partes = entrada.split(" ", 2);
        String comando = partes[0].toLowerCase();
        String args = partes.length > 1 ? partes[1] : "";

        switch (comando) {
            case "prod":
                handleProd(args);
                break;
            case "ticket":
                handleTicket(args); // <-- Re-enable this
                break;
            case "client":
                handleClient(args);
                break;
            case "cash":
                handleCashier(args);
                break;
            case "help":
                printHelp();
                break;
            case "echo":
                if (!args.isEmpty()) {
                    System.out.println(comando + " " + args);
                } else {
                    System.out.println(comando);
                }
                break;
            default:
                System.out.println("Comando no reconocido. Escribe 'help' para ver la lista.");
        }
    }

    // handleProd remains the same for now (still needs E2 updates)
    private void handleProd(String args) {
        // ... (previous code for handleProd) ...
        // TODO: Implement prod addFood, prod addMeeting
        // TODO: Update prod add for optional ID and maxPers
        String[] comandoProd = args.split(" ", 2); // Use 2 to separate sub-command from rest
        if (comandoProd.length == 0 || comandoProd[0].isEmpty()) {
            System.out.println("Uso de prod: add | list | update | remove | addFood | addMeeting");
            return;
        }
        String subCommand = comandoProd[0];
        String subArgs = comandoProd.length > 1 ? comandoProd[1] : "";

        switch (subCommand) {
            case "add":
                // Basic parsing, needs update for optional ID and maxPers
                // Example: prod add [<id>] "<name>" <category> <price> [<maxPers>]
                String[] comandosAdd = subArgs.split("\""); // Simplistic parsing
                if (comandosAdd.length >= 3) {
                    try {
                        // This needs more robust parsing for optional ID and maxPers
                        int id = Integer.parseInt(comandosAdd[0].trim());
                        String name = comandosAdd[1];
                        String[] remainingArgs = comandosAdd[2].trim().split(" ");
                        ProductCategory productCategory = ProductCategory.valueOf(remainingArgs[0].toUpperCase());
                        double price = Double.parseDouble(remainingArgs[1]);
                        // int maxPers = (remainingArgs.length > 2) ? Integer.parseInt(remainingArgs[2])
                        // : 0; // Placeholder

                        Product prod = new Product(id, name, productCategory, price);
                        if (store.addProduct(prod)) {
                            System.out.println(prod);
                            System.out.println("prod add: ok");
                        }
                    } catch (Exception e) {
                        System.out.println("Error en prod add: " + e.getMessage()
                                + ". Uso: prod add <id> \"<name>\" <category> <price>");
                    }
                } else {
                    System.out.println("Uso incorrecto. Formato: prod add <id> \"<name>\" <category> <price>");
                }
                break;
            case "list":
                List<Product> lista = store.listProducts();
                System.out.println("Catalog:");
                if (lista.isEmpty()) {
                    System.out.println("  (El catálogo está vacío)");
                } else {
                    for (Product prod : lista) {
                        System.out.println("  " + prod); // Assumes Product.toString() is correct
                    }
                }
                System.out.println("prod list: ok");
                break;
            case "update":
                String[] updateArgs = subArgs.split(" ", 3); // id field value...
                if (updateArgs.length >= 3) {
                    try {
                        int idProd = Integer.parseInt(updateArgs[0]);
                        String campo = updateArgs[1];
                        String actualizacion = updateArgs[2].replace("\"", ""); // Remove quotes if present
                        if (store.updateProduct(idProd, campo, actualizacion)) {
                            System.out.println(store.getProduct(idProd)); // Show updated product
                            System.out.println("prod update: ok");
                        } else {
                            // Error message printed by store.updateProduct
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Error en prod update: ID inválido.");
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("Uso incorrecto. Formato: prod update <id> NAME|CATEGORY|PRICE <value>");
                    } catch (Exception e) {
                        System.out.println("Error en prod update: " + e.getMessage());
                    }
                } else {
                    System.out.println("Uso incorrecto. Formato: prod update <id> NAME|CATEGORY|PRICE <value>");
                }
                break;
            case "remove":
                try {
                    int id = Integer.parseInt(subArgs.trim());
                    Product eliminado = store.removeProduct(id);
                    if (eliminado != null) {
                        // Message printed by store.removeProduct
                        System.out.println("prod remove: ok");
                    } else {
                        // Error message printed by store.removeProduct
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error en prod remove: ID inválido.");
                } catch (Exception e) {
                    System.out.println("Error en prod remove: " + e.getMessage());
                }
                break;
            // TODO: Add cases for addFood and addMeeting
            default:
                System.out.println("Uso de prod: add | list | update | remove | addFood | addMeeting");
        }
    }

    // handleClient remains the same
    private void handleClient(String args) {
        // ... (previous code for handleClient) ...
        String[] parts = args.split(" ", 2);
        if (parts.length == 0 || parts[0].isEmpty()) {
            System.out.println("Uso de client: add | remove | list");
            return;
        }
        String subCommand = parts[0];
        String subArgs = parts.length > 1 ? parts[1] : "";

        switch (subCommand) {
            case "add":
                // client add "<nombre>" <DNI> <email> <cashld>
                // Use regex for more robust parsing of quoted name
                Pattern pattern = Pattern.compile("\"([^\"]*)\"\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)");
                Matcher matcher = pattern.matcher(subArgs);

                if (matcher.matches()) {
                    String name = matcher.group(1);
                    String dni = matcher.group(2);
                    String email = matcher.group(3);
                    String cashierId = matcher.group(4);
                    if (userManager.addClient(name, dni, email, cashierId)) {
                        System.out.println("client add: ok");
                    } // Error messages handled by userManager.addClient
                } else {
                    System.out.println("Uso incorrecto. Formato: client add \"<nombre>\" <DNI> <email> <cashId>");
                }
                break;
            case "remove":
                // client remove <DNI>
                String dniToRemove = subArgs.trim();
                if (!dniToRemove.isEmpty()) {
                    if (userManager.removeClient(dniToRemove) != null) {
                        System.out.println("client remove: ok");
                    } // Error message handled by userManager.removeClient
                } else {
                    System.out.println("Uso incorrecto. Formato: client remove <DNI>");
                }
                break;
            case "list":
                // client list
                List<Client> clients = userManager.getAllClients();
                System.out.println("Clients:");
                if (clients.isEmpty()) {
                    System.out.println("  (No clients registered)");
                } else {
                    for (Client client : clients) {
                        System.out.println("  " + client); // Assumes Client.toString() is correct
                    }
                }
                System.out.println("client list: ok");
                break;
            default:
                System.out.println("Uso de client: add | remove | list");
        }
    }

    // handleCashier updated to call ticketManager.removeTickets
    private void handleCashier(String args) {
        String[] parts = args.split(" ", 2);
        if (parts.length == 0 || parts[0].isEmpty()) {
            System.out.println("Uso de cash: add | remove | list | tickets");
            return;
        }
        String subCommand = parts[0];
        String subArgs = parts.length > 1 ? parts[1] : "";

        switch (subCommand) {
            case "add":
                // cash add [<id>] "<nombre>" <email>
                Pattern patternAdd = Pattern.compile("(?:(\\S+)\\s+)?\"([^\"]*)\"\\s+(\\S+)");
                Matcher matcherAdd = patternAdd.matcher(subArgs);

                if (matcherAdd.matches()) {
                    String id = matcherAdd.group(1);
                    String name = matcherAdd.group(2);
                    String email = matcherAdd.group(3);
                    if (userManager.addCashier(id, name, email)) {
                        System.out.println("cash add: ok");
                    }
                } else {
                    System.out.println("Uso incorrecto. Formato: cash add [<id>] \"<nombre>\" <email>");
                }
                break;
            case "remove":
                // cash remove <id>
                String idToRemove = subArgs.trim();
                if (!idToRemove.isEmpty()) {
                    Cashier removedCashier = userManager.removeCashier(idToRemove);
                    if (removedCashier != null) {
                        System.out.println("cash remove: ok");
                        // *** E2 Requirement: Delete associated tickets ***
                        ticketManager.removeTickets(removedCashier.getCreatedTicketIds(), userManager); // <-- Call
                                                                                                        // TicketManager
                    }
                } else {
                    System.out.println("Uso incorrecto. Formato: cash remove <id>");
                }
                break;
            case "list":
                // cash list
                List<Cashier> cashiers = userManager.getAllCashiers();
                System.out.println("Cashiers:");
                if (cashiers.isEmpty()) {
                    System.out.println("  (No cashiers registered)");
                } else {
                    for (Cashier cashier : cashiers) {
                        System.out.println("  " + cashier);
                    }
                }
                System.out.println("cash list: ok");
                break;
            case "tickets":
                // cash tickets <id>
                String cashierIdForTickets = subArgs.trim();
                if (!cashierIdForTickets.isEmpty()) {
                    Cashier cashier = userManager.getCashier(cashierIdForTickets);
                    if (cashier != null) {
                        System.out.println("Tickets for Cashier " + cashierIdForTickets + ":");
                        List<String> ticketIds = cashier.getCreatedTicketIds();
                        if (ticketIds.isEmpty()) {
                            System.out.println("  (No tickets found for this cashier)");
                        } else {
                            Collections.sort(ticketIds);
                            for (String ticketId : ticketIds) {
                                // *** Use TicketManager to get status ***
                                String status = ticketManager.getTicketStatus(ticketId); // <-- Call TicketManager
                                System.out.println("  ID: " + ticketId + ", Status: " + status);
                            }
                        }
                        System.out.println("cash tickets: ok");
                    } else {
                        System.out.println("Error: Cashier with ID " + cashierIdForTickets + " not found.");
                    }
                } else {
                    System.out.println("Uso incorrecto. Formato: cash tickets <id>");
                }
                break;
            default:
                System.out.println("Uso de cash: add | remove | list | tickets");
        }
    }

    /**
     * Comandos de ayuda de Ticket (Implemented for E2 using TicketManager)
     * 
     * @param args
     */
    private void handleTicket(String args) {
        String[] parts = args.split(" ", 2);
        if (parts.length == 0 || parts[0].isEmpty()) {
            System.out.println("Uso de ticket: new | add | remove | print | list");
            return;
        }
        String subCommand = parts[0];
        String subArgs = parts.length > 1 ? parts[1] : "";

        switch (subCommand) {
            case "new":
                // ticket new [<id>] <cashId> <clientId>
                String[] newArgs = subArgs.split(" ");
                String ticketId = null;
                String cashId;
                String clientId;
                if (newArgs.length == 3) { // ID provided
                    ticketId = newArgs[0];
                    cashId = newArgs[1];
                    clientId = newArgs[2];
                } else if (newArgs.length == 2) { // ID not provided
                    cashId = newArgs[0];
                    clientId = newArgs[1];
                } else {
                    System.out.println("Uso incorrecto. Formato: ticket new [<id>] <cashId> <clientId>");
                    return;
                }
                if (ticketManager.createTicket(ticketId, cashId, clientId, userManager) != null) {
                    System.out.println("ticket new: ok");
                } // Error message handled by ticketManager.createTicket
                break;

            case "add":
                // ticket add <ticketId> <cashId> <prodId> <amount> [--p<txt> ...]
                String[] addArgs = subArgs.split(" ");
                if (addArgs.length < 4) {
                    System.out.println(
                            "Uso incorrecto. Formato: ticket add <ticketId> <cashId> <prodId> <amount> [--p<txt> ...]");
                    return;
                }
                try {
                    String tId = addArgs[0];
                    String cId = addArgs[1];
                    int pId = Integer.parseInt(addArgs[2]);
                    int amount = Integer.parseInt(addArgs[3]);
                    // TODO: Parse customization flags (--p<txt>) from addArgs[4] onwards
                    // List<String> customizations = parseCustomizations(addArgs);

                    if (ticketManager.addProductToTicket(tId, cId, pId, amount, store /* , customizations */)) {
                        System.out.println("ticket add: ok");
                    } // Errors and provisional total handled by TicketManager/Ticket
                } catch (NumberFormatException e) {
                    System.out.println("Error en ticket add: Product ID y amount deben ser números.");
                } catch (Exception e) {
                    System.out.println("Error en ticket add: " + e.getMessage());
                }
                break;

            case "remove":
                // ticket remove <ticketId> <cashId> <prodId>
                String[] removeArgs = subArgs.split(" ");
                if (removeArgs.length != 3) {
                    System.out.println("Uso incorrecto. Formato: ticket remove <ticketId> <cashId> <prodId>");
                    return;
                }
                try {
                    String tId = removeArgs[0];
                    String cId = removeArgs[1];
                    int pId = Integer.parseInt(removeArgs[2]);
                    if (ticketManager.removeProductFromTicket(tId, cId, pId)) {
                        System.out.println("ticket remove: ok");
                    } // Errors and provisional total handled by TicketManager/Ticket
                } catch (NumberFormatException e) {
                    System.out.println("Error en ticket remove: Product ID debe ser un número.");
                } catch (Exception e) {
                    System.out.println("Error en ticket remove: " + e.getMessage());
                }
                break;

            case "print":
                // ticket print <ticketId> <cashId>
                String[] printArgs = subArgs.split(" ");
                if (printArgs.length != 2) {
                    System.out.println("Uso incorrecto. Formato: ticket print <ticketId> <cashId>");
                    return;
                }
                String tIdToPrint = printArgs[0];
                String cIdPrinting = printArgs[1];
                if (ticketManager.printAndCloseTicket(tIdToPrint, cIdPrinting)) {
                    System.out.println("ticket print: ok");
                } // Errors handled by TicketManager/Ticket
                break;

            case "list":
                // ticket list
                List<Ticket> tickets = ticketManager.getAllTicketsSorted();
                System.out.println("All Tickets (sorted by Cashier ID, then Ticket ID):");
                if (tickets.isEmpty()) {
                    System.out.println("  (No tickets in the system)");
                } else {
                    for (Ticket t : tickets) {
                        // Using the basic toString() for now, might need more detail
                        System.out.println("  " + t);
                    }
                }
                System.out.println("ticket list: ok");
                break;

            default:
                System.out.println("Uso de ticket: new | add | remove | print | list");
        }
    }

    // printHelp remains the same as previous version
    private void printHelp() {
        // ... (previous code for printHelp) ...
        System.out.println("Commands:");
        // Product commands (updated)
        System.out.println("  prod add [<id>] \"<name>\" <category> <price> [<maxPers>]");
        System.out.println("  prod addFood [<id>] \"<name>\" <price> <expiration: yyyy-MM-dd> <max_people>");
        System.out.println("  prod addMeeting [<id>] \"<name>\" <price> <expiration: yyyy-MM-dd> <max_people>");
        System.out.println("  prod list");
        System.out.println("  prod update <id> NAME|CATEGORY|PRICE <value>");
        System.out.println("  prod remove <id>");
        // Ticket commands (updated)
        System.out.println("  ticket new [<id>] <cashId> <clientId>");
        System.out.println("  ticket add <ticketId> <cashId> <prodId> <amount> [--p<txt> ...]");
        System.out.println("  ticket remove <ticketId> <cashId> <prodId>");
        System.out.println("  ticket print <ticketId> <cashId>");
        System.out.println("  ticket list");
        // User commands (new)
        System.out.println("  client add \"<nombre>\" <DNI> <email> <cashId>");
        System.out.println("  client remove <DNI>");
        System.out.println("  client list");
        System.out.println("  cash add [<id>] \"<nombre>\" <email>");
        System.out.println("  cash remove <id>");
        System.out.println("  cash list");
        System.out.println("  cash tickets <id>");
        // General commands
        System.out.println("  echo \"<texto>\"");
        System.out.println("  help");
        System.out.println("  exit");
        System.out.println();
        // Info (remains the same for now)
        System.out.println("Categories: MERCH, STATIONERY, CLOTHES, BOOK, ELECTRONICS");
        System.out.println(
                "Discounts if there are ≥2 units in the category: MERCH 0%, STATIONERY 5%, CLOTHES 7%, BOOK 10%, ELECTRONICS 3%");
        System.out.println("Custom Product Surcharge: 10% per custom text.");
        System.out.println("Food/Meeting Notes: Price is per person. Time constraints apply.");
    }

    // TODO: Helper method to parse customization flags like --pTEXT from ticket add
    // args
    // private List<String> parseCustomizations(String[] args) { ... }
}