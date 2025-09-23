package es.upm.etsisi.poo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a customer's ticket or shopping cart.
 * Manages a collection of products and calculates the total price with discounts.
 */
public class Ticket {
    private static final int MAX_ITEMS = 100;
    private final Map<Product, Integer> items;

    /**
     * Constructs an empty ticket.
     */
    public Ticket() {
        this.items = new HashMap<>();
    }

    /**
     * Adds a specified quantity of a product to the ticket.
     *
     * @param product  The product to add.
     * @param quantity The number of units to add.
     * @throws Exception if the ticket is full.
     */
    public void addProduct(Product product, int quantity) throws Exception {
        // Calculate current total number of items in the ticket
        int currentTotalItems = items.values().stream().mapToInt(Integer::intValue).sum();
        // Prevent exceeding the maximum number of items
        if (currentTotalItems + quantity > MAX_ITEMS) {
            throw new Exception("Error: Cannot add " + quantity + " items. Ticket would exceed " + MAX_ITEMS + " items.");
        }
        // Add the product and update its quantity
        items.put(product, items.getOrDefault(product, 0) + quantity);
    }

    /**
     * Removes all units of a specific product from the ticket.
     *
     * @param product The product to remove.
     */
    public void removeProduct(Product product) {
        items.remove(product);
    }

    /**
     * Clears all items from the ticket.
     */
    public void clear() {
        this.items.clear();
    }

    /**
     * Generates a string representation of the ticket details, including
     * individual items, total price, discounts, and the final price.
     * The items are sorted alphabetically by product name.
     *
     * @return A formatted string representing the ticket invoice.
     */
    public String getTicketDetails() {
        // If there are no items, return zeroed totals
        if (items.isEmpty()) {
            return "Total price: 0.0\nTotal discount: 0.0\nFinal Price: 0.0";
        }

        StringBuilder sb = new StringBuilder();
        double totalPrice = 0.0;
        double totalDiscount = 0.0;

        // Count how many items exist for each category
        Map<ProductCategory, Integer> categoryCounts = new HashMap<>();
        for (Map.Entry<Product, Integer> entry : items.entrySet()) {
            ProductCategory category = entry.getKey().getCategory();
            int quantity = entry.getValue();
            categoryCounts.put(category, categoryCounts.getOrDefault(category, 0) + quantity);
        }

        // Create a flat list of all products, repeating each according to its quantity
        List<Product> allProducts = new ArrayList<>();
        for (Map.Entry<Product, Integer> entry : items.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                allProducts.add(entry.getKey());
            }
        }

        // Sort products alphabetically by name for display
        allProducts.sort(Comparator.comparing(Product::getName));

        // Build the output string and calculate totals
        for (Product product : allProducts) {
            totalPrice += product.getPrice();
            double itemDiscount = 0.0;

            // Apply discount if there are 2 or more items in the same category
            if (categoryCounts.get(product.getCategory()) >= 2) {
                itemDiscount = product.getPrice() * product.getCategory().getDiscount();
                totalDiscount += itemDiscount;
            }

            sb.append(product.toString());
            if (itemDiscount > 0) {
                sb.append(String.format(" **discount-%.1f", itemDiscount));
            }
            sb.append("\n");
        }

        double finalPrice = totalPrice - totalDiscount;

        // Append summary totals
        sb.append(String.format("Total price: %.1f\n", totalPrice));
        sb.append(String.format("Total discount: %.1f\n", totalDiscount));
        sb.append(String.format("Final Price: %.1f", finalPrice));

        return sb.toString();
    }
}