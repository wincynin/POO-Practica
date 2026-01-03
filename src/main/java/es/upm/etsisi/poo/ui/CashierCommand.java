package es.upm.etsisi.poo.ui;

import java.util.List;
import java.util.Collections;

import es.upm.etsisi.poo.application.Store;
import es.upm.etsisi.poo.domain.user.Cashier;
import es.upm.etsisi.poo.domain.ticket.Ticket;
import es.upm.etsisi.poo.domain.exceptions.UPMStoreDomainException;

// [Command] Cashier CRUD operations.
class CashierCommand extends AbstractCommand {
    public CashierCommand(Store store) {
        super(store);
    }

    @Override
    @SuppressWarnings("Convert2Lambda")
    public void execute(String[] args) throws IllegalArgumentException, UPMStoreDomainException {
        if (args.length == 0) {
            throw new IllegalArgumentException("Usage: cash add | remove | list | tickets");
        }
        List<String> argList = parseArgs(args[0]);
        if (argList.isEmpty()) {
            return;
        }
        String command = argList.get(0);
        argList.remove(0);

        switch (command) {
            case "add" -> {
                String id = null;
                String name;
                String email;

                // Rule: Check if optional Cashier ID is provided (E2)
                if (argList.size() > 2) {
                    id = argList.get(0);
                    argList.remove(0);
                }
                name = argList.get(0);
                email = argList.get(1);

                store.addCashier(id, name, email);
                System.out.println("cash add: ok");
            }
            case "remove" -> {
                if (!argList.isEmpty()) {
                    String cashierId = argList.get(0);
                    store.removeCashier(cashierId);
                    System.out.println("cash remove: ok");
                } else {
                    throw new IllegalArgumentException("Usage: cash remove <id>");
                }
            }
            case "list" -> {
                List<Cashier> cashierList = store.getCashiers();
                // Rule: Sort by name (E2)
                Collections.sort(cashierList);
                System.out.println("Cashiers:");
                for (Cashier cashier : cashierList) {
                    System.out.println("  " + cashier);
                }
                System.out.println("cash list: ok");
            }
            case "tickets" -> {
                if (!argList.isEmpty()) {
                    String cashierId = argList.get(0);
                    Cashier cashier = store.findCashierById(cashierId);
                    if (cashier == null) {
                        throw new IllegalArgumentException("Error: Cashier with ID " + cashierId + " not found.");
                    }

                    List<Ticket<?>> cashierTickets = cashier.getTickets();

                    Collections.sort(cashierTickets, new java.util.Comparator<Ticket<?>>() {
                        @Override
                        public int compare(Ticket<?> t1, Ticket<?> t2) {
                            return t1.getId().compareToIgnoreCase(t2.getId());
                        }
                    });

                    System.out.println("Tickets for Cashier " + cashierId + ":");
                    for (Ticket<?> ticket : cashierTickets) {
                        // Rule: Show only ID and state (E2)
                        System.out.println("  ID: " + ticket.getId() + ", State: " + ticket.getState());
                    }
                    System.out.println("cash tickets: ok");
                } else {
                    throw new IllegalArgumentException("Usage: cash tickets <id>");
                }
            }
            default -> System.out.println("Unknown cash command.");
        }
    }
}