package es.upm.etsisi.poo.domain.ticket;

import java.util.HashMap;
import java.util.Map;

import es.upm.etsisi.poo.domain.exceptions.TicketRuleViolationException;
import es.upm.etsisi.poo.domain.product.Product;
import es.upm.etsisi.poo.domain.product.ProductCategory;

// [Class] Ticket type for Individual Clients.
public class CommonTicket extends Ticket<Product> {
    public CommonTicket(String id) {
        super(id);
    }
    @Override
    public boolean accepts(es.upm.etsisi.poo.domain.product.Product p) {
        return !p.isService();
    }

    @Override
    public void validateProduct(Product p) throws TicketRuleViolationException {
        if (p.isService()) {
            throw new TicketRuleViolationException("Common tickets cannot contain Services.");
        }
    }

    @Override
    public double getTotalPrice() {
        double total = 0.0;
        // Map to count items per category
        Map<ProductCategory, Integer> categoryCounts = new HashMap<>();

        // Pass 1: Count quantities per category
        for (TicketLine<Product> line : getLines()) {
            Product p = line.getProduct();
            if (p.getCategory() != null) {
                categoryCounts.put(p.getCategory(), categoryCounts.getOrDefault(p.getCategory(), 0) + line.getQuantity());
            }
        }

        // Pass 2: Calculate price with discounts
        for (TicketLine<Product> line : getLines()) {
            double lineTotal = line.getLineTotal();
            Product p = line.getProduct();
            // APPLY DISCOUNT ONLY IF COUNT >= 2
            if (p.getCategory() != null && categoryCounts.getOrDefault(p.getCategory(), 0) >= 2) {
                lineTotal *= (1.0 - p.getCategory().getDiscount());
            }
            total += lineTotal;
        }
        return total;
    }
}