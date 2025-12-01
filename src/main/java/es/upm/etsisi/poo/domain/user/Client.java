package es.upm.etsisi.poo.domain.user;

import java.util.ArrayList;
import java.util.List;

import es.upm.etsisi.poo.domain.ticket.Ticket;

// Represents a client, as defined in E2.
public class Client extends User {
    private final String cashierId;
    private final List<Ticket> tickets;

    public Client(String id, String name, String email, String cashierId) {
        super(id, name, email);
        if (cashierId == null || cashierId.isEmpty()) {
            throw new IllegalArgumentException("Error: Cashier ID cannot be null or empty.");
        }
        this.cashierId = cashierId;
        this.tickets = new ArrayList<>();
    }

    public String getCashierId() {
        return cashierId;
    }

    public void addTicket(Ticket ticket) {
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket cannot be null.");
        }
        this.tickets.add(ticket);
    }

    public List<Ticket> getTickets() {
        return new ArrayList<>(tickets);
    }

    @Override
    public String toString() {
        // Returns a string representation of the Client, including id, name, email and cashierId, in that order.
        return String.format("{class: Client, id: '%s', name: '%s', email: '%s', cashierId: '%s'}",
                getId(), getName(), getEmail(), getCashierId());
    }
}