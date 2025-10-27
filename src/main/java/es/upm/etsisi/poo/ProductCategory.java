package es.upm.etsisi.poo;

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
        return discount;
    }
}