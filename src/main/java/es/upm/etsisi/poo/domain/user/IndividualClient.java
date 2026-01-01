package es.upm.etsisi.poo.domain.user;

import java.util.ArrayList;
import java.util.List;

import es.upm.etsisi.poo.domain.ticket.CommonTicket;
import es.upm.etsisi.poo.domain.ticket.Ticket;

// [Entity] Individual (DNI-based) Client.
public class IndividualClient extends Client {
    private final String cashierId;
    private final List<Ticket<?>> tickets;

    public IndividualClient(String id, String name, String email, String cashierId) {
        super(id, name, email);
        if (cashierId == null || cashierId.isEmpty()) {
            throw new IllegalArgumentException("Error: Cashier ID cannot be null or empty.");
        }
        this.cashierId = cashierId;
        this.tickets = new ArrayList<>();
    }

    @Override
    public boolean validateId(String id) {
        if (id == null || id.length() != 9) {
            return false;
        }
        for (int i = 0; i < 8; i++) {
            if (!Character.isDigit(id.charAt(i))) {
                return false;
            }
        }
        return Character.isLetter(id.charAt(8));
    }

    public String getCashierId() {
        return cashierId;
    }

    @Override
    public void addTicket(Ticket<?> ticket) {
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket cannot be null.");
        }
        this.tickets.add(ticket);
    }

    public List<Ticket<?>> getTickets() {
        return new ArrayList<>(tickets);
    }

    @Override
    public boolean hasTicket(String ticketId) {
        for (Ticket<?> ticket : tickets) {
            if (ticket.getId().equals(ticketId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Ticket<?> createTicket(String ticketId, char printFlag) {
        return new CommonTicket(ticketId);
    }

    @Override
    public String toString() {
        return String.format("{class: IndividualClient, id: '%s', name: '%s', email: '%s', cashierId: '%s'}",
                getId(), getName(), getEmail(), getCashierId());
    }
}