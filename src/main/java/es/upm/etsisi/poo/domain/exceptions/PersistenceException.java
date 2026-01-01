package es.upm.etsisi.poo.domain.exceptions;

public class PersistenceException extends UPMStoreDomainException {
    public PersistenceException(String message) {
        super(message);
    }

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
