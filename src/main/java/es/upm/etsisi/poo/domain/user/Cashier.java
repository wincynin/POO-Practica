package es.upm.etsisi.poo.domain.user;

import java.util.List;
import java.util.ArrayList;
import es.upm.etsisi.poo.domain.ticket.Ticket;


public class Cashier extends User {
    private final List<Ticket> tickets;

    @SuppressWarnings("Convert2Diamond")
    public Cashier(String id, String name, String email) {
        super(id, name, email);
        this.tickets = new ArrayList<Ticket>();
    }

    public void addTicket(Ticket ticket) {
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket cannot be null.");
        }
        this.tickets.add(ticket);
    }

    @SuppressWarnings("Convert2Diamond")
    public List<Ticket> getTickets() {
        return new ArrayList<Ticket>(tickets);
    }

    @Override
    public String toString() {
        // Returns a string representation of the Cashier, including id, name, and email, in that order.
        return String.format("{class: Cashier, id: '%s', name: '%s', email: '%s'}", getId(), getName(), getEmail());
    }
}
