package es.upm.etsisi.poo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set; // Needed if we track IDs here

/**
 * Represents a cashier in the store.
 */
public class Cashier {
    private String id; // Unique identifier (UW + 7 digits)
    private String name;
    private String email; // Company email
    private List<String> createdTicketIds; // Store IDs of tickets created by this cashier

    // We need a way to generate unique IDs. A static Random object is useful.
    private static final Random randomGenerator = new Random();
    // A static Set could track used IDs to ensure uniqueness, maybe managed
    // elsewhere.
    // private static Set<String> usedIds = new HashSet<>();

    /**
     * Constructor for Cashier. Auto-generates ID if null is provided.
     * 
     * @param id         Cashier's ID (UW + 7 digits) or null to auto-generate.
     * @param name       Cashier's name.
     * @param email      Cashier's company email.
     * @param usedIdsSet A set containing already used cashier IDs to ensure
     *                   uniqueness.
     */
    public Cashier(String id, String name, String email, Set<String> usedIdsSet) {
        if (name == null || name.trim().isEmpty() || name.length() > 100) {
            throw new IllegalArgumentException("Error: Cashier name is invalid.");
        }
        if (email == null || email.trim().isEmpty()) { // Add company email validation later
            throw new IllegalArgumentException("Error: Cashier email is invalid.");
        }

        if (id == null || id.trim().isEmpty()) {
            this.id = generateUniqueId(usedIdsSet); // Auto-generate
        } else {
            // Validate provided ID format (UW + 7 digits) - Simple check for now
            if (!id.matches("^UW\\d{7}$")) {
                throw new IllegalArgumentException(
                        "Error: Invalid Cashier ID format. Must be UW followed by 7 digits.");
            }
            if (!usedIdsSet.add(id)) { // Try to add the provided ID to the set
                throw new IllegalArgumentException("Error: Provided Cashier ID already exists.");
            }
            this.id = id; // Use provided ID
        }

        this.name = name;
        this.email = email;
        this.createdTicketIds = new ArrayList<>();
    }

    /**
     * Generates a unique Cashier ID (UW + 7 random digits).
     * 
     * @param usedIdsSet The set of already used IDs.
     * @return A unique Cashier ID.
     */
    private String generateUniqueId(Set<String> usedIdsSet) {
        String newId;
        do {
            int randomNum = 1000000 + randomGenerator.nextInt(9000000); // Generate 7 digits
            newId = "UW" + randomNum;
        } while (!usedIdsSet.add(newId)); // Ensure uniqueness and add to set
        return newId;
    }

    // --- Getters ---
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getCreatedTicketIds() {
        return new ArrayList<>(createdTicketIds); // Return a copy
    }

    // --- Ticket Management ---
    public void addCreatedTicketId(String ticketId) {
        if (ticketId != null && !ticketId.trim().isEmpty()) {
            this.createdTicketIds.add(ticketId);
        }
    }

    // Method to remove tickets when cashier is deleted (implementation detail
    // needed)
    public void removeAllTickets() {
        // Logic to interact with Ticket management system to delete tickets
        // For now, just clear the local list
        this.createdTicketIds.clear();
        System.out.println("DEBUG: Tickets for cashier " + this.id + " marked for removal."); // Placeholder
    }

    // --- Overridden methods ---
    @Override
    public String toString() {
        // Format needed for "cash list" (doesn't show tickets)
        return String.format("Cashier{id='%s', name='%s', email='%s'}", id, name, email);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Cashier cashier = (Cashier) o;
        return Objects.equals(id, cashier.id); // Cashiers are equal if their ID is the same
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // Use ID for hash code
    }
}