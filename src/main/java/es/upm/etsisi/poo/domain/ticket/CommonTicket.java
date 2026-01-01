package es.upm.etsisi.poo.domain.ticket;

import es.upm.etsisi.poo.domain.exceptions.TicketRuleViolationException;
import es.upm.etsisi.poo.domain.product.Product;

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
}