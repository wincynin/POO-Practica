package es.upm.etsisi.poo.domain.product;

import java.time.LocalDateTime;

public enum EventType {
    FOOD(3),
    MEETING(0);

    private final int minPlanningDays;

    EventType(int minPlanningDays) {
        this.minPlanningDays = minPlanningDays;
    }

    public boolean isPlanningTimeValid(LocalDateTime expirationDate) {
        if (this == MEETING) {
            return LocalDateTime.now().plusHours(12).isBefore(expirationDate);
        }
        return LocalDateTime.now().plusDays(minPlanningDays).isBefore(expirationDate);
    }
}
