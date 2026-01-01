package es.upm.etsisi.poo.domain.ticket;

import es.upm.etsisi.poo.domain.exceptions.TicketRuleViolationException;
import es.upm.etsisi.poo.domain.product.Product;
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
}