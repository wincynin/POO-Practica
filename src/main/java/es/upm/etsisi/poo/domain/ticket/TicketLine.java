package es.upm.etsisi.poo.domain.ticket;

import java.util.List;
import java.util.ArrayList;
import es.upm.etsisi.poo.domain.product.*;

// Represents a line in a ticket, as defined in E2.
public class TicketLine<T extends Product> implements Comparable<TicketLine<T>> {
    private int quantity;
    private final T product;
    private final List<String> customTexts;

    @SuppressWarnings("Convert2Diamond")
    public TicketLine(T product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
        this.product = product;
        this.quantity = quantity;
        this.customTexts = new ArrayList<String>();
    }

    public T getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
        this.quantity = quantity;
    }

    public List<String> getCustomTexts() {
        return customTexts;
    }

    public void addCustomText(String text) {
        product.addCustomText(customTexts, text);
    }

    public double getLineTotal() {
        return product.getLineTotal(quantity, this.customTexts);
    }

    @Override
    public int compareTo(TicketLine<T> other) {
        return this.getProduct().getName().compareToIgnoreCase(other.getProduct().getName());
    }
}