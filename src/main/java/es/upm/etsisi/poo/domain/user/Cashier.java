package es.upm.etsisi.poo.domain.user;

import java.util.ArrayList;
import java.util.List;

import es.upm.etsisi.poo.domain.ticket.Ticket;

// [Entity] Cashier User.
public class Cashier extends User implements Comparable<Cashier> {
    private final List<Ticket<?>> tickets;

    public Cashier(String id, String name, String email) {
        super(id, name, email);
        this.tickets = new ArrayList<>();
    }

    public static String generateCashierId(List<Cashier> existingCashiers) {
        String id;

        // Logic: "UW" + 7 digits.
        do {
            
            int randomDigits = (int) (Math.random() * 10000000);
            id = "UW" + String.format("%07d", randomDigits);
        }
        // Check: Ensure uniqueness.
        while (isCashierIdTaken(id, existingCashiers));

        return id;
    }

    private static boolean isCashierIdTaken(String id, List<Cashier> existingCashiers) {
        for (Cashier cashier : existingCashiers) {
            if (cashier.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public void addTicket(Ticket<?> ticket) {
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket cannot be null.");
        }
        this.tickets.add(ticket);
    }

    public List<Ticket<?>> getTickets() {
        return new ArrayList<>(tickets);
    }

    // Check: Verify ownership.
    public boolean hasTicket(String ticketId) {
        for (Ticket<?> ticket : tickets) {
            if (ticket.getId().equals(ticketId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("{class: Cashier, id: '%s', name: '%s', email: '%s'}",
                getId(), getName(), getEmail());
    }

    @Override
    public int compareTo(Cashier other) {
        return this.getName().compareToIgnoreCase(other.getName());
    }
}