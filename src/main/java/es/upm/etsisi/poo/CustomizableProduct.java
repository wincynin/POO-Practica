package es.upm.etsisi.poo;

import java.util.ArrayList;
import java.util.List;

public class CustomizableProduct extends Product {
    private final int maxCustomizableTexts;
    private final List<String> customTexts;

    public CustomizableProduct(int id, String name, ProductCategory category, double price, int maxCustomizableTexts) {
        super(id, name, category, price);
        this.maxCustomizableTexts = maxCustomizableTexts;
        this.customTexts = new ArrayList<>();
    }

    public CustomizableProduct(String name, ProductCategory category, double price, int maxCustomizableTexts) {
        super(name, category, price);
        this.maxCustomizableTexts = maxCustomizableTexts;
        this.customTexts = new ArrayList<>();
    }

    public int getMaxCustomizableTexts() {
        return maxCustomizableTexts;
    }

    public List<String> getCustomTexts() {
        return customTexts;
    }

    public void addCustomText(String text) {
        if (customTexts.size() < maxCustomizableTexts) {
            customTexts.add(text);
        } else {
            throw new IllegalStateException("Error: Maximum number of custom texts reached.");
        }
    }

    @Override
    public double getPrice() {
        double price = super.getPrice();
        for (String customText : customTexts) {
            price += super.getPrice() * 0.1;
        }
        return price;
    }

    @Override
    public String toString() {
        return String.format("{class: %s, id:%d, name:'%s', category:%s, price:%.1f, customTexts:%s}",
                this.getClass().getSimpleName(), getId(), getName(), getCategory(), getPrice(), customTexts);
    }
}