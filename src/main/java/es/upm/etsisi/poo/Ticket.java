package es.upm.etsisi.poo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects; // For timestamps in ID
import java.util.Random; // For formatting timestamps
import java.util.Set; // For random part of ID

/**
 * Represents a single sales ticket for E2.
 */
public class Ticket { // Renamed from E1's Ticket logic

    // E2 requirements
    private String id;
    private final String cashierId;
    private final String clientId;
    private TicketState state;
    private LocalDateTime creationTime;
    private LocalDateTime closeTime; // Null until closed

    private List<Product> items; // Keep the list of products from E1

    private static final int MAX_ITEMS = 100; // E1 limit
    private static final Random randomGenerator = new Random();
    private static final DateTimeFormatter idFormatter = DateTimeFormatter.ofPattern("yy-MM-dd-HH:mm");
    private static final DateTimeFormatter closeFormatter = DateTimeFormatter.ofPattern("yy-MM-dd-HH:mm");

    // Enum for Ticket State as required in E2
    public enum TicketState {
        VACIO, // Empty or newly created
        ACTIVO, // Has items added
        CERRADO // Printed/Closed, cannot be modified
    }

    /**
     * Constructor for a new Ticket.
     * 
     * @param id            Optional initial ID, generates one if null/empty.
     * @param cashierId     ID of the cashier creating the ticket.
     * @param clientId      DNI of the client associated with the ticket.
     * @param usedTicketIds A set containing already used ticket IDs to ensure
     *                      uniqueness.
     */
    public Ticket(String id, String cashierId, String clientId, Set<String> usedTicketIds) {
        if (cashierId == null || cashierId.trim().isEmpty()) {
            throw new IllegalArgumentException("Error: Cashier ID cannot be empty for a ticket.");
        }
        if (clientId == null || clientId.trim().isEmpty()) {
            throw new IllegalArgumentException("Error: Client ID cannot be empty for a ticket.");
        }

        this.cashierId = cashierId;
        this.clientId = clientId;
        this.creationTime = LocalDateTime.now();
        this.state = TicketState.VACIO; // Starts empty
        this.items = new ArrayList<>();
        this.closeTime = null;

        if (id == null || id.trim().isEmpty()) {
            this.id = generateUniqueId(this.creationTime, usedTicketIds);
        } else {
            // Basic validation: Check if it starts like a timestamp format
            // We won't strictly enforce the random part check here, focus on uniqueness
            if (!id.matches("^\\d{2}-\\d{2}-\\d{2}-\\d{2}:\\d{2}-\\d{5}.*")) {
                System.out.println("Warning: Provided ticket ID format might be incorrect, but checking uniqueness.");
            }
            if (!usedTicketIds.add(id)) {
                throw new IllegalArgumentException("Error: Provided Ticket ID already exists.");
            }
            this.id = id; // Use provided ID
        }
    }

    /**
     * Generates a unique Ticket ID (YY-MM-dd-HH:mm-<5 random digits>).
     * 
     * @param creationTime Time of creation.
     * @param usedIdsSet   The set of already used IDs.
     * @return A unique Ticket ID.
     */
    private static String generateUniqueId(LocalDateTime creationTime, Set<String> usedIdsSet) {
        String baseId = creationTime.format(idFormatter) + "-";
        String newId;
        do {
            int randomNum = 10000 + randomGenerator.nextInt(90000); // Generate 5 digits
            newId = baseId + randomNum;
        } while (!usedIdsSet.add(newId)); // Ensure uniqueness and add to set
        return newId;
    }

    // --- Getters ---
    public String getId() {
        return id;
    }

    public String getCashierId() {
        return cashierId;
    }

    public String getClientId() {
        return clientId;
    }

    public TicketState getState() {
        return state;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public LocalDateTime getCloseTime() {
        return closeTime;
    }

    // --- Ticket Operations (Modified from E1) ---

    /**
     * Adds a product to the ticket. E2: Handles state changes.
     * TODO: E2 logic for custom products, meetings, food, quantity meaning.
     * 
     * @param prod     Product to add.
     * @param quantity Number of items (or people for events).
     * @return true if successful, false otherwise.
     */
    public boolean addProduct(Product prod, int quantity) {
        if (this.state == TicketState.CERRADO) {
            System.out.println("Error: Cannot add products to a closed ticket (ID: " + this.id + ").");
            return false;
        }
        if (items.size() + quantity > MAX_ITEMS) {
            System.out.println("Error: Ticket item limit (" + MAX_ITEMS + ") exceeded for ticket ID: " + this.id);
            return false;
        }
        // TODO: Add E2 checks:
        // - If prod is Meeting/Food, check if already exists.
        // - If prod is customizable, handle custom texts and price calculation.
        // - Check expiration dates for Meeting/Food relative to now or close time.

        for (int i = 0; i < quantity; i++) {
            items.add(prod);
        }
        this.state = TicketState.ACTIVO; // Ticket is now active
        // E2: Print provisional total after adding? The requirement says "Al
        // incorporar..."
        printProvisionalTotal();
        return true;
    }

    /**
     * Removes all occurrences of a product from the ticket. E2: Handles state
     * changes.
     * 
     * @param productId ID of the product to remove.
     * @return true if any items were removed, false otherwise.
     */
    public boolean removeProduct(int productId) {
        if (this.state == TicketState.CERRADO) {
            System.out.println("Error: Cannot remove products from a closed ticket (ID: " + this.id + ").");
            return false;
        }
        boolean removed = items.removeIf(prod -> prod.getId() == productId);

        if (removed && items.isEmpty()) {
            this.state = TicketState.VACIO; // Reverts to empty if last item removed
        }
        if (removed) {
            // E2: Print provisional total after removing
            printProvisionalTotal();
        }
        return removed;
    }

    /**
     * Closes the ticket. E2: Updates state and ID.
     * Called by printTicket command.
     * 
     * @param usedTicketIds The set of used IDs to ensure the new closed ID doesn't
     *                      clash.
     * @return true if closed successfully, false if already closed.
     */
    public boolean closeTicket(Set<String> usedTicketIds) {
        if (this.state == TicketState.CERRADO) {
            System.out.println("Error: Ticket (ID: " + this.id + ") is already closed.");
            return false;
        }
        // TODO: E2 Check time constraints for Meals/Meetings before closing
        // if (!checkEventTimeConstraints()) { return false; }

        this.closeTime = LocalDateTime.now();
        this.state = TicketState.CERRADO;

        // Update ID: Remove old ID from set, create new one, add new one to set
        usedTicketIds.remove(this.id);
        String closedIdSuffix = "-" + this.closeTime.format(closeFormatter);
        String potentialNewId = this.id + closedIdSuffix;

        // Handle potential (though unlikely) collision with the new ID format
        int attempt = 0;
        String finalNewId = potentialNewId;
        while (!usedTicketIds.add(finalNewId)) {
            attempt++;
            finalNewId = potentialNewId + "_v" + attempt; // Append version if collision occurs
            System.out.println("Warning: Ticket ID collision on close, generated alternative: " + finalNewId);
        }
        this.id = finalNewId; // Set the final, unique closed ID
        return true;
    }

    /**
     * Prints the ticket details (simulates generating the invoice).
     * E2: Needs to be called via TicketManager to handle closing.
     * This method now just formats the output. The closing logic is in
     * closeTicket().
     */
    public void printTicketDetails() {
        double totalPrice = calculateTotalPrice();
        double totalDiscount = calculateTotalDiscount();
        double finalPrice = totalPrice - totalDiscount;

        // Sort items alphabetically by name
        List<Product> sortedItems = new ArrayList<>(items);
        sortedItems.sort(Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER));

        System.out.println("--- TICKET INVOICE ---");
        System.out.println("Ticket ID: " + this.id);
        System.out.println("Cashier ID: " + this.cashierId);
        System.out.println("Client ID: " + this.clientId);
        System.out.println("Status: " + this.state);
        if (this.closeTime != null) {
            System.out.println("Closed at: " + this.closeTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
        System.out.println("--- Items ---");

        if (sortedItems.isEmpty()) {
            System.out.println("  (Ticket is empty)");
        } else {
            for (Product prod : sortedItems) {
                // TODO: Update printing for E2 details (custom texts, event info)
                double discount = getDiscountForProduct(prod); // Uses E1 logic for now
                if (discount > 0) {
                    System.out.printf("  %s **discount -%.1f%n", prod.toString(), discount);
                } else {
                    System.out.println("  " + prod.toString());
                }
            }
        }
        System.out.println("--- Summary ---");
        System.out.printf("Total price: %.1f%n", totalPrice);
        System.out.printf("Total discount: %.1f%n", totalDiscount);
        System.out.printf("Final Price: %.1f%n", finalPrice);
        System.out.println("--------------------");
    }

    /**
     * Prints the provisional total as required by E2 on add/remove.
     */
    private void printProvisionalTotal() {
        if (this.state == TicketState.CERRADO)
            return; // Don't print for closed tickets

        double totalPrice = calculateTotalPrice();
        double totalDiscount = calculateTotalDiscount();
        double finalPrice = totalPrice - totalDiscount;

        // E2 requirement: Print provisional totals and items with discounts
        System.out.println("--- Provisional Ticket Update ---");
        System.out.println("Ticket ID: " + this.id + " (Status: " + this.state + ")");
        List<Product> sortedItems = new ArrayList<>(items);
        sortedItems.sort(Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER));
        if (sortedItems.isEmpty()) {
            System.out.println("  (Ticket is now empty)");
        } else {
            for (Product prod : sortedItems) {
                double discount = getDiscountForProduct(prod);
                if (discount > 0) {
                    System.out.printf("  %s **discount -%.1f%n", prod.toString(), discount);
                } else {
                    System.out.println("  " + prod.toString());
                }
            }
        }
        System.out.printf("Provisional Total price: %.1f%n", totalPrice);
        System.out.printf("Provisional Total discount: %.1f%n", totalDiscount);
        System.out.printf("Provisional Final Price: %.1f%n", finalPrice);
        System.out.println("-----------------------------");
    }

    // --- Calculation Methods (Mostly E1 logic, need E2 updates) ---

    /**
     * Calculates total price before discounts. TODO: Needs E2 update for
     * custom/event prices.
     */
    private double calculateTotalPrice() {
        double suma = 0.0;
        for (Product prod : items) {
            // TODO: Adjust price calculation based on product type (custom, event per
            // person)
            suma += prod.getPrice();
        }
        return suma;
    }

    /** Calculates total discount. TODO: Needs E2 update for custom/event prices. */
    private double calculateTotalDiscount() {
        double suma = 0.0;
        // Need a way to avoid applying category discount multiple times if same product
        // added multiple times
        // This is complex with just a list. A Map<Product, Integer> might be better for
        // items.
        // For now, using E1 logic which might over-calculate discounts if quantity > 1
        // added separately.
        List<Product> processedForDiscount = new ArrayList<>(); // Track products already discounted
        for (Product prod : items) {
            boolean alreadyProcessed = false;
            for (Product p : processedForDiscount) {
                if (p.getId() == prod.getId()) { // Simple check by ID, might need equals()
                    alreadyProcessed = true;
                    break;
                }
            }
            if (!alreadyProcessed) {
                suma += getDiscountForProduct(prod) * Collections.frequency(items, prod); // Multiply discount by
                                                                                          // quantity
                processedForDiscount.add(prod);
            }
        }
        return suma;
    }

    /**
     * Calculates discount for a single instance of a product based on category
     * count. (E1 logic).
     */
    private double getDiscountForProduct(Product p) {
        // TODO: Ensure this doesn't apply to Food/Meeting products
        // if (p instanceof MealProduct || p instanceof MeetingProduct) return 0.0;

        int count = 0;
        for (Product item : items) {
            // Check if items have the same category (and are eligible for category
            // discount)
            if (item.getCategory() != null && item.getCategory() == p.getCategory()) {
                count++;
            }
        }

        if (count >= 2 && p.getCategory() != null) {
            // TODO: Adjust price based on product type before applying discount?
            return p.getPrice() * p.getCategory().getDiscount();
        }
        return 0.0;
    }

    // --- Helper Methods ---
    public boolean isEmpty() {
        return items.isEmpty();
    }

    // --- Overridden Object methods ---
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Ticket ticket = (Ticket) o;
        return Objects.equals(id, ticket.id); // Tickets are equal if ID is the same
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // Use ID for hash code
    }

    @Override
    public String toString() {
        // Basic toString, primarily for debugging or simple lists
        return String.format("Ticket{id='%s', state=%s, cashierId='%s', clientId='%s'}",
                id, state, cashierId, clientId);
    }
}