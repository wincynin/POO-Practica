package es.upm.etsisi.poo.ui;

import es.upm.etsisi.poo.domain.ticket.TicketPrintType;
import java.util.List;
import java.util.ArrayList;
import es.upm.etsisi.poo.application.Store;
import es.upm.etsisi.poo.domain.exceptions.UPMStoreDomainException;
import es.upm.etsisi.poo.domain.ticket.Ticket;

class TicketCommand extends AbstractCommand {

    public TicketCommand(Store store) {
        super(store);
    }

    @Override
    @SuppressWarnings("Convert2Lambda")
    public void execute(String[] args) throws IllegalArgumentException, UPMStoreDomainException {
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

                // Logic: Check flags (-c, -s).
                if (argList.size() > 2 && argList.get(argList.size() - 1).startsWith("-")) {
                    flag = argList.get(argList.size() - 1).charAt(1);
                    argList.remove(argList.size() - 1);
                }

                // Logic: Optional Ticket ID.
                if (argList.size() > 2) {
                    ticketId = argList.get(0);
                    argList.remove(0);
                }
                cashierId = argList.get(0);
                clientId = argList.get(1);

                TicketPrintType printType = TicketPrintType.fromFlag(flag);

                store.createTicket(ticketId, cashierId, clientId, printType);
                System.out.println("ticket new: ok");
                break;
            case "add":
                String addTicketId = argList.get(0);
                String addCashierId = argList.get(1);
                String prodId = argList.get(2);
                int amount = 1; // Default to 1 if not specified
                if (argList.size() > 3) {
                    // Only try to parse if the argument actually exists
                    try {
                        amount = Integer.parseInt(argList.get(3));
                    } catch (NumberFormatException e) {
                        // If the 4th argument isn't a number (e.g., it's a customization flag), keep default 1
                    }
                }

                // Logic: Parse custom text (--p).
                @SuppressWarnings("Convert2Diamond")
                List<String> customTexts = new ArrayList<String>();
                for (int i = 4; i < argList.size(); i++) {
                    if (argList.get(i).startsWith("--p")) {
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
                String removeProdId = argList.get(2);

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
                // Sort: Cashier name, then ID.
                allTickets.sort(new TicketCashierComparator(store));

                System.out.println("Tickets:");
                for (Ticket<?> ticket : allTickets) {
                    // Display: Resolve IDs to names.
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