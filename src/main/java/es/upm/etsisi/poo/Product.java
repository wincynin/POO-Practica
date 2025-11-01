package es.upm.etsisi.poo;

public class Product {
    private final String id;
    private String name;
    private ProductCategory category;
    private double price;

    public Product(String id, String name, ProductCategory category, double price) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Error: ID cannot be null or empty.");
        }
        if (name == null || name.length() > 100) {
            throw new IllegalArgumentException("Error: Name cannot be empty or exceed 100 characters.");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Error: Price must be greater than 0.");
        }
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
    }

    public String getId() {
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
        if (name == null || name.length() > 100) {
            throw new IllegalArgumentException("Error: Name cannot be empty or exceed 100 characters.");
        } else {
            this.name = name;
        }
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public void setPrice(double price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Error: Price must be greater than 0.");
        } else {
            this.price = price;
        }
    }

    @Override
    public String toString() {
        return String.format("{class: %s, id:%s, name:'%s', category:%s, price:%.1f}",
                this.getClass().getSimpleName(), id, name, category, price);
    }
}
