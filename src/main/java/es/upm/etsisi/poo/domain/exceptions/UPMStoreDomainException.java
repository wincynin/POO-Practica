package es.upm.etsisi.poo.domain.exceptions;

// [Exception] Base class for all custom project errors.
public class UPMStoreDomainException extends RuntimeException {
    public UPMStoreDomainException(String message) {
        super(message);
    }

    public UPMStoreDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}