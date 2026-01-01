package es.upm.etsisi.poo.domain.product;

import es.upm.etsisi.poo.domain.exceptions.InvalidProductDataException;
import java.util.List;
import java.util.Collections;

// Represents a standard product in the system.
public class StandardProduct extends Product {

    public StandardProduct(String name, ProductCategory category, double price) throws InvalidProductDataException {
        super(name, category, price);
    }

    public StandardProduct(int id, String name, ProductCategory category, double price) throws InvalidProductDataException {
        super(id, name, category, price);
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