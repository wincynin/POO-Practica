package es.upm.etsisi.poo.domain.product;

import java.time.LocalDateTime;

// Represents event types with their planning time requirements as defined by E2.
public enum EventType {
    FOOD(3),
    MEETING(0);

    private final int minPlanningDays;
    private static final int MIN_MEETING_PLANNING_HOURS = 12;

    EventType(int minPlanningDays) {
        this.minPlanningDays = minPlanningDays;
    }

    // Returns true if the planning time requirement is met for the given expiration date.
    public boolean isPlanningTimeValid(LocalDateTime expirationDate) {
        if (this == MEETING) {
            return LocalDateTime.now().plusHours(MIN_MEETING_PLANNING_HOURS).isBefore(expirationDate);
        }
        return LocalDateTime.now().plusDays(minPlanningDays).isBefore(expirationDate);
    }
}