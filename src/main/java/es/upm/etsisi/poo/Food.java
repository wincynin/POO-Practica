package es.upm.etsisi.poo;

import java.time.LocalDateTime;

public class Food extends Product {
    private final LocalDateTime expirationDate;
    private final int maxParticipants;

    public Food(String id, String name, double price, LocalDateTime expirationDate, int maxParticipants) {
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

    @Override
    public double getPrice() {
        return super.getPrice();
    }
}