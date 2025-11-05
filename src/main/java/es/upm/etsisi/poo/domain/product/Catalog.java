package es.upm.etsisi.poo.domain.product;

import java.util.ArrayList;
import java.util.List;

import es.upm.etsisi.poo.common.ProductNotFoundException;

public class Catalog {
    private static final int MAX_PRODUCTS = 200;
    private final List<Product> products;

    @SuppressWarnings("Convert2Diamond")
    public Catalog() {
        this.products = new ArrayList<Product>();
    }

    public void addProduct(Product prod) throws IllegalArgumentException {
        if (products.size() >= MAX_PRODUCTS) {
            throw new IllegalArgumentException("Error: Maximum number of products reached.");
        }
        try {
            getProduct(prod.getId());
            // If we get here, the product exists
            throw new IllegalArgumentException("Error: A product with that ID already exists.");
        } catch (ProductNotFoundException e) {
            // This is the expected case, the product does not exist, so we can add it.
            products.add(prod);
        }
    }

    public Product removeProduct(int id) throws ProductNotFoundException {
        Product productToRemove = getProduct(id);
        products.remove(productToRemove);
        return productToRemove;
    }

    public void updateProduct(int id, String field, String value) throws ProductNotFoundException, IllegalArgumentException {
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
                    if (newPrice <= 0) {
                        throw new IllegalArgumentException("Error: Price must be > 0");
                    }
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
