package es.upm.etsisi.poo.domain.ticket;

import es.upm.etsisi.poo.domain.product.StandardProduct;

// Represents a common ticket that contains only standard products.
public class CommonTicket extends Ticket<StandardProduct> {
    public CommonTicket(String id) {
        super(id);
    }
}