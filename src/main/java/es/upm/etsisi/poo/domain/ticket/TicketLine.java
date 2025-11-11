package es.upm.etsisi.poo.domain.ticket;

import java.util.List;
import java.util.ArrayList;
import es.upm.etsisi.poo.domain.product.*;

// Represents a line in a ticket, as defined in E2.
public class TicketLine {
    private int quantity;
    private final Product product;
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
        // Required logic, texts can only be added if the product is a CustomizableProduct.
        if (product instanceof CustomizableProduct) {
            CustomizableProduct cp = (CustomizableProduct) product;
            // Check if we can add more custom texts based on the product's limit.
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
        // We do not calculate the price here and call product.getPrice().

        // If the product is 'CustomizableProduct', the overriden getPrice()
        // will return the price with the 10% surcharge included.
        if (product instanceof CustomizableProduct) {
            CustomizableProduct cp = (CustomizableProduct) product;
            return cp.getPrice() * quantity;
        }
        // If it is a normal product, it will call the getPrice() from Product.
        return product.getPrice() * quantity;
    }
}