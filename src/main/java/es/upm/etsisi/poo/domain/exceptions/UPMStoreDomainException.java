package es.upm.etsisi.poo.domain.exceptions;

public class UPMStoreDomainException extends Exception {
    public UPMStoreDomainException(String message) {
        super(message);
    }

    public UPMStoreDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
