package es.upm.etsisi.poo.domain.ticket;

import es.upm.etsisi.poo.domain.product.StandardProduct;

// [Class] Ticket type for Individual Clients.
public class CommonTicket extends Ticket<StandardProduct> {
    public CommonTicket(String id) {
        super(id);
    }
}