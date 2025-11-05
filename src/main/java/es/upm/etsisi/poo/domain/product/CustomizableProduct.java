package es.upm.etsisi.poo.domain.product;

import java.util.ArrayList;
import java.util.List;

public class CustomizableProduct extends Product {
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
        return super.getPrice() * (1 + customTexts.size() * 0.1);
    }

    @Override
    public String toString() {
        return String.format("{class: %s, id:%d, name:'%s', category:%s, price:%.1f, customTexts:%s}",
                this.getClass().getSimpleName(), getId(), getName(), getCategory(), getPrice(), customTexts);
    }

    @Override
    public Product copyWithNewId(int newId) {
        return new CustomizableProduct(newId, this.getName(), this.getCategory(), this.getPrice(), this.getMaxCustomizableTexts());
    }
}
