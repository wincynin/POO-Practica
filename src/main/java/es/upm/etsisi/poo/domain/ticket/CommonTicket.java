package es.upm.etsisi.poo.domain.ticket;

import es.upm.etsisi.poo.domain.product.StandardProduct;
import es.upm.etsisi.poo.domain.user.Cashier;
import es.upm.etsisi.poo.domain.user.Client;

public class CommonTicket extends Ticket<StandardProduct> {
    public CommonTicket(String id, Client client, Cashier cashier) {
        super(id, client, cashier);
    }
}
