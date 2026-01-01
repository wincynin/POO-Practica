package es.upm.etsisi.poo.domain.product;

import java.util.List;

// Represents a concept of product used for the system.
public abstract class Product implements java.io.Serializable {

    private int id;
    private String name;
    private double price;
    private static int nextId = 1;
    private ProductCategory category;
    private static final double MIN_PRICE = -0.001;
    private static final int MAX_NAME_LENGTH = 100;

    protected Product(String name, ProductCategory category, double price) {
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

    protected Product(int id, String name, ProductCategory category, double price) {
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

    public void setName(String name) {
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
        if (price <= MIN_PRICE) {
            throw new IllegalArgumentException("Error: Price must be greater than 0.");
        } else {
            this.price = price;
        }
    }

    public static void updateNextId(int id) {
        nextId = Math.max(nextId, id + 1);
    }

    public abstract List<String> getCustomTexts();
    public abstract void addCustomText(List<String> customTexts, String text);
    public abstract double getLineTotal(int quantity, List<String> customTexts);
    public abstract boolean isBookable();
    public abstract void validate();

    @Override
    public String toString() {
        return String.format("{class: %s, id:%d, name:'%s', category:%s, price:%.1f}",
                this.getClass().getSimpleName(), id, name, category, price);
    }
}