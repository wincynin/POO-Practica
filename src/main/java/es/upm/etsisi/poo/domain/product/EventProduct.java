package es.upm.etsisi.poo.domain.product;

import java.time.LocalDateTime;

public class EventProduct extends BookableProduct {
    private static final int MAX_PARTICIPANTS = 100;

    private final int maxParticipants;
    private final LocalDateTime expirationDate;
    private final EventType eventType;

    public EventProduct(String name, double price, LocalDateTime expirationDate, int maxParticipants, EventType eventType) {
        super(name, price);
        if (maxParticipants > MAX_PARTICIPANTS) {
            throw new IllegalArgumentException("Error: Maximum number of participants cannot exceed " + MAX_PARTICIPANTS);
        }
        this.expirationDate = expirationDate;
        this.maxParticipants = maxParticipants;
        this.eventType = eventType;
    }

    public void validate() {
        if (!eventType.isPlanningTimeValid(expirationDate)) {
            throw new IllegalStateException("Error: Planning time for " + eventType + " is not met.");
        }
    }

    @Override
    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public EventType getEventType() {
        return eventType;
    }
}
