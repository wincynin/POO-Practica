package es.upm.etsisi.poo.domain.ticket;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.ArrayList;
import java.util.Comparator;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import es.upm.etsisi.poo.domain.product.*;


// Represents a ticket, as defined in E1 and E2.
public class Ticket {
    private String id;
    private TicketState state;
    private final List<TicketLine> lines;
    private static final int MAX_PRODUCT_LINES = 100;

    @SuppressWarnings("Convert2Diamond")
    public Ticket(String id) {
        this.state = TicketState.EMPTY;
        this.lines = new ArrayList<TicketLine>();

        if (id == null) {
            // If the ID is null, it is autogenerate, as requested.
            this.id = generateId();
        } else {
            this.id = id;
        }
    }

    private String generateId() {
        // The ID format must be "YY-MM-dd-HH:mm-" + 5 random digits as requested.
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy-MM-dd-HH:mm"));
        Random random = new Random();
        int randomNumber = random.nextInt(90000) + 10000;
        return datePart + "-" + randomNumber;
    }

    public String getId() {
        return id;
    }

    public TicketState getState() {
        return state;
    }

    @SuppressWarnings("Convert2Diamond")
    public List<TicketLine> getLines() {
        return new ArrayList<TicketLine>(lines);
    }

    public void addProduct(Product product, int quantity) {
        addProduct(product, quantity, null);
    }

    public void addProduct(Product product, int quantity, List<String> customTexts) {
        if (product == null) {
            throw new IllegalArgumentException("Error: Product cannot be null.");
        }
        // Check for closed tickets as they are required to be immutable after closing.
        if (this.state == TicketState.CLOSED) {
            throw new IllegalStateException("Error: Cannot add products to a closed ticket.");
        }
        // Check for empty tickets and set to active when adding the first product.
        if (this.state == TicketState.EMPTY) {
            this.state = TicketState.ACTIVE;
        }

        // Special rules for Bookable products.
        if (product.isBookable()) {
            // They can only be added once.
            for (TicketLine line : lines) {
                if (line.getProduct().getId() == product.getId()) {
                    throw new IllegalStateException("Error: Bookable products can only be added once.");
                }
            }
            // Check planning time constraints
            product.validate();
        }

        // We now check for matching ID AND matching customizations, to merge quantities them as requested.
        for (TicketLine currentLine : lines) {
            if (currentLine.getProduct().getId() == product.getId()) {
                // Check if customizations also match
                List<String> lineTexts = currentLine.getCustomTexts();      // This is never null, just empty
                boolean lineHasCustomTexts = !lineTexts.isEmpty();
                boolean newHasCustomTexts = (customTexts != null && !customTexts.isEmpty());

                if (!lineHasCustomTexts && !newHasCustomTexts) {
                    // CASE 1: Both are plain (no customs), so merge
                    currentLine.setQuantity(currentLine.getQuantity() + quantity);
                    return;
                }
                if (lineHasCustomTexts && newHasCustomTexts && lineTexts.equals(customTexts)) {
                    // CASE 2: Both have customs, and the lists are identical, so merge
                    currentLine.setQuantity(currentLine.getQuantity() + quantity);
                    return;
                }
            }
        }

        // We respect the E1 rule of max 100 items per ticket.
        if (lines.size() >= MAX_PRODUCT_LINES) {
            throw new IllegalStateException("Error: Ticket cannot have more than " + MAX_PRODUCT_LINES + " product lines.");
        }
        // After all checks, we can add the new line.
        TicketLine newLine = new TicketLine(product, quantity);
        if (customTexts != null) {
            for (String text : customTexts) {
                newLine.addCustomText(text);
            }
        }
        lines.add(newLine);
    }

    public boolean removeProduct(int productId) {
        // Check for closed tickets as they are required to be immutable after closing.
        if (this.state == TicketState.CLOSED) {
            throw new IllegalStateException("Error: Cannot remove products from a closed ticket.");
        }

        for (int i = lines.size() - 1; i >= 0; i--) {
            if (lines.get(i).getProduct().getId() == productId) {
                lines.remove(i);
                return true;
            }
        }
        return false;
    }

    public double getTotalPrice() {
        double total = 0.0;

        for (TicketLine line : lines) {
            // The total is based on getLineTotal(), which already includes the customization surcharges.
            total += line.getLineTotal();
        }
        return total;
    }

    public double getTotalDiscount() {
        double totalDiscount = 0.0;
        @SuppressWarnings("Convert2Diamond")
        List<ProductCategory> discountableCategories = getDiscountableCategories();

        // Apply discount based on category's discount rate, if eligible, while iterating through all lines.
        for (TicketLine line : lines) {
            if (discountableCategories.contains(line.getProduct().getCategory())) {
                totalDiscount += line.getLineTotal() * line.getProduct().getCategory().getDiscount();
            }
        }
        return totalDiscount;
    }

    @SuppressWarnings("Convert2Lambda")
    public void printAndClose() {
        // Check for closed tickets as they are required to be immutable after closing.
        if (this.state == TicketState.CLOSED) {
            System.out.println("Warning: Ticket is already closed. Reprinting.");
        }

        // Mantaining E1 requirement of sorting by product name alphabetically when printing.
        lines.sort(new Comparator<TicketLine>() {
            // This sorts the 'lines' list alphabetically by product name, ignoring case.
            // It uses compareToIgnoreCase(), which returns:
            // - NEGATIVE: if l1's name < l2's name
            // - ZERO: if l1's name == l2's name
            // - POSITIVE: if l1's name > l2's name
            // The sort() method then uses these return values to reorder the list.
            @Override
            public int compare(TicketLine l1, TicketLine l2) {
                return l1.getProduct().getName().compareToIgnoreCase(l2.getProduct().getName());
            }
        });

        // Printing the ticket details in the order they were requested.
        System.out.println("Ticket ID: " + this.id);
        System.out.println("--------------------");

        // We recalculate the discountable categories to know where to print the text "**discount".
        List<ProductCategory> discountableCategories = getDiscountableCategories();

        for (TicketLine line : lines) {
            Product product = line.getProduct();
            // Formatted string for the product details as requested, giving class name, id, name and price.
            String productString = String.format("{class: %s, id:%d, name:'%s', price:%.1f}",
                    product.getClass().getSimpleName(), product.getId(), product.getName(), product.getPrice());

            // Calculate discount for this line if applicable while iterating.
            double discount = 0.0;
            if (discountableCategories.contains(product.getCategory())) {
                discount = line.getLineTotal() * product.getCategory().getDiscount();
            }

            // Formatting the line output as requested, including discount and custom texts if any.
            System.out.printf("  %s, Quantity: %d", productString, line.getQuantity());
            if (discount > 0) {
                // Discount presented with two decimal places as requested.
                System.out.printf(" **discount-%.2f", discount);
            }
            // If the line has custom texts, we print the prefix " --p" to match the input
            // command format.
            if (!line.getCustomTexts().isEmpty()) {
                System.out.print(" --p");
                for (String text : line.getCustomTexts()) {
                    System.out.print(" " + text);
                }
            }
            // Nueva línea para el siguiente item.
            System.out.println();
        }

        System.out.println("--------------------");
        double totalPrice = getTotalPrice();
        double totalDiscount = getTotalDiscount();
        double finalPrice = totalPrice - totalDiscount;

        // Printing total price, total discount and final price, each value with two decimal places.
        System.out.printf("Total price: %.2f%n", totalPrice);
        System.out.printf("Total discount: %.2f%n", totalDiscount);
        System.out.printf("Final Price: %.2f%n", finalPrice);

        // Closing the ticket and updating the ID after the printing as requested.
        if (this.state != TicketState.CLOSED) {
            this.state = TicketState.CLOSED;
            this.id += "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy-MM-dd-HH:mm"));
        }

        System.out.println("ticket print: ok");
    }

    private List<ProductCategory> getDiscountableCategories() {
        Map<ProductCategory, Integer> categoryCounts = new HashMap<>();
        for (TicketLine line : lines) {
            ProductCategory category = line.getProduct().getCategory();
            if (category != null) {
                categoryCounts.put(category, categoryCounts.getOrDefault(category, 0) + line.getQuantity());
            }
        }

        List<ProductCategory> discountableCategories = new ArrayList<>();
        for (Map.Entry<ProductCategory, Integer> entry : categoryCounts.entrySet()) {
            if (entry.getValue() > 1) {
                discountableCategories.add(entry.getKey());
            }
        }
        return discountableCategories;
    }

    public boolean isEmpty() {
        return lines.isEmpty();
    }
}