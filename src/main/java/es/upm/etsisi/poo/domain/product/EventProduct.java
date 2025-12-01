package es.upm.etsisi.poo.domain.product;

import java.time.LocalDateTime;

public class EventProduct extends BookableProduct {
    private final EventType type;

    public EventProduct(String name, double price, LocalDateTime expirationDate, int maxParticipants, EventType type) {
        super(name, price, expirationDate, maxParticipants);
        this.type = type;
    }

    // Constructor that accepts ID
    public EventProduct(int id, String name, double price, LocalDateTime expirationDate, int maxParticipants, EventType type) {
        super(id, name, price, expirationDate, maxParticipants);
        this.type = type;
    }

    public EventType getType() {
        return type;
    }

    @Override
    public void validate() {
        // Validation logic based on type (Food vs Meeting)
        if (type == EventType.FOOD) {
            // Food: planned at least 3 days in advance
            if (getExpirationDate().isBefore(LocalDateTime.now().plusDays(3))) {
                throw new IllegalStateException("Error: Food products must be planned at least 3 days in advance.");
            }
        } else if (type == EventType.MEETING) {
            // Meeting: planned at least 12 hours in advance
            if (getExpirationDate().isBefore(LocalDateTime.now().plusHours(12))) {
                throw new IllegalStateException("Error: Meeting products must be planned at least 12 hours in advance.");
            }
        }
    }

    @Override
    public void validateTicketAddition() {
        // Reuse the validation logic when adding to ticket
        validate();
    }

    @Override
    public boolean isBookable() {
        return true;
    }
    
    @Override
    public String toString() {
        return String.format("{class: %s, id:%d, name:'%s', price:%.1f, type:%s, expiration:%s}", 
            getClass().getSimpleName(), getId(), getName(), getPrice(), type, getExpirationDate().toString());
    }
}