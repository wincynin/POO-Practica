package es.upm.etsisi.poo.domain.exceptions;

// [Exception] Thrown when Product doesn't fit the Ticket type.
public class TicketTypeMismatchException extends UPMStoreDomainException {
    public TicketTypeMismatchException(String message) {
        super(message);
    }
}