package es.upm.etsisi.poo.domain.product;

import java.time.LocalDateTime;

import es.upm.etsisi.poo.domain.exceptions.InvalidProductDataException;

// [Class] Product type: Event.
public class EventProduct extends BookableProduct {
    private static final int MIN_FOOD_PLANNING_DAYS = 3;
    private static final int MIN_MEETING_PLANNING_HOURS = 12;

    private final EventType type;

    public EventProduct(String name, double price, LocalDateTime expirationDate, int maxParticipants, EventType type) throws InvalidProductDataException {
        super(name, price, expirationDate, maxParticipants);
        this.type = type;
    }

    // Constructor that accepts ID
    public EventProduct(String id, String name, double price, LocalDateTime expirationDate, int maxParticipants, EventType type) throws InvalidProductDataException {
        super(id, name, price, expirationDate, maxParticipants);
        this.type = type;
    }

    public EventType getType() {
        return type;
    }

    @Override
    public void validate() throws InvalidProductDataException {
        // Validation logic based on type (Food vs Meeting)
        if (type == EventType.FOOD) {
            // Food: planned at least 3 days in advance
            if (getExpirationDate().isBefore(LocalDateTime.now().plusDays(MIN_FOOD_PLANNING_DAYS))) {
                throw new InvalidProductDataException("Error: Food products must be planned at least " + MIN_FOOD_PLANNING_DAYS + " days in advance.");
            }
        } else if (type == EventType.MEETING) {
            // Meeting: planned at least 12 hours in advance
            if (getExpirationDate().isBefore(LocalDateTime.now().plusHours(MIN_MEETING_PLANNING_HOURS))) {
                throw new InvalidProductDataException("Error: Meeting products must be planned at least " + MIN_MEETING_PLANNING_HOURS + " hours in advance.");
            }
        }
    }

    @Override
    public void validateTicketAddition() throws InvalidProductDataException {
        // Reuse the validation logic when adding to ticket
        validate();
    }

    @Override
    public boolean isBookable() {
        return true;
    }

    @Override
    public boolean isService() {
        return false;
    }

    @Override
    public String getExpirationDetails() {
        return ", Date: " + this.getExpirationDate().toString();
    }

    @Override
    public String getPrintablePriceDetails() {
        return String.format("Price: %.2f", getPrice());
    }
    
    @Override
    public String toString() {
        return String.format("{class: %s, id:%s, name:'%s', price:%.1f, type:%s, expiration:%s}",
            getClass().getSimpleName(), getId(), getName(), getPrice(), type, getExpirationDate().toString());
    }
}