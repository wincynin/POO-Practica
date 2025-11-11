package es.upm.etsisi.poo.common;

// Custom exception to signal that a product ID was not found in the catalog.
// This is used by Catalog.getProduct() and handled by CommandHandler, Store, and IdGenerator.
public class ProductNotFoundException extends Exception {
    public ProductNotFoundException(String message) {
        super(message);
    }
}