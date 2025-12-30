package es.upm.etsisi.poo.domain.ticket;

import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import es.upm.etsisi.poo.domain.product.*;
import es.upm.etsisi.poo.domain.user.Cashier;
import es.upm.etsisi.poo.domain.user.Client;
import es.upm.etsisi.poo.infrastructure.printing.PrintStrategy;
import es.upm.etsisi.poo.infrastructure.printing.StandardPrintStrategy;


// Represents a ticket, as defined in E1 and E2.
public abstract class Ticket<T extends Product> implements Comparable<Ticket<T>>, java.io.Serializable {
    private String id;
    private TicketState state;
    private final List<TicketLine<T>> lines;
    private final Client client;
    private final Cashier cashier;
    private PrintStrategy printStrategy;
    private static final int MAX_PRODUCT_LINES = 100;

    @SuppressWarnings("Convert2Diamond")
    public Ticket(String id, Client client, Cashier cashier) {
        this.state = TicketState.EMPTY;
        this.lines = new ArrayList<TicketLine<T>>();
        this.client = client;
        this.cashier = cashier;
        this.printStrategy = new StandardPrintStrategy(); // Default strategy

        if (id == null) {
            // If the ID is null, it is autogenerate, as requested.
            this.id = generateId();
        } else {
            this.id = id;
        }
    }

    private String generateId() {
        // The ID format must be "YY-MM-dd-HH:mm-" + 5 random digits as requested.
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

    public Client getClient() {
        return client;
    }

    public Cashier getCashier() {
        return cashier;
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

    public void addProduct(T product, int quantity) {
        addProduct(product, quantity, null);
    }

    public void addProduct(T product, int quantity, List<String> customTexts) {
        if (product == null) {
            throw new IllegalArgumentException("Error: Product cannot be null.");
        }
        // Check for closed tickets as they are required to be immutable after closing.
        if (this.state == TicketState.CLOSED) {
            throw new IllegalStateException("Error: Cannot add products to a closed ticket.");
        }
        // Check for empty tickets and set to active when adding the first product.
        if (this.state == TicketState.EMPTY) {
            this.state = TicketState.ACTIVE;
        }

        // Special rules for Bookable products.
        if (product.isBookable()) {
            // They can only be added once.
            for (TicketLine<T> line : lines) {
                if (line.getProduct().getId() == product.getId()) {
                    throw new IllegalStateException("Error: Bookable products can only be added once.");
                }
            }
            // Check planning time constraints
            product.validate();
        }

        // We now check for matching ID AND matching customizations, to merge quantities them as requested.
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

        // We respect the E1 rule of max 100 items per ticket.
        if (lines.size() >= MAX_PRODUCT_LINES) {
            throw new IllegalStateException("Error: Ticket cannot have more than " + MAX_PRODUCT_LINES + " product lines.");
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

    public boolean removeProduct(int productId) {
        // Check for closed tickets as they are required to be immutable after closing.
        if (this.state == TicketState.CLOSED) {
            throw new IllegalStateException("Error: Cannot remove products from a closed ticket.");
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
            // The total is based on getLineTotal(), which already includes the customization surcharges.
            total += line.getLineTotal();
        }
        return total;
    }

    public String print() {
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