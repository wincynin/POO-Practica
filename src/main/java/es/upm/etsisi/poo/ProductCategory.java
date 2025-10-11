package es.upm.etsisi.poo;

/**
 * Enum representing the different categories of products available.
 * Each category is associated with a specific discount rate.
 */
public enum ProductCategory {
    MERCH(0.0),
    STATIONERY(0.05),
    CLOTHES(0.07),
    BOOK(0.10),
    ELECTRONICS(0.03);

    private final double discount;

    /**
     * Private constructor to associate a discount with each category.
     * @param discount The discount rate for the category.
     */
    ProductCategory(double discount) {
        this.discount = discount;
    }

    /**
     * Returns the discount rate for the category.
     * @return The discount as a double.
     */
    public double getDiscount() {
        return this.discount;
    }
}