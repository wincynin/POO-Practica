package es.upm.etsisi.poo;

public enum ProductCategory {
    MERCH(0.0),
    PAPELERIA(0.05),
    ROPA(0.07),
    LIBRO(0.10),
    ELECTRONICA(0.03);

    private final double discount;

    ProductCategory(double discount) {
        this.discount = discount;
    }

    public double getDiscount() {
        return this.discount;
    }
}