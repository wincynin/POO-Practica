package es.upm.etsisi.poo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Cashier extends User {
    private final List<Ticket> tickets;

    public Cashier(String id, String name, String email) {
        super(id, name, email);
        this.tickets = new ArrayList<>();
    }

    public void addTicket(Ticket ticket) {
        Objects.requireNonNull(ticket, "Ticket cannot be null.");
        this.tickets.add(ticket);
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    @Override
    public String toString() {
        return String.format("{class: Cashier, id: '%s', name: '%s', email: '%s'}", getId(), getName(), getEmail());
    }
}
