package es.upm.etsisi.poo.domain.product;

import java.util.List;
import java.util.ArrayList;

// [Class] Manages the list of Products.
public class Catalog implements java.io.Serializable {
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
        products.add(prod);
    }

    public Product removeProduct(int id) {
        Product productToRemove = getProduct(id);
        if (productToRemove != null) {
            products.remove(productToRemove);
        }
        return productToRemove;
    }

    public void updateProduct(int id, String field, String value) throws IllegalArgumentException {
        Product prod = getProduct(id);
        if (prod == null) {
            throw new IllegalArgumentException("Error: Product with ID " + id + " not found.");
        }

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

    public Product getProduct(int id) {
        for (Product product : products) {
            if (product.getId() == id) {
                return product;
            }
        }
        return null;
    }

    public List<Product> getProducts() {
        return products;
    }
}