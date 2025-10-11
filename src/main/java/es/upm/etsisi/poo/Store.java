package es.upm.etsisi.poo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages the store's inventory of products.
 * This class is responsible for adding, removing, updating, and finding products,
 * enforcing a maximum capacity and ensuring unique product IDs.
 */
public class Store {
    private static final int MAX_PRODUCTS = 200;
    private final Map<Integer, Product> products;

    /**
     * Constructs an empty Store, initializing the product inventory.
     */
    public Store() {
        this.products = new HashMap<>();
    }

    /**
     * Adds a product to the store's inventory.
     * The method checks for capacity limits and duplicate IDs before adding.
     *
     * @param product The Product object to add. Must not be null.
     * @return {@code true} if the product was added successfully, {@code false} otherwise.
     */
    public boolean addProduct(Product product) {
        if (products.size() >= MAX_PRODUCTS) {
            System.out.println("Error: Cannot add product. Store inventory is full.");
            return false;
        }
        if (products.containsKey(product.getId())) {
            System.out.println("Error: Product with ID " + product.getId() + " already exists.");
            return false;
        }
        products.put(product.getId(), product);
        return true;
    }

    /**
     * Removes a product from the inventory using its unique ID.
     *
     * @param productId The ID of the product to remove.
     * @return The removed {@code Product} object if found, otherwise {@code null}.
     */
    public Product removeProduct(int productId) {
        if (!products.containsKey(productId)) {
            System.out.println("Error: Product with ID " + productId + " not found.");
            return null;
        }
        return products.remove(productId);
    }

    /**
     * Finds and retrieves a product from the inventory by its ID.
     *
     * @param productId The ID of the product to find.
     * @return The {@code Product} object if found, otherwise {@code null}.
     */
    public Product findProductById(int productId) {
        return products.get(productId);
    }

    /**
     * Retrieves a list of all products currently in the store's inventory.
     *
     * @return A {@code List<Product>} containing all products. The list will be empty if the store has no products.
     */
    public List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }

    /**
     * Updates a specific field of a product identified by its ID.
     *
     * @param productId     The ID of the product to update.
     * @param fieldToUpdate The name of the field to update (e.g., "NAME", "CATEGORY", "PRICE").
     * @param newValue      The new value for the specified field.
     * @return {@code true} if the update was successful, {@code false} otherwise.
     */
    public boolean updateProduct(int productId, String fieldToUpdate, String newValue) {
        Product product = findProductById(productId);
        if (product == null) {
            System.out.println("Error: Product with ID " + productId + " not found.");
            return false;
        }

        switch (fieldToUpdate.toUpperCase()) {
            case "NAME":
                return product.setName(newValue);
            case "CATEGORY":
            // Validate and parse the new category
                ProductCategory category = null;
                for (ProductCategory cat : ProductCategory.values()) {
                    if (cat.name().equalsIgnoreCase(newValue)) {
                        category = cat;
                        break;
                    }
                }
                if (category != null) {
                    return product.setCategory(category);
                } else {
                    System.out.println("Error: Invalid category '" + newValue + "'.");
                    return false;
                }
            case "PRICE":
            // Validate and parse the new price
                try {
                    double price = Double.parseDouble(newValue);
                    return product.setPrice(price);
                } catch (NumberFormatException e) {
                    System.out.println("Error: Invalid price format '" + newValue + "'.");
                    return false;
                }
            default:
                System.out.println("Error: Invalid field '" + fieldToUpdate + "'. Can only be NAME, CATEGORY, or PRICE.");
                return false;
        }
    }
}