package es.upm.etsisi.poo.domain.user;

import es.upm.etsisi.poo.domain.ticket.Ticket;

public abstract class Client extends User implements Comparable<Client> {

    public Client(String id, String name, String email) {
        super(id, name, email);
    }

    public abstract boolean validateId(String id);
    public abstract void addTicket(Ticket<?> ticket);
    public abstract boolean hasTicket(String ticketId);

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