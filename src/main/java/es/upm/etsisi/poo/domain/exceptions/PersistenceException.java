package es.upm.etsisi.poo.domain.exceptions;

// [Exception] Wrapper for file IO errors.
public class PersistenceException extends UPMStoreDomainException {
    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}