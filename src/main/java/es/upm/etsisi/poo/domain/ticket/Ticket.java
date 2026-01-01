package es.upm.etsisi.poo.domain.ticket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import es.upm.etsisi.poo.domain.exceptions.TicketRuleViolationException;
import es.upm.etsisi.poo.domain.product.Product;
import es.upm.etsisi.poo.infrastructure.printing.PrintStrategy;

public abstract class Ticket<T extends Product> implements Serializable, Comparable<Ticket<?>> {
    private final String id;
    private TicketState state;
    private final List<TicketLine<T>> lines;
    private PrintStrategy printStrategy;

    public Ticket(String id) {
        this.id = id;
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
        // Rule: Cannot modify a CLOSED ticket.
        if (this.state == TicketState.CLOSED) {
            throw new TicketRuleViolationException("Cannot modify a CLOSED ticket.");
        }

        // Rule: Bookable products...
        // Rule: Prevent duplicate Bookable items.
        if (product.isBookable()) {
            for (TicketLine<T> line : lines) {
                if (line.getProduct().getId().equals(product.getId())) {
                    throw new TicketRuleViolationException("Bookable products cannot be duplicated in a ticket.");
                }
            }
        }

        // Limit: Max 100 lines (E1).
        // Constraint: Max 100 items (throws if exceeded).
        if (lines.size() >= 100) {
            throw new TicketRuleViolationException("Ticket cannot exceed 100 lines.");
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
        // Rule: Cannot remove items from CLOSED ticket.
        if (this.state == TicketState.CLOSED) {
            throw new TicketRuleViolationException("Cannot remove items from CLOSED ticket.");
        }

        boolean removed = lines.removeIf(line -> line.getProduct().getId().equals(productId));
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
        this.state = TicketState.CLOSED;
        return result;
    }

    public abstract boolean accepts(Product p);
    public abstract void validateProduct(Product p) throws TicketRuleViolationException;

    public List<TicketLine<T>> getLines() {
        return lines;
    }

    public double getTotalPrice() {
        double total = 0.0;
        for (TicketLine<T> line : lines) {
            total += line.getLineTotal();
        }
        return total;
    }

    @Override
    public int compareTo(Ticket<?> other) {
        return this.id.compareTo(other.id);
    }
}