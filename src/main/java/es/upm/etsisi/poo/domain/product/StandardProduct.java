package es.upm.etsisi.poo.domain.product;

import java.util.Collections;
import java.util.List;

import es.upm.etsisi.poo.domain.exceptions.InvalidProductDataException;

// Represents a standard product in the system.
public class StandardProduct extends Product {

    public StandardProduct(String name, ProductCategory category, double price) throws InvalidProductDataException {
        super(name, category, price);
        validateStandardProduct(name, price);
    }

    public StandardProduct(String id, String name, ProductCategory category, double price) throws InvalidProductDataException {
        super(id, name, category, price);
        validateStandardProduct(name, price);
    }

    private void validateStandardProduct(String name, double price) {
        if (name == null || name.trim().isEmpty() || name.length() > MAX_NAME_LENGTH) {
            throw new InvalidProductDataException("Error: Name cannot be empty or exceed " + MAX_NAME_LENGTH + " characters.");
        }
        if (price <= 0) {
            throw new InvalidProductDataException("Error: Price must be greater than 0.");
        }
    }

    @Override
    public List<String> getCustomTexts() {
        return Collections.emptyList();
    }

    @Override
    public void addCustomText(List<String> customTexts, String text) {
        throw new IllegalStateException("Error: This product is not customizable.");
    }

    @Override
    public double getLineTotal(int quantity, List<String> customTexts) {
        return getPrice() * quantity;
    }

    @Override
    public boolean isBookable() {
        return false;
    }

    @Override
    public void validate() {
        // Standard products have no specific validation logic.
    }
}