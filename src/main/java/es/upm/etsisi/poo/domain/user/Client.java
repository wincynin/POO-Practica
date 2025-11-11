package es.upm.etsisi.poo.domain.user;

// Represents a client, as defined in E2.
public class Client extends User {
    private final String cashierId;

    public Client(String id, String name, String email, String cashierId) {
        super(id, name, email);
        if (cashierId == null || cashierId.isEmpty()) {
            throw new IllegalArgumentException("Error: Cashier ID cannot be null or empty.");
        }
        this.cashierId = cashierId;
    }

    public String getCashierId() {
        return cashierId;
    }

    @Override
    public String toString() {
        // Returns a string representation of the Client, including id, name, email and cashierId, in that order.
        return String.format("{class: Client, id: '%s', name: '%s', email: '%s', cashierId: '%s'}",
                getId(), getName(), getEmail(), getCashierId());
    }
}