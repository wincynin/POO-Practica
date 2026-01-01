package es.upm.etsisi.poo.domain.ticket;

import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

// [Class] Manages the list of Tickets.
public class TicketRepository implements Serializable {
    private final List<Ticket<?>> tickets;

    public TicketRepository() {
        this.tickets = new ArrayList<>();
    }

    public void add(Ticket<?> ticket) {
        tickets.add(ticket);
    }

    public Ticket<?> findById(String id) {
        for (Ticket<?> ticket : tickets) {
            if (ticket.getId().equals(id)) {
                return ticket;
            }
        }
        return null;
    }

    public List<Ticket<?>> getAll() {
        return new ArrayList<>(tickets);
    }

    public void remove(Ticket<?> ticket) {
        tickets.remove(ticket);
    }
}