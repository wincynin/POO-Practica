package es.upm.etsisi.poo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Catalog {
    private static final int MAX_PRODUCTS = 200;
    private final List<Product> products;

    public Catalog() {
        this.products = new ArrayList<>();
    }

    public boolean addProduct(Product prod) {
        if (products.size() >= MAX_PRODUCTS) {
            System.out.println("Error: Maximum number of products reached.");
            return false;
        } else if (getProduct(prod.getId()) != null) {
            System.out.println("Error: A product with that ID already exists.");
            return false;
        }
        products.add(prod);
        return true;
    }

    public Product removeProduct(String id) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId().equals(id)) {
                return products.remove(i);
            }
        }
        return null;
    }

    public Product getProduct(String id) {
        for (Product product : products) {
            if (product.getId().equals(id)) {
                return product;
            }
        }
        return null;
    }

    public List<Product> listProducts() {
        products.sort(Comparator.comparing(Product::getId));
        return products;
    }

    public boolean updateProduct(String id, String field, String value) {
        Product prod = getProduct(id);
        if (prod == null) {
            return false;
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
                    System.out.println("Error: Invalid category.");
                    return false;
                }
                break;
            case "PRICE":
                try {
                    double newPrice = Double.parseDouble(value);
                    if (newPrice <= 0) {
                        System.out.println("Error: Price must be greater than 0.");
                        return false;
                    }
                    prod.setPrice(newPrice);
                } catch (NumberFormatException e) {
                    System.out.println("Error: Invalid price.");
                    return false;
                }
                break;
            default:
                System.out.println("Error: Invalid field.");
                return false;
        }
        return true;
    }

    public boolean isEmpty() {
        return products.isEmpty();
    }

    public int getSize() {
        return products.size();
    }
}