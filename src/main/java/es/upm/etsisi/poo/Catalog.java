
/**
 * Catalog class manages a collection of products.
 * Supports adding, removing, updating, and searching products by ID.
 * Maximum number of products is limited by MAX_PRODUCTS.
 */
package es.upm.etsisi.poo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a product catalog with a fixed maximum size.
 */
public class Catalog {
    /** Maximum number of products allowed in the catalog. */
    private static final int MAX_PRODUCTS = 200;

    /** Map storing products by their unique ID. */
    private final Map<Integer, Product> products;

    /**
     * Constructs an empty catalog.
     */
    public Catalog() {
        this.products = new HashMap<>();
    }

    /**
     * Adds a product to the catalog.
     *
     * @param product Product to add
     * @throws Exception if catalog is full or product ID already exists
     */
    public void addProduct(Product product) throws Exception {
        // Check if catalog is full
        if (products.size() >= MAX_PRODUCTS) {
            throw new Exception("Error: Cannot add product. Catalog is full.");
        }
        // Check for duplicate product ID
        if (products.containsKey(product.getId())) {
            throw new Exception("Error: Product with ID " + product.getId() + " already exists.");
        }
        products.put(product.getId(), product);
    }

    /**
     * Removes a product from the catalog by its ID.
     *
     * @param productId ID of the product to remove
     * @return the removed Product
     * @throws Exception if product ID is not found
     */
    public Product removeProduct(int productId) throws Exception {
        // Check if product exists
        if (!products.containsKey(productId)) {
            throw new Exception("Error: Product with ID " + productId + " not found.");
        }
        return products.remove(productId);
    }

    /**
     * Finds a product by its ID.
     *
     * @param productId ID of the product to find
     * @return the Product if found, otherwise null
     */
    public Product findProductById(int productId) {
        return products.get(productId);
    }

    /**
     * Returns a list of all products in the catalog.
     *
     * @return list of all products
     */
    public List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }

    /**
     * Updates a field of a product by its ID.
     *
     * @param productId ID of the product to update
     * @param fieldToUpdate Field to update (NAME, CATEGORY, or PRICE)
     * @param newValue New value for the field
     * @return the updated Product
     * @throws Exception if product not found, field invalid, or value invalid
     */
    public Product updateProduct(int productId, String fieldToUpdate, String newValue) throws Exception {
        Product product = findProductById(productId);
        if (product == null) {
            throw new Exception("Error: Product with ID " + productId + " not found.");
        }

        // Update the specified field
        switch (fieldToUpdate.toUpperCase()) {
            case "NAME" -> product.setName(newValue);
            case "CATEGORY" -> {
                try {
                    ProductCategory category = ProductCategory.valueOf(newValue.toUpperCase());
                    product.setCategory(category);
                } catch (IllegalArgumentException e) {
                    throw new Exception("Error: Invalid category '" + newValue + "'.");
                }
            }
            case "PRICE" -> {
                try {
                    double price = Double.parseDouble(newValue);
                    product.setPrice(price);
                } catch (NumberFormatException e) {
                    throw new Exception("Error: Invalid price format '" + newValue + "'.");
                }
            }
            default -> throw new Exception("Error: Invalid field '" + fieldToUpdate + "'. Can only be NAME, CATEGORY, or PRICE.");
        }
        return product;
    }
}