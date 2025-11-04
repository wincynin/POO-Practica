package es.upm.etsisi.poo;

import java.util.ArrayList;
import java.util.List;


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
        return String.format("{class: Cashier, id: '%s', name: '%s', email: '%s'}", getId(), getName(), getEmail());
    }
}
