package es.upm.etsisi.poo.domain.product;

import java.time.LocalDateTime;

// Represents a Food product, as defined in E2.
public class Food extends Product {
    private static final int AUTO_GENERATE_ID = 0;      // Internal constant for auto-generation ID placeholder
    private static final int MAX_PARTICIPANTS = 100;    // E2 requirement: max participants cannot exceed 100

    private final int maxParticipants;
    private final LocalDateTime expirationDate;

    public Food(String name, double price, LocalDateTime expirationDate, int maxParticipants) {
        super(AUTO_GENERATE_ID, name, null, price);
        if (maxParticipants > MAX_PARTICIPANTS) {
            throw new IllegalArgumentException("Error: Maximum number of participants cannot exceed 100.");
        }
        this.expirationDate = expirationDate;
        this.maxParticipants = maxParticipants;
    }

    public Food(int id, String name, double price, LocalDateTime expirationDate, int maxParticipants) {
        super(id, name, null, price);
        if (maxParticipants > MAX_PARTICIPANTS) {
            throw new IllegalArgumentException("Error: Maximum number of participants cannot exceed 100.");
        }
        this.expirationDate = expirationDate;
        this.maxParticipants = maxParticipants;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    @Override
    public double getPrice() {
        return super.getPrice();
    }

    // Override copyWithNewId to create a Food with the new generated ID to ensure not creating a Product instance by mistake.
    @Override
    public Product copyWithNewId(int newId) {
        return new Food(newId, this.getName(), this.getPrice(), this.getExpirationDate(), this.getMaxParticipants());
    }
}