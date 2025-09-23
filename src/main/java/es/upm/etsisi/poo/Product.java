package es.upm.etsisi.poo;

public class Product {
    private int id;
    private String name;
    private ProductCategory category;
    private double price;

    public Product(int id, String name, ProductCategory category, double price) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "{class:Product, id:" + id + ", name: '" + name + "', category:" + category + ", price:" + price + "}";
    }
}