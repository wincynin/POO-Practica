package es.upm.etsisi.poo.domain.exceptions;

public class UnauthorizedAccessException extends UPMStoreDomainException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
