package es.upm.etsisi.poo.domain.exceptions;

// [Exception] Thrown when searching for an ID that doesn't exist.
public class ResourceNotFoundException extends UPMStoreDomainException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}