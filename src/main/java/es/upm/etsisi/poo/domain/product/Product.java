package es.upm.etsisi.poo.domain.product;

import java.util.List;

import es.upm.etsisi.poo.domain.exceptions.InvalidProductDataException;

// [Abstract Class] Base class for all products.
public abstract class Product implements java.io.Serializable {

    private String name;
    private double price;
    private final String id;
    private static int nextId = 1;              // Static counter for IDs.
    private ProductCategory category;
    protected static int nextServiceId = 1;
    protected static final double MIN_PRICE = -0.001;
    protected static final int MAX_NAME_LENGTH = 100;

    protected Product(String name, ProductCategory category, double price) throws InvalidProductDataException {
        // Validation removed to allow Services (which may have no price/name initially).
        // Subclasses (like StandardProduct) must enforce their own rules.
        this.id = String.valueOf(nextId++);
        this.name = name;
        this.category = category;
        this.price = price;
    }

    protected Product(String id, String name, ProductCategory category, double price) throws InvalidProductDataException {
        // Validation removed to allow Services.
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        updateCounters(id);
    }

    private void updateCounters(String id) {
        if (isService()) {
            try {
                int val = Integer.parseInt(id.substring(0, id.length() - 1));
                nextServiceId = Math.max(nextServiceId, val + 1);
            } catch (NumberFormatException ignored) {}
        } else {
            try {
                int val = Integer.parseInt(id);
                nextId = Math.max(nextId, val + 1);
            } catch (NumberFormatException ignored) {}
        }
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

    public void setName(String name) throws InvalidProductDataException {
        if (name == null || name.length() > MAX_NAME_LENGTH) {
            throw new InvalidProductDataException("Error: Name cannot be empty or exceed 100 characters.");
        } else {
            this.name = name;
        }
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public void setPrice(double price) throws InvalidProductDataException {
        if (price <= MIN_PRICE) {
            throw new InvalidProductDataException("Error: Price must be greater than 0.");
        } else {
            this.price = price;
        }
    }

    public static void updateNextId(int id) {
        nextId = Math.max(nextId, id + 1);
    }

    public static void updateNextServiceId(int id) {
        nextServiceId = Math.max(nextServiceId, id + 1);
    }

    public abstract List<String> getCustomTexts();
    public abstract void addCustomText(List<String> customTexts, String text);
    public abstract double getLineTotal(int quantity, List<String> customTexts);
    public abstract boolean isBookable();
    public abstract void validate();
    
    public boolean isService() {
        return false;
    }

    @Override
    public String toString() {
        return String.format("{class: %s, id:%s, name:'%s', category:%s, price:%.1f}",
                this.getClass().getSimpleName(), id, name, category, price);
    }
}