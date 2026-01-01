package es.upm.etsisi.poo.domain.ticket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<Ticket<?>> findAllByCashierId(String cashierId) {
        return tickets.stream()
                .filter(ticket -> ticket.getCashier() != null && ticket.getCashier().getId().equals(cashierId))
                .collect(Collectors.toList());
    }
}
