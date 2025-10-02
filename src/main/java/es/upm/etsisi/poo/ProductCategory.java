package es.upm.etsisi.poo;

/**
 * Enum representing the different categories of products available.
 * Each category is associated with a specific discount rate.
 *
 * <ul>
 *   <li>{@link #MERCH} - Merchandise with no discount.</li>
 *   <li>{@link #PAPELERIA} - Stationery with a 5% discount.</li>
 *   <li>{@link #ROPA} - Clothing with a 7% discount.</li>
 *   <li>{@link #LIBRO} - Books with a 10% discount.</li>
 *   <li>{@link #ELECTRONICA} - Electronics with a 3% discount.</li>
 * </ul>
 */
public enum ProductCategory {
    MERCH(0.0),
    STATIONERY(0.05),
    CLOTHES(0.07),
    BOOK(0.10),
    ELECTRONICS(0.03);

    private final double discount;

    ProductCategory(double discount) {
        this.discount = discount;
    }

    public double getDiscount() {
        return this.discount;
    }
}