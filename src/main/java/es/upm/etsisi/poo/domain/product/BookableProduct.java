package es.upm.etsisi.poo.domain.product;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import es.upm.etsisi.poo.domain.exceptions.InvalidProductDataException;

// [Class] Product type: Bookable.
public abstract class BookableProduct extends Product {
    private final int maxParticipants;
    private final LocalDateTime expirationDate;

    public BookableProduct(String name, double price, LocalDateTime expirationDate, int maxParticipants) throws InvalidProductDataException {
        // E2 Requirement: These products do not have a category, so we pass null.
        super(name, null, price);
        this.expirationDate = expirationDate;
        this.maxParticipants = maxParticipants;
    }

    public BookableProduct(String id, String name, double price, LocalDateTime expirationDate, int maxParticipants) throws InvalidProductDataException {
        // E2 Requirement: These products do not have a category, so we pass null.
        super(id, name, null, price);
        this.expirationDate = expirationDate;
        this.maxParticipants = maxParticipants;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    @Override
    public List<String> getCustomTexts() {
        return Collections.emptyList();
    }

    @Override
    public void addCustomText(List<String> customTexts, String text) {
        throw new UnsupportedOperationException("Bookable products do not support custom texts.");
    }

    @Override
    public double getLineTotal(int quantity, List<String> customTexts) {
        return getPrice() * quantity;
    }

    @Override
    public boolean isBookable() {
        return true;
    }

    // Abstract method to be implemented by subclasses (Food/Meeting)
    @Override
    public abstract void validate();
    
    // Abstract method for ticket addition policy
    public abstract void validateTicketAddition() throws InvalidProductDataException;
}