package es.upm.etsisi.poo.domain.ticket;

import es.upm.etsisi.poo.domain.product.Product;
import es.upm.etsisi.poo.domain.product.Service;
import es.upm.etsisi.poo.domain.product.StandardProduct;
import es.upm.etsisi.poo.domain.user.Cashier;
import es.upm.etsisi.poo.domain.user.Client;
import es.upm.etsisi.poo.infrastructure.printing.CompanyPrintStrategy;

public class CompanyTicket extends Ticket<Product> {
    public CompanyTicket(String id, Client client, Cashier cashier) {
        super(id, client, cashier);
        setPrintStrategy(new CompanyPrintStrategy());
    }

    @Override
    public String print() {
        if (getPrintStrategy() instanceof CompanyPrintStrategy) {
            boolean hasService = getLines().stream().anyMatch(l -> !(l.getProduct() instanceof StandardProduct));
            boolean hasProduct = getLines().stream().anyMatch(l -> l.getProduct() instanceof StandardProduct);
            if (!hasService || !hasProduct) {
                throw new IllegalStateException("Error: Mixed tickets must contain at least one Product and one Service.");
            }
        }
        return super.print();
    }
}