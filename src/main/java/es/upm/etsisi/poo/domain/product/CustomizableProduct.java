package es.upm.etsisi.poo.domain.product;

import java.util.List;
import java.util.ArrayList;

// Represents a product that can be customized with texts, as defined in E2
public class CustomizableProduct extends Product {
    private static final double CUSTOM_SURCHARGE = 0.1;     //E2 Requirement: 10% surcharge per custom text
    private final int maxCustomizableTexts;
    private final List<String> customTexts;

    @SuppressWarnings("Convert2Diamond")
    public CustomizableProduct(String name, ProductCategory category, double price, int maxCustomizableTexts) {
        super(name, category, price);
        this.maxCustomizableTexts = maxCustomizableTexts;
        this.customTexts = new ArrayList<String>();
    }

    @SuppressWarnings("Convert2Diamond")
    public CustomizableProduct(int id, String name, ProductCategory category, double price, int maxCustomizableTexts) {
        super(id, name, category, price);
        this.maxCustomizableTexts = maxCustomizableTexts;
        this.customTexts = new ArrayList<String>();
    }

    public int getMaxCustomizableTexts() {
        return maxCustomizableTexts;
    }

    @Override
    public List<String> getCustomTexts() {
        return customTexts;
    }

    @Override
    public void addCustomText(List<String> customTexts, String text) {
        if (customTexts.size() < maxCustomizableTexts) {
            customTexts.add(text);
        } else {
            throw new IllegalStateException("Error: Maximum number of custom texts reached.");
        }
    }

    @Override
    public double getLineTotal(int quantity, List<String> customTexts) {
        return super.getPrice() * (1 + customTexts.size() * CUSTOM_SURCHARGE) * quantity;
    }

    @Override
    public String toString() {
        // Returns a string representation of the CustomizableProduct, including id, name, category, price and custom texts, in that order.
        return String.format("{class: %s, id:%d, name:'%s', category:%s, price:%.1f, customTexts:%s}",
                this.getClass().getSimpleName(), getId(), getName(), getCategory(), getPrice(), customTexts);
    }


}