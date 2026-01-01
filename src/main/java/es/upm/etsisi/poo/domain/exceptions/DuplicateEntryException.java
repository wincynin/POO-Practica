package es.upm.etsisi.poo.domain.exceptions;

// [Exception] Thrown when an ID already exists.
public class DuplicateEntryException extends UPMStoreDomainException {
    public DuplicateEntryException(String message) {
        super(message);
    }
}