package es.upm.etsisi.poo;

import java.util.Objects;
// We might need these later for ticket management and ID generation
// import java.util.Random;
// import java.util.Set;
// import java.util.HashSet;

/**
 * Represents a client in the store.
 */
public class Client {
    private String name;
    private String dni; // Unique identifier for the client
    private String email;
    private String cashierId; // ID of the cashier who registered the client
    // private List<String> ticketIds; // To store associated ticket IDs later

    /**
     * Constructor for Client.
     * 
     * @param name      Client's name.
     * @param dni       Client's DNI (must be unique).
     * @param email     Client's email.
     * @param cashierId ID of the registering cashier.
     */
    public Client(String name, String dni, String email, String cashierId) {
        // Basic validation (can be expanded)
        if (name == null || name.trim().isEmpty() || name.length() > 100) {
            throw new IllegalArgumentException("Error: Client name is invalid.");
        }
        if (dni == null || dni.trim().isEmpty()) { // Add more specific DNI validation later
            throw new IllegalArgumentException("Error: Client DNI is invalid.");
        }
        if (email == null || email.trim().isEmpty()) { // Add email format validation later
            throw new IllegalArgumentException("Error: Client email is invalid.");
        }
        if (cashierId == null || cashierId.trim().isEmpty()) {
            throw new IllegalArgumentException("Error: Registering Cashier ID is invalid.");
        }

        this.name = name;
        this.dni = dni;
        this.email = email;
        this.cashierId = cashierId;
        // this.ticketIds = new ArrayList<>(); // Initialize later
    }

    // --- Getters ---
    public String getName() {
        return name;
    }

    public String getDni() {
        return dni;
    }

    public String getEmail() {
        return email;
    }

    public String getCashierId() {
        return cashierId;
    }

    // --- Overridden methods ---
    @Override
    public String toString() {
        // Format required by "client list"
        return String.format("Client{name='%s', dni='%s', email='%s', createdByCashier='%s'}",
                name, dni, email, cashierId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Client client = (Client) o;
        return Objects.equals(dni, client.dni); // Clients are equal if their DNI is the same
    }

    @Override
    public int hashCode() {
        return Objects.hash(dni); // Use DNI for hash code
    }

    // --- Methods for ticket management (to be added later) ---
    // public void addTicketId(String ticketId) { ... }
    // public List<String> getTicketIds() { ... }
}