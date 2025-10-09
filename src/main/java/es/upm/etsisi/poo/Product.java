package es.upm.etsisi.poo;

/**
 * Represents a product with a unique identifier, name, category, and price.
 * <p>
 * Instances of {@code Product} are immutable with respect to their ID, but allow
 * modification of name, category, and price with validation.
 * </p>
 * <p>
 * Use {@link #createInstance(int, String, ProductCategory, double)} to create new products.
 * </p>
 */
public final class Product {

    private final int id;
    private String name;
    private ProductCategory category;
    private double price;

    /**
     * Private constructor for Product.
     * Use {@link #createInstance(int, String, ProductCategory, double)} for object creation.
     *
     * @param id       Unique identifier for the product
     * @param name     Name of the product
     * @param category Category of the product
     * @param price    Price of the product
     */
    private Product(int id, String name, ProductCategory category, double price) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
    }

    /**
     * Creates a new {@code Product} instance with validation.
     * <p>
     * This method checks that all parameters are valid before creating a Product.
     * If any parameter is invalid, it returns null instead of a Product object.
     * </p>
     *
     * @param id       Unique identifier (must be positive)
     * @param name     Product name (not null, not empty, max 100 characters)
     * @param category Product category (not null)
     * @param price    Product price (must be positive)
     * @return A new {@code Product} if all validations pass, otherwise {@code null}
     */
    public static Product createInstance(int id, String name, ProductCategory category, double price) {
        // Validate id: must be positive
        if (id <= 0) return null;
        // Validate name: must not be null, empty, or too long
        if (name == null || name.trim().isEmpty() || name.length() > 100) return null;
        // Validate category: must not be null
        if (category == null) return null;
        // Validate price: must be positive
        if (price <= 0) return null;
        // All validations passed, create and return new Product
        return new Product(id, name, category, price);
    }

    /**
     * Returns the unique identifier of the product.
     * <p>
     * This method simply returns the product's id.
     * </p>
     *
     * @return Product ID
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the name of the product.
     * <p>
     * Use this method to get the current name of the product.
     * </p>
     *
     * @return Product name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the product name with validation.
     * <p>
     * This method updates the product's name only if the new name is valid.
     * It checks that the name is not null, not empty, and not longer than 100 characters.
     * </p>
     *
     * @param name New product name (not null, not empty, max 100 characters)
     * @return {@code true} if the name was set successfully, {@code false} otherwise
     */
    public boolean setName(String name) {
        // Validate name before setting
        if (name == null || name.trim().isEmpty() || name.length() > 100) return false;
        this.name = name;
        return true;
    }

    /**
     * Returns the category of the product.
     * <p>
     * Use this method to get the current category of the product.
     * </p>
     *
     * @return Product category
     */
    public ProductCategory getCategory() {
        return category;
    }

    /**
     * Sets the product category with validation.
     * <p>
     * This method updates the product's category only if the new category is not null.
     * </p>
     *
     * @param category New product category (not null)
     * @return {@code true} if the category was set successfully, {@code false} otherwise
     */
    public boolean setCategory(ProductCategory category) {
        // Validate category before setting
        if (category == null) return false;
        this.category = category;
        return true;
    }

    /**
     * Returns the price of the product.
     * <p>
     * Use this method to get the current price of the product.
     * </p>
     *
     * @return Product price
     */
    public double getPrice() {
        return price;
    }

    /**
     * Sets the product price with validation.
     * <p>
     * This method updates the product's price only if the new price is positive.
     * </p>
     *
     * @param price New product price (must be positive)
     * @return {@code true} if the price was set successfully, {@code false} otherwise
     */
    public boolean setPrice(double price) {
        // Validate price before setting
        if (price <= 0) return false;
        this.price = price;
        return true;
    }

    /**
     * Returns a string representation of the product.
     * <p>
     * This method is useful for debugging and logging.
     * It shows all the product's properties in a readable format.
     * </p>
     *
     * @return String representation in the format:
     *         {class:Product, id:..., name:'...', category:..., price:...}
     */
    @Override
    public String toString() {
        // Return formatted string with product details as per specification
        return String.format("{class:Product, id:%d, name:'%s', category:%s, price:%.2f}", id, name, category, price);
    }
}