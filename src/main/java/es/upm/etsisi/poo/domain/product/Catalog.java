package es.upm.etsisi.poo.domain.product;

import java.util.List;
import java.util.ArrayList;

import es.upm.etsisi.poo.common.ProductNotFoundException;

// Represents a catalog of products, as defined in E1 and E2, manages adding, removing, updating, and retrieving products.
public class Catalog {
    private final List<Product> products;
    private static final int MAX_PRODUCTS = 200;    // E1 requirement: max products cannot exceed 200


    @SuppressWarnings("Convert2Diamond")
    public Catalog() {
        this.products = new ArrayList<Product>();
    }

    public void addProduct(Product prod) throws IllegalArgumentException {
        if (products.size() >= MAX_PRODUCTS) {
            throw new IllegalArgumentException("Error: Maximum number of products reached.");
        }

        // This try enforces E1 requirement: there cannot be two products with the same ID.
        try {
            getProduct(prod.getId());
            throw new IllegalArgumentException("Error: A product with that ID already exists.");
        } catch (ProductNotFoundException e) {
            products.add(prod);
        }
    }

    public Product removeProduct(int id) throws ProductNotFoundException {
        Product productToRemove = getProduct(id);
        products.remove(productToRemove);
        return productToRemove;
    }

    public void updateProduct(int id, String field, String value)
            throws ProductNotFoundException, IllegalArgumentException {
        Product prod = getProduct(id);

        switch (field.toUpperCase()) {
            case "NAME":
                prod.setName(value);
                break;
            case "CATEGORY":
                try {
                    ProductCategory newCategory = ProductCategory.valueOf(value.toUpperCase());
                    prod.setCategory(newCategory);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Error: Invalid category");
                }
                break;
            case "PRICE":
                try {
                    double newPrice = Double.parseDouble(value);
                    prod.setPrice(newPrice);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Error: Invalid price");
                }
                break;
            default:
                throw new IllegalArgumentException("Error: Invalid field");
        }
    }

    public boolean isEmpty() {
        return products.isEmpty();
    }

    public int getSize() {
        return products.size();
    }

    public Product getProduct(int id) throws ProductNotFoundException {
        for (Product product : products) {
            if (product.getId() == id) {
                return product;
            }
        }
        throw new ProductNotFoundException("Product with ID " + id + " not found.");
    }

    public List<Product> getProducts() {
        return products;
    }
}