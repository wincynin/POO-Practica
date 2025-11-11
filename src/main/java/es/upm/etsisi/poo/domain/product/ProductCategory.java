package es.upm.etsisi.poo.domain.product;

// Represents product categories and their discount rates as required by E1.
public enum ProductCategory {
    MERCH(0.0),             // E1 Specified Discount: 0%
    BOOK(0.10),             // E1 Specified Discount: 10%
    CLOTHES(0.07),          // E1 Specified Discount: 7%
    STATIONERY(0.05),       // E1 Specified Discount: 5%
    ELECTRONICS(0.03);      // E1 Specified Discount: 3%

    private final double discount;

    ProductCategory(double discount) {
        this.discount = discount;
    }

    public double getDiscount() {
        return discount;
    }
}