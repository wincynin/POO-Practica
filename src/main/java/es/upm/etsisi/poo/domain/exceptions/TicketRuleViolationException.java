package es.upm.etsisi.poo.domain.exceptions;

// [Exception] Thrown when breaking business rules (e.g., editing closed ticket).
public class TicketRuleViolationException extends UPMStoreDomainException {
    public TicketRuleViolationException(String message) {
        super(message);
    }
}