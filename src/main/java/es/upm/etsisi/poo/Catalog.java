package es.upm.etsisi.poo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Catalog {
    private static final int MAX_PRODUCTS = 200;

    private final Map<Integer, Product> products;

    public Catalog() {
        this.products = new HashMap<>();
    }

    public void addProduct(Product product) throws Exception {
        if (products.size() >= MAX_PRODUCTS) {
            throw new Exception("Error: Cannot add product. Catalog is full.");
        }
        if (products.containsKey(product.getId())) {
            throw new Exception("Error: Product with ID " + product.getId() + " already exists.");
        }
        products.put(product.getId(), product);
    }

    public Product removeProduct(int productId) throws Exception {
        if (!products.containsKey(productId)) {
            throw new Exception("Error: Product with ID " + productId + " not found.");
        }
        return products.remove(productId);
    }

    public Product findProductById(int productId) {
        return products.get(productId);
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }

    public Product updateProduct(int productId, String fieldToUpdate, String newValue) throws Exception {
        Product product = findProductById(productId);
        if (product == null) {
            throw new Exception("Error: Product with ID " + productId + " not found.");
        }

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