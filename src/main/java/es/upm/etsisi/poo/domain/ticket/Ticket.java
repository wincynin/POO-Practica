package es.upm.etsisi.poo.domain.ticket;

import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Comparator;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import es.upm.etsisi.poo.domain.product.*;

public class Ticket {
    private String id;
    private final String cashierId;
    private final String userId;
    private TicketState state;
    private final List<TicketLine> lines;

    @SuppressWarnings("Convert2Diamond")
    public Ticket(String id, String cashierId, String userId) {
        this.cashierId = cashierId;
        this.userId = userId;
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

    public String getCashierId() {
        return cashierId;
    }

    public String getUserId() {
        return userId;
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

        // Special rules for Food and Meeting products.
        if (product instanceof Food || product instanceof Meeting) {
            // They can only be added once.
            for (TicketLine line : lines) {
                if (line.getProduct().getId() == product.getId()) {
                    throw new IllegalStateException("Error: Food and Meeting products can only be added once.");
                }
            }
            // Check planning time constraints, specified to be at least 3 days for Food
            if (product instanceof Food) {
                Food food = (Food) product;
                if (food.getExpirationDate().isBefore(LocalDateTime.now().plusDays(3))) {
                    throw new IllegalStateException("Error: Food products must be planned at least 3 days in advance.");
                }
            }
            // Check planning time constraints, specified 12 hours for Meeting.
            if (product instanceof Meeting) {
                Meeting meeting = (Meeting) product;
                if (meeting.getExpirationDate().isBefore(LocalDateTime.now().plusHours(12))) {
                    throw new IllegalStateException(
                            "Error: Meeting products must be planned at least 12 hours in advance.");
                }
            }
        }

        // We now check for matching ID AND matching customizations, to merge quantities them as requested.
        for (TicketLine currentLine : lines) {
            if (currentLine.getProduct().getId() == product.getId()) {
                // Check if customizations also match
                List<String> lineTexts = currentLine.getCustomTexts(); // This is never null, just empty
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
        if (lines.size() >= 100) {
            throw new IllegalStateException("Error: Ticket cannot have more than 100 product lines.");
        }
        // After all checks, we can add the new line.
        TicketLine newLine = new TicketLine(product, quantity);
        if (customTexts != null && product instanceof CustomizableProduct) {
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
        System.out.println("Cashier ID: " + this.cashierId);
        System.out.println("Client ID: " + this.userId);
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
            if (product instanceof CustomizableProduct) {
                // If the line has custom texts, we print the prefix " --p" to match the input
                // command format.
                if (!line.getCustomTexts().isEmpty()) {
                    System.out.print(" --p");
                    for (String text : line.getCustomTexts()) {
                        System.out.print(" " + text);
                    }
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

    @SuppressWarnings("Convert2Diamond")
    private List<ProductCategory> getDiscountableCategories() {
        List<ProductCategory> discountableCategories = new ArrayList<ProductCategory>();

        // Identify categories eligible for discount, criteria being more than 1 item in the same category.
        for (ProductCategory category : ProductCategory.values()) {
            int categoryCount = 0;
            for (TicketLine line : lines) {
                if (line.getProduct().getCategory() == category) {
                    categoryCount += line.getQuantity();
                }
            }
            if (categoryCount > 1) {
                discountableCategories.add(category);
            }
        }
        return discountableCategories;
    }

    public boolean isEmpty() {
        return lines.isEmpty();
    }
}