package es.upm.etsisi.poo;

import java.time.LocalDateTime;

public class Food extends Product {
    private final LocalDateTime expirationDate;
    private final int maxParticipants;

    public Food(String name, double price, LocalDateTime expirationDate, int maxParticipants) {
        super(0, name, null, price);
        if (maxParticipants > 100) {
            throw new IllegalArgumentException("Error: Maximum number of participants cannot exceed 100.");
        }
        this.expirationDate = expirationDate;
        this.maxParticipants = maxParticipants;
    }

    public Food(int id, String name, double price, LocalDateTime expirationDate, int maxParticipants) {
        super(id, name, null, price);
        if (maxParticipants > 100) {
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

    @Override
    public Product copyWithNewId(int newId) {
        return new Food(newId, this.getName(), this.getPrice(), this.getExpirationDate(), this.getMaxParticipants());
    }
}