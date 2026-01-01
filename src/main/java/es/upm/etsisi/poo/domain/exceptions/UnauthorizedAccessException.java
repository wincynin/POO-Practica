package es.upm.etsisi.poo.domain.exceptions;

// [Exception] Security error (e.g., wrong cashier).
public class UnauthorizedAccessException extends UPMStoreDomainException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}