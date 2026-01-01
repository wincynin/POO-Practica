package es.upm.etsisi.poo.domain.ticket;

import java.util.*;
import java.time.LocalDateTime;
import es.upm.etsisi.poo.domain.product.*;
import java.time.format.DateTimeFormatter;
import es.upm.etsisi.poo.domain.exceptions.TicketRuleViolationException;
import es.upm.etsisi.poo.infrastructure.printing.PrintStrategy;
import es.upm.etsisi.poo.infrastructure.printing.StandardPrintStrategy;


// [Class] Represents a Ticket (E1).
public abstract class Ticket<T extends Product> implements Comparable<Ticket<T>>, java.io.Serializable {
    private String id;
    private TicketState state;
    private final List<TicketLine<T>> lines;
    private PrintStrategy printStrategy;
    private static final int MAX_PRODUCT_LINES = 100;

    @SuppressWarnings("Convert2Diamond")
    public Ticket(String id) {
        this.state = TicketState.EMPTY;
        this.lines = new ArrayList<TicketLine<T>>();
        this.printStrategy = new StandardPrintStrategy(); // Default strategy

        if (id == null) {
            // Generate a random ID if null.
            this.id = generateId();
        } else {
            this.id = id;
        }
    }

    private String generateId() {
        // Format: YY-MM-dd + random numbers.
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy-MM-dd-HH:mm"));
        Random random = new Random();
        int randomNumber = random.nextInt(90000) + 10000;
        return datePart + "-" + randomNumber;
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

    @SuppressWarnings("Convert2Diamond")
    public List<TicketLine<T>> getLines() {
        return new ArrayList<TicketLine<T>>(lines);
    }

    public void addProduct(T product, int quantity) throws TicketRuleViolationException {
        addProduct(product, quantity, null);
    }

    public void addProduct(T product, int quantity, List<String> customTexts) throws TicketRuleViolationException {
        if (product == null) {
            throw new IllegalArgumentException("Error: Product cannot be null.");
        }
        // Rule: Cannot change a closed ticket.
        if (this.state == TicketState.CLOSED) {
            throw new TicketRuleViolationException("Error: Cannot add products to a closed ticket.");
        }
        // Change state to ACTIVE when adding first product.
        if (this.state == TicketState.EMPTY) {
            this.state = TicketState.ACTIVE;
        }

        // Rule: Bookable products can only be added once.
        if (product.isBookable()) {
            // They can only be added once.
            for (TicketLine<T> line : lines) {
                if (line.getProduct().getId() == product.getId()) {
                    throw new TicketRuleViolationException("Error: Bookable products can only be added once.");
                }
            }
            // Check planning time constraints
            product.validate();
        }

        // Logic: If product and text are same, increase quantity.
        for (TicketLine<T> currentLine : lines) {
            if (currentLine.getProduct().getId() == product.getId()) {
                // Check if customizations also match
                List<String> lineTexts = currentLine.getCustomTexts();      // This is never null, just empty
                boolean lineHasCustomTexts = !lineTexts.isEmpty();
                boolean newHasCustomTexts = (customTexts != null && !customTexts.isEmpty());

                if (!lineHasCustomTexts && !newHasCustomTexts) {
                    // CASE 1: Both are plain (no customs), so merge
                    currentLine.setQuantity(currentLine.getQuantity() + quantity);
                    return;
                }
                if (lineHasCustomTexts && newHasCustomTexts && lineTexts.equals(customTexts)) {
                    // CASE 2: Both have customs, and the lists are identical, so merge
                    currentLine.setQuantity(currentLine.getQuantity() + quantity);
                    return;
                }
            }
        }

        // Limit: Max 100 lines (E1).
        if (lines.size() >= MAX_PRODUCT_LINES) {
            throw new TicketRuleViolationException("Error: Ticket cannot have more than " + MAX_PRODUCT_LINES + " product lines.");
        }
        // After all checks, we can add the new line.
        TicketLine<T> newLine = new TicketLine<>(product, quantity);
        if (customTexts != null) {
            for (String text : customTexts) {
                newLine.addCustomText(text);
            }
        }
        lines.add(newLine);
    }

    public boolean removeProduct(int productId) throws TicketRuleViolationException {
        // Rule: Cannot modify closed ticket.
        if (this.state == TicketState.CLOSED) {
            throw new TicketRuleViolationException("Error: Cannot remove products from a closed ticket.");
        }

        for (int i = lines.size() - 1; i >= 0; i--) {
            if (lines.get(i).getProduct().getId() == productId) {
                lines.remove(i);
                return true;
            }
        }
        return false;
    }

    public double getTotalPrice() {
        double total = 0.0;

        for (TicketLine<T> line : lines) {
            // Calculate total price of all lines.
            total += line.getLineTotal();
        }
        return total;
    }

    public String print() throws TicketRuleViolationException {
        if (this.state != TicketState.CLOSED) {
            this.state = TicketState.CLOSED;
            this.id += "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy-MM-dd-HH:mm"));
        }
        Collections.sort(lines);
        return printStrategy.formatTicket(this);
    }

    public boolean isEmpty() {
        return lines.isEmpty();
    }

    @Override
    public int compareTo(Ticket<T> other) {
        return this.getId().compareToIgnoreCase(other.getId());
    }
}