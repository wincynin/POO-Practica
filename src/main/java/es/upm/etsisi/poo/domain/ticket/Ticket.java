package es.upm.etsisi.poo.domain.ticket;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import es.upm.etsisi.poo.domain.exceptions.TicketRuleViolationException;
import es.upm.etsisi.poo.domain.printing.PrintStrategy;
import es.upm.etsisi.poo.domain.product.Product;

// [Entity] Abstract Ticket.
public abstract class Ticket<T extends Product> implements Serializable, Comparable<Ticket<?>> {
    private static final int MAX_TICKET_LINES = 100;
    private String id;
    private TicketState state;
    private final List<TicketLine<T>> lines;
    private PrintStrategy printStrategy;

    public Ticket(String id) {
        this.id = (id != null) ? id : generateTicketId();
        this.state = TicketState.EMPTY;
        this.lines = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public TicketState getState() {
        return state;
    }

    public void setPrintStrategy(PrintStrategy printStrategy) {
        this.printStrategy = printStrategy;
    }

    public PrintStrategy getPrintStrategy() {
        return printStrategy;
    }

    public void addProduct(T product, int quantity, List<String> customTexts) {
        // Rule: Closed tickets are read-only.
        if (this.state == TicketState.CLOSED) {
            throw new TicketRuleViolationException("Cannot modify a CLOSED ticket.");
        }

        // Rule: No duplicate Bookables allowed.
        if (product.isBookable()) {
            for (TicketLine<T> line : lines) {
                if (line.getProduct().getId().equals(product.getId())) {
                    throw new TicketRuleViolationException("Bookable products cannot be duplicated in a ticket.");
                }
            }
        }

        // Constraint: Max 100 items (E1).
        if (lines.size() >= MAX_TICKET_LINES) {
            throw new TicketRuleViolationException("Ticket cannot exceed " + MAX_TICKET_LINES + " lines.");
        }

        boolean merged = false;
        if (!product.isBookable()) {
            for (TicketLine<T> line : lines) {
                if (line.getProduct().getId().equals(product.getId()) && Objects.equals(line.getCustomTexts(), customTexts)) {
                    line.setQuantity(line.getQuantity() + quantity);
                    merged = true;
                    break;
                }
            }
        }

        if (!merged) {
            lines.add(new TicketLine<>(quantity, product, customTexts));
        }

        if (this.state == TicketState.EMPTY) {
            this.state = TicketState.ACTIVE;
        }
    }

    public boolean removeProduct(String productId) {
        // Rule: Closed tickets are read-only.
        if (this.state == TicketState.CLOSED) {
            throw new TicketRuleViolationException("Cannot remove items from CLOSED ticket.");
        }

        boolean removed = false;
        Iterator<TicketLine<T>> iterator = lines.iterator();
        while (iterator.hasNext()) {
            TicketLine<T> line = iterator.next();
            if (line.getProduct().getId().equals(productId)) {
                iterator.remove();
                removed = true;
            }
        }
        if (lines.isEmpty()) {
            this.state = TicketState.EMPTY;
        }
        return removed;
    }

    public String print() {
        if (printStrategy == null) {
            return "Error: No print strategy set.";
        }
        String result = printStrategy.formatTicket(this);
        // Logic: Close ticket and generate final ID.
        this.id = generateTicketId();
        this.state = TicketState.CLOSED;
        return result;
    }

    public abstract boolean accepts(Product p);
    public abstract void validateProduct(Product p) throws TicketRuleViolationException;

    public List<TicketLine<T>> getLines() {
        return lines;
    }

    public abstract double getTotalPrice();

    private String generateTicketId() {
        // Format: Date + Random Digits.
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yy-MM-dd-HH:mm");
        int randomDigits = (int) (Math.random() * 100000);
        return LocalDateTime.now().format(dtf) + "-" + String.format("%05d", randomDigits);
    }

    @Override
    public int compareTo(Ticket<?> other) {
        return this.id.compareTo(other.id);
    }
}