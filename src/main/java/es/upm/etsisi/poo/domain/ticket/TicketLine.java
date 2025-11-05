package es.upm.etsisi.poo.domain.ticket;

import java.util.ArrayList;
import java.util.List;
import es.upm.etsisi.poo.domain.product.CustomizableProduct;
import es.upm.etsisi.poo.domain.product.Product;

public class TicketLine {
    private final Product product;
    private int quantity;
    private final List<String> customTexts;

    @SuppressWarnings("Convert2Diamond")
    public TicketLine(Product product, int quantity) {
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

    public Product getProduct() {
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
        if (product instanceof CustomizableProduct) {
            CustomizableProduct cp = (CustomizableProduct) product;
            if (customTexts.size() < cp.getMaxCustomizableTexts()) {
                customTexts.add(text);
            } else {
                throw new IllegalStateException("Error: Maximum number of custom texts reached.");
            }
        } else {
            throw new IllegalStateException("Error: This product is not customizable.");
        }
    }

    public double getLineTotal() {
        if (product instanceof CustomizableProduct) {
            CustomizableProduct cp = (CustomizableProduct) product;
            return cp.getPrice() * quantity;
        }
        return product.getPrice() * quantity;
    }
}
