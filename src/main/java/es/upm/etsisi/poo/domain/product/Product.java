package es.upm.etsisi.poo.domain.product;

import java.util.Collections;
import java.util.List;

// Represents a standard product, as defined in E1 and E2.
public class Product {

    private String name;
    private double price;
    private int id;
    private ProductCategory category;
    private static final double MIN_PRICE = 0.0;        // E1 requirement: price must be > 0
    private static int nextId = 1;
    private static final int MAX_NAME_LENGTH = 100;     // E1 reqeuirement: name max length to be under 100 char

    public Product(String name, ProductCategory category, double price) {
        if (name == null || name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("Error: Name cannot be empty or exceed 100 characters.");
        }
        if (price <= MIN_PRICE) {
            throw new IllegalArgumentException("Error: Price must be greater than 0.");
        }
        this.id = nextId++;
        this.name = name;
        this.category = category;
        this.price = price;
    }

    public Product(int id, String name, ProductCategory category, double price) {
        if (name == null || name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("Error: Name cannot be empty or exceed 100 characters.");
        }
        if (price <= MIN_PRICE) {
            throw new IllegalArgumentException("Error: Price must be greater than 0.");
        }
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        nextId = Math.max(nextId, id + 1);
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public List<String> getCustomTexts() {
        return Collections.emptyList();
    }

    public void setName(String name) {
        // Check new name cannot be empty or > 100 chars
        if (name == null || name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("Error: Name cannot be empty or exceed 100 characters.");
        } else {
            this.name = name;
        }
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public void setPrice(double price) {
        // Check new price > 0
        if (price <= MIN_PRICE) {
            throw new IllegalArgumentException("Error: Price must be greater than 0.");
        } else {
            this.price = price;
        }
    }



    public void addCustomText(List<String> customTexts, String text) {
        throw new IllegalStateException("Error: This product is not customizable.");
    }

    public double getLineTotal(int quantity, List<String> customTexts) {
        return getPrice() * quantity;
    }

    public boolean isBookable() {
        return false;
    }


    public void validate() {
        // Does nothing. Subclasses can override this.
    }

    @Override
    public String toString() {
        // Returns a string representation of the Product, including id, name, category and price, in that order.
        return String.format("{class: %s, id:%d, name:'%s', category:%s, price:%.1f}",
                this.getClass().getSimpleName(), id, name, category, price);
    }
}