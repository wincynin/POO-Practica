
/**
 * Represents a product in the catalog.
 * Each product has an immutable ID, a name, category, and price.
 */
package es.upm.etsisi.poo;

/**
 * Product class models a product with id, name, category, and price.
 * The id is immutable once set.
 */
public final class Product {
    /** Unique identifier for the product (immutable). */
    private final int id;
    /** Name of the product. */
    private String name;
    /** Category of the product. */
    private ProductCategory category;
    /** Price of the product. */
    private double price;

    /**
     * Constructs a new Product with the specified id, name, category, and price.
     *
     * @param id Unique identifier (must be positive)
     * @param name Name of the product (non-empty, max 100 chars)
     * @param category Category of the product (non-null)
     * @param price Price of the product (must be positive)
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Product(int id, String name, ProductCategory category, double price) {
        // Validate id
        if (id <= 0) {
            throw new IllegalArgumentException("Error: Product ID must be positive.");
        }
        this.id = id;
        setName(name);
        setCategory(category);
        setPrice(price);
    }

    /**
     * Returns the unique identifier of the product.
     * @return product id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the name of the product.
     * @return product name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the product.
     * @param name product name (non-empty, max 100 chars)
     * @throws IllegalArgumentException if name is invalid
     */
    public void setName(String name) {
        // Validate name
        if (name == null || name.trim().isEmpty() || name.length() > 100) {
            throw new IllegalArgumentException("Error: Product name cannot be empty and must be at most 100 characters.");
        }
        this.name = name;
    }

    /**
     * Returns the category of the product.
     * @return product category
     */
    public ProductCategory getCategory() {
        return category;
    }

    /**
     * Sets the category of the product.
     * @param category product category (non-null)
     * @throws IllegalArgumentException if category is null
     */
    public void setCategory(ProductCategory category) {
        // Validate category
        if (category == null) {
            throw new IllegalArgumentException("Error: Product category cannot be null.");
        }
        this.category = category;
    }

    /**
     * Returns the price of the product.
     * @return product price
     */
    public double getPrice() {
        return price;
    }

    /**
     * Sets the price of the product.
     * @param price product price (must be positive)
     * @throws IllegalArgumentException if price is not positive
     */
    public void setPrice(double price) {
        // Validate price
        if (price <= 0) {
            throw new IllegalArgumentException("Error: Product price must be positive.");
        }
        this.price = price;
    }

    /**
     * Returns a string representation of the product.
     * @return formatted string with product details
     */
    @Override
    public String toString() {
        return String.format("{class:Product, id:%d, name:'%s', category:'%s', price:%.1f}",
                id, name, category, price);
    }
}