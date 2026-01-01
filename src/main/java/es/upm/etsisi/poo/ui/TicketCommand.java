package es.upm.etsisi.poo.ui;

import java.util.List;
import java.util.ArrayList;
import es.upm.etsisi.poo.application.Store;
import es.upm.etsisi.poo.domain.ticket.Ticket;

class TicketCommand extends AbstractCommand {

    public TicketCommand(Store store) {
        super(store);
    }

    @Override
    @SuppressWarnings("Convert2Lambda")
    public void execute(String[] args) throws IllegalArgumentException {
        if (args.length == 0) {
            throw new IllegalArgumentException("Usage: ticket new | add | remove | print | list");
        }
        List<String> argList = parseArgs(args[0]);
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
}