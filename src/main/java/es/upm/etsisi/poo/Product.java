package es.upm.etsisi.poo;

public class Product {
    private int id;
    private String name;
    private ProductCategory category;
    private double price;

    public Product(int id, String name, ProductCategory category, double price) {
        if (id <= 0) {
            throw new IllegalArgumentException("Error: ID debe ser positivo.");
        }
        if (name == null || name.length() > 100) {
            throw new IllegalArgumentException("Error: El nombre no puede estar vacío ni superar los 100 caracteres.");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Error: el precio debe ser > 0 euros.");
        }
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
    }

    public int getId() { return id;}
    public  String getName() { return name; }
    public ProductCategory getCategory() { return category; }
    public double getPrice() { return price; }

    public void setName(String name) {
        if (name == null || name.length() > 100) {
            throw new IllegalArgumentException("Error: El nombre no puede estar vacío ni superar los 100 caracteres.");
        } else {
            this.name = name;
        }
    }
    public void setCategory(ProductCategory category) { this.category = category; }
    public void setPrice(double price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Error: el precio debe ser > 0 euros.");
        } else {
            this.price = price;
        }
    }

    @Override
    public String toString() {
        return String.format("{class: Product , id:%d, name:'%s', category:%s, price:%.1f}", id, name, category, price);
    }
}