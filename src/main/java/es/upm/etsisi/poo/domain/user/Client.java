package es.upm.etsisi.poo.domain.user;

import es.upm.etsisi.poo.domain.ticket.Ticket;

// [Abstract Class] Base class for Clients.
public abstract class Client extends User implements Comparable<Client> {

    public Client(String id, String name, String email) {
        super(id, name, email);
    }

    public abstract boolean validateId(String id);
    public abstract void addTicket(Ticket<?> ticket);
    public abstract boolean hasTicket(String ticketId);
    public abstract Ticket<?> createTicket(String ticketId, char printFlag);

    @Override
    public String toString() {
        return String.format("{class: Client, id: '%s', name: '%s', email: '%s'}",
                getId(), getName(), getEmail());
    }

    @Override
    public int compareTo(Client other) {
        return this.getName().compareToIgnoreCase(other.getName());
    }
}