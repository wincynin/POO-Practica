package es.upm.etsisi.poo.domain.ticket;



import es.upm.etsisi.poo.domain.product.StandardProduct;

public class CommonTicket extends Ticket<StandardProduct> {
    public CommonTicket(String id) {
        super(id);
    }
}
