package es.upm.etsisi.poo.ui;

import java.util.Comparator;

import es.upm.etsisi.poo.application.Store;
import es.upm.etsisi.poo.domain.ticket.Ticket;

public class TicketCashierComparator implements Comparator<Ticket> {
    private final Store store;

    public TicketCashierComparator(Store store) {
        this.store = store;
    }

    @Override
    public int compare(Ticket t1, Ticket t2) {
        String c1 = store.findCashierIdByTicket(t1);
        String c2 = store.findCashierIdByTicket(t2);

        int cashierCompare = c1.compareToIgnoreCase(c2);
        if (cashierCompare != 0) {
            return cashierCompare;
        }
        // If cashier IDs are the same, sort by ticket ID as a secondary criterion.
        return t1.getId().compareToIgnoreCase(t2.getId());
    }
}