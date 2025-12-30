package es.upm.etsisi.poo.domain.ticket;

import es.upm.etsisi.poo.domain.product.Product;
import es.upm.etsisi.poo.domain.product.StandardProduct;
import es.upm.etsisi.poo.infrastructure.printing.CompanyPrintStrategy;

public class CompanyTicket extends Ticket<Product> {
    public CompanyTicket(String id) {
        super(id);
        setPrintStrategy(new CompanyPrintStrategy());
    }

    @Override
    public String print() {
        if (getPrintStrategy() instanceof CompanyPrintStrategy) {
            // "Classic" Java loop instead of Streams/Lambdas
            boolean hasService = false;
            boolean hasProduct = false;

            for (TicketLine<Product> line : getLines()) {
                if (line.getProduct() instanceof StandardProduct) {
                    hasProduct = true;
                } else {
                    // If it's not a StandardProduct, it must be a Service
                    hasService = true;
                }
            }

            if (!hasService || !hasProduct) {
                throw new IllegalStateException("Error: Mixed tickets must contain at least one Product and one Service.");
            }
        }
        return super.print();
    }
}
