package es.upm.etsisi.poo;

import java.time.LocalDateTime;
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

    public Ticket(String id, String cashierId, String userId) {
        this.cashierId = cashierId;
        this.userId = userId;
        this.state = TicketState.ACTIVE;
        this.lines = new ArrayList<TicketLine>();
        if (id == null) {
            this.id = generateId();
        } else {
            this.id = id;
        }
    }

    private String generateId() {
        LocalDateTime now = LocalDateTime.now();
        String datePart = String.format("%02d-%02d-%02d-%02d:%02d-",
                now.getYear() % 100,
                now.getMonthValue(),
                now.getDayOfMonth(),
                now.getHour(),
                now.getMinute());
        Random random = new Random();
        int randomNumber = random.nextInt(90000) + 10000;
        return datePart + randomNumber;
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

    public List<TicketLine> getLines() {
        return new ArrayList<TicketLine>(lines);
    }

    public void addProduct(Product product, int quantity) {
        addProduct(product, quantity, null);
    }

    public void addProduct(Product product, int quantity, List<String> customTexts) {
        if (this.state == TicketState.CLOSED) {
            throw new IllegalStateException("Error: Cannot add products to a closed ticket.");
        }

        if (product instanceof Food || product instanceof Meeting) {
            for (TicketLine line : lines) {
                if (line.getProduct().getId() == product.getId()) {
                    throw new IllegalStateException("Error: Food and Meeting products can only be added once.");
                }
            }
            if (product instanceof Food) {
                Food food = (Food) product;
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

        for (TicketLine currentLine : lines) {
            if (currentLine.getProduct().getId() == product.getId()) {
                currentLine.setQuantity(currentLine.getQuantity() + quantity);
                return;
            }
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
        for (TicketLine line : lines) {
            if (line.getQuantity() >= 2 && line.getProduct().getCategory() != null) {
                totalDiscount += line.getLineTotal() * line.getProduct().getCategory().getDiscount();
            }
        }
        return totalDiscount;
    }

    public void printAndClose() {
        if (this.state == TicketState.CLOSED) {
            System.out.println("Warning: Ticket is already closed. Reprinting.");
        }

        lines.sort(Comparator.comparing(l -> l.getProduct().getName()));

        System.out.println("Ticket ID: " + this.id);
        System.out.println("Cashier ID: " + this.cashierId);
        System.out.println("Client ID: " + this.userId);
        System.out.println("--------------------");

        for (TicketLine line : lines) {
            Product product = line.getProduct();
            String productString = String.format("{class: %s, id:%d, name:'%s', price:%.1f}",
                    product.getClass().getSimpleName(), product.getId(), product.getName(), product.getPrice());
            double discount = 0.0;
            if (line.getQuantity() >= 2 && product.getCategory() != null) {
                discount = line.getLineTotal() * line.getProduct().getCategory().getDiscount();
            }

            System.out.printf("  %s, Quantity: %d", productString, line.getQuantity());
            if (discount > 0) {
                System.out.printf(" **discount-%.2f", discount);
            }
            if (product instanceof CustomizableProduct) {
                CustomizableProduct cp = (CustomizableProduct) product;
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
            LocalDateTime now = LocalDateTime.now();
            String closingDate = String.format("-%02d-%02d-%02d-%02d:%02d",
                    now.getYear() % 100,
                    now.getMonthValue(),
                    now.getDayOfMonth(),
                    now.getHour(),
                    now.getMinute());
            this.id += closingDate;
        }
        System.out.println("ticket print: ok");
    }

    public boolean isEmpty() {
        return lines.isEmpty();
    }
}