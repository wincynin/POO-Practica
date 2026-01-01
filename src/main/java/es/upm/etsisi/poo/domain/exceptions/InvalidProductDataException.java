package es.upm.etsisi.poo.domain.exceptions;

// [Exception] Thrown for bad product inputs (e.g., negative price).
public class InvalidProductDataException extends UPMStoreDomainException {
    public InvalidProductDataException(String message) {
        super(message);
    }
}