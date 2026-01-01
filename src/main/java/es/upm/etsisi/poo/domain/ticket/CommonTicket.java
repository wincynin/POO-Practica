package es.upm.etsisi.poo.domain.ticket;

import java.util.HashMap;
import java.util.Map;

import es.upm.etsisi.poo.domain.exceptions.TicketRuleViolationException;
import es.upm.etsisi.poo.domain.product.Product;
import es.upm.etsisi.poo.domain.product.ProductCategory;
import es.upm.etsisi.poo.domain.product.StandardProduct;

// [Class] Ticket type for Individual Clients.
public class CommonTicket extends Ticket<StandardProduct> {
    public CommonTicket(String id) {
        super(id);
    }
    @Override
    public boolean accepts(es.upm.etsisi.poo.domain.product.Product p) {
        return p instanceof StandardProduct;
    }

    @Override
    public void validateProduct(Product p) throws TicketRuleViolationException {
        if (!(p instanceof StandardProduct)) {
            throw new TicketRuleViolationException("Common tickets only accept Standard Products.");
        }
    }

    @Override
    public double getTotalPrice() {
        double total = 0.0;
        Map<ProductCategory, Integer> categoryCounts = new HashMap<>();

        // E1: Count items per category to determine eligibility for discount.
        for (TicketLine<StandardProduct> line : getLines()) {
            StandardProduct p = line.getProduct();
            if (p.getCategory() != null) {
                categoryCounts.put(p.getCategory(), categoryCounts.getOrDefault(p.getCategory(), 0) + line.getQuantity());
            }
        }

        for (TicketLine<StandardProduct> line : getLines()) {
            double lineTotal = line.getLineTotal();
            StandardProduct p = line.getProduct();
            if (p.getCategory() != null && categoryCounts.getOrDefault(p.getCategory(), 0) >= 2) {
                lineTotal *= (1.0 - p.getCategory().getDiscount());
            }
            total += lineTotal;
        }
        return total;
    }
}