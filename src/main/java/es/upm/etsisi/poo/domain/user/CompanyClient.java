package es.upm.etsisi.poo.domain.user;

import java.util.ArrayList;
import java.util.List;
import es.upm.etsisi.poo.domain.ticket.CompanyTicket;
import es.upm.etsisi.poo.domain.ticket.Ticket;
import es.upm.etsisi.poo.domain.ticket.TicketPrintType;

// [Entity] Corporate (NIF-based) Client.
public class CompanyClient extends Client {
    private final String cashierId;
    private final List<Ticket<?>> tickets;

    public CompanyClient(String id, String name, String email, String cashierId) {
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
        if (!Character.isLetter(id.charAt(0))) {
            return false;
        }
        for (int i = 1; i < 9; i++) {
            if (!Character.isDigit(id.charAt(i))) {
                return false;
            }
        }
        return true;
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
    public Ticket<?> createTicket(String ticketId, char flag) {
        TicketPrintType printType;
        switch (flag) {
            case 's':
                printType = TicketPrintType.SERVICE;
                break;
            case 'c':
                printType = TicketPrintType.COMPANY;
                break;
            case 'p':
            default:
                printType = TicketPrintType.STANDARD;
                break;
        }
        return new CompanyTicket(ticketId, printType);
    }

    @Override
    public String toString() {
        return String.format("{class: CompanyClient, id: '%s', name: '%s', email: '%s', cashierId: '%s'}",
                getId(), getName(), getEmail(), getCashierId());
    }
}