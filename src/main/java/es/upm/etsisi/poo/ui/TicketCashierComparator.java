package es.upm.etsisi.poo.ui;

import java.util.Comparator;

import es.upm.etsisi.poo.application.Store;
import es.upm.etsisi.poo.domain.ticket.Ticket;
import es.upm.etsisi.poo.domain.user.Cashier;

public class TicketCashierComparator implements Comparator<Ticket> {
    private final Store store;

    public TicketCashierComparator(Store store) {
        this.store = store;
    }

    @Override
    public int compare(Ticket t1, Ticket t2) {
        String c1 = findCashierIdByTicket(t1);
        String c2 = findCashierIdByTicket(t2);

        return c1.compareToIgnoreCase(c2);
    }

    private String findCashierIdByTicket(Ticket ticket) {
        for (Cashier c : store.getCashiers()) {
            if (c.hasTicket(ticket.getId())) {
                return c.getId();
            }
        }
        return "Unknown";
    }
}