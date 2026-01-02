package es.upm.etsisi.poo.domain.ticket;

import java.util.List;
import java.io.Serializable;

import es.upm.etsisi.poo.domain.product.Product;

public class TicketLine<T extends Product> implements Serializable {
    private int quantity;
    private final T product;
    private final List<String> customTexts;

    public TicketLine(int quantity, T product, List<String> customTexts) {
        this.quantity = quantity;
        this.product = product;
        this.customTexts = customTexts;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public T getProduct() {
        return product;
    }

    public List<String> getCustomTexts() {
        return customTexts;
    }

    public double getLineTotal() {
        return product.getLineTotal(quantity, customTexts);
    }
}