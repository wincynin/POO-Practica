package es.upm.etsisi.poo.domain.product;

import java.time.LocalDateTime;

public abstract class BookableProduct extends Product {
    private final LocalDateTime expirationDate;
    private final int maxParticipants;

    public BookableProduct(String name, double price, LocalDateTime expirationDate, int maxParticipants) {
        // E2 Requirement: These products do not have a category, so we pass null.
        super(name, null, price);
        this.expirationDate = expirationDate;
        this.maxParticipants = maxParticipants;
    }

    public BookableProduct(int id, String name, double price, LocalDateTime expirationDate, int maxParticipants) {
        // E2 Requirement: These products do not have a category, so we pass null.
        super(id, name, null, price);
        this.expirationDate = expirationDate;
        this.maxParticipants = maxParticipants;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }
    
    // Abstract method to be implemented by subclasses (Food/Meeting)
    @Override
    public abstract void validate();
    
    // Abstract method for ticket addition policy
    public abstract void validateTicketAddition();
}