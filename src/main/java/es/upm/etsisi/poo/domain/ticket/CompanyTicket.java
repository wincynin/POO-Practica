package es.upm.etsisi.poo.domain.ticket;

import es.upm.etsisi.poo.domain.product.Product;
import es.upm.etsisi.poo.domain.user.Cashier;
import es.upm.etsisi.poo.domain.user.Client;
import es.upm.etsisi.poo.infrastructure.printing.CompanyPrintStrategy;

public class CompanyTicket extends Ticket<Product> {
    public CompanyTicket(String id, Client client, Cashier cashier) {
        super(id, client, cashier);
        setPrintStrategy(new CompanyPrintStrategy());
    }
}
