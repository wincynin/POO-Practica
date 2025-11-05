package es.upm.etsisi.poo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

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
            this.id = generateId();
        } else {
            this.id = id;
        }
    }

    private String generateId() {
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
        if (this.state == TicketState.CLOSED) {
            throw new IllegalStateException("Error: Cannot add products to a closed ticket.");
        }

        if (this.state == TicketState.EMPTY) {
            this.state = TicketState.ACTIVE;
        }

        if (product instanceof Food || product instanceof Meeting) {
            for (TicketLine line : lines) {
                if (line.getProduct().getId() == product.getId()) {
                    throw new IllegalStateException("Error: Food and Meeting products can only be added once.");
                }
            }
            if (product instanceof Food food) {
                if (food.getExpirationDate().isBefore(LocalDateTime.now().plusDays(3))) {
                    throw new IllegalStateException("Error: Food products must be planned at least 3 days in advance.");
                }
            }
            if (product instanceof Meeting meeting) {
                if (meeting.getExpirationDate().isBefore(LocalDateTime.now().plusHours(12))) {
                    throw new IllegalStateException("Error: Meeting products must be planned at least 12 hours in advance.");
                }
            }
        }
        
        // We now check for matching ID AND matching customizations
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

        if (lines.size() >= 100) {
            throw new IllegalStateException("Error: Ticket cannot have more than 100 product lines.");
        }

        TicketLine newLine = new TicketLine(product, quantity);
        if (customTexts != null && product instanceof CustomizableProduct) {
            for (String text : customTexts) {
                newLine.addCustomText(text);
            }
        }
        lines.add(newLine);
    }

    public boolean removeProduct(int productId) {
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
            total += line.getLineTotal();
        }
        return total;
    }

    public double getTotalDiscount() {
        double totalDiscount = 0.0;
        @SuppressWarnings("Convert2Diamond")
        List<ProductCategory> discountableCategories = new ArrayList<ProductCategory>();
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

        for (TicketLine line : lines) {
            if (discountableCategories.contains(line.getProduct().getCategory())) {
                totalDiscount += line.getLineTotal() * line.getProduct().getCategory().getDiscount();
            }
        }
        return totalDiscount;
    }

    @SuppressWarnings("Convert2Lambda")
    public void printAndClose() {
        if (this.state == TicketState.CLOSED) {
            System.out.println("Warning: Ticket is already closed. Reprinting.");
        }

        lines.sort(new Comparator<TicketLine>() {
            @Override
            public int compare(TicketLine l1, TicketLine l2) {
                return l1.getProduct().getName().compareToIgnoreCase(l2.getProduct().getName());
            }
        });

        System.out.println("Ticket ID: " + this.id);
        System.out.println("Cashier ID: " + this.cashierId);
        System.out.println("Client ID: " + this.userId);
        System.out.println("--------------------");

        @SuppressWarnings("Convert2Diamond")
        List<ProductCategory> discountableCategories = new ArrayList<ProductCategory>();
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

        for (TicketLine line : lines) {
            Product product = line.getProduct();
            String productString = String.format("{class: %s, id:%d, name:'%s', price:%.1f}",
                    product.getClass().getSimpleName(), product.getId(), product.getName(), product.getPrice());
            double discount = 0.0;
            if (discountableCategories.contains(product.getCategory())) {
                discount = line.getLineTotal() * product.getCategory().getDiscount();
            }

            System.out.printf("  %s, Quantity: %d", productString, line.getQuantity());
            if (discount > 0) {
                System.out.printf(" **discount-%.2f", discount);
            }
            if (product instanceof CustomizableProduct) {
                if (!line.getCustomTexts().isEmpty()) {
                    System.out.print(" --p");
                    for (String text : line.getCustomTexts()) {
                        System.out.print(" " + text);
                    }
                }
            }
            System.out.println();
        }

        System.out.println("--------------------");
        double totalPrice = getTotalPrice();
        double totalDiscount = getTotalDiscount();
        double finalPrice = totalPrice - totalDiscount;
        System.out.printf("Total price: %.2f%n", totalPrice);
        System.out.printf("Total discount: %.2f%n", totalDiscount);
        System.out.printf("Final Price: %.2f%n", finalPrice);


        if (this.state != TicketState.CLOSED) {
            this.state = TicketState.CLOSED;
            this.id += "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy-MM-dd-HH:mm"));
        }
        System.out.println("ticket print: ok");
    }

    public boolean isEmpty() {
        return lines.isEmpty();
    }
}