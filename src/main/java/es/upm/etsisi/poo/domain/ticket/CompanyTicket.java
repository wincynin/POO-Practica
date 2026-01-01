package es.upm.etsisi.poo.domain.ticket;

import es.upm.etsisi.poo.domain.exceptions.TicketRuleViolationException;
import es.upm.etsisi.poo.domain.product.Product;
import es.upm.etsisi.poo.domain.product.StandardProduct;
import es.upm.etsisi.poo.infrastructure.printing.CompanyPrintStrategy;

// [Class] Ticket type for Company Clients.
public class CompanyTicket extends Ticket<Product> {
    private int productCount;
    private int serviceCount;

    public CompanyTicket(String id) {
        super(id);
        setPrintStrategy(new CompanyPrintStrategy());
        this.productCount = 0;
        this.serviceCount = 0;
    }

    @Override
    public void addProduct(Product product, int quantity, java.util.List<String> customTexts) throws TicketRuleViolationException {
        int linesBefore = getLines().size();
        super.addProduct(product, quantity, customTexts);
        int linesAfter = getLines().size();

        if (linesAfter > linesBefore) { // A new line was added
            if (product instanceof StandardProduct) {
                productCount++;
            } else {
                serviceCount++;
            }
        }
    }

    @Override
    public boolean removeProduct(String productId) throws TicketRuleViolationException {
        Product productToRemove = null;
        for (TicketLine<Product> line : getLines()) {
            if (line.getProduct().getId().equals(productId)) {
                productToRemove = line.getProduct();
                break;
            }
        }

        boolean removed = super.removeProduct(productId);

        if (removed && productToRemove != null) {
            if (productToRemove instanceof StandardProduct) {
                productCount--;
            } else {
                serviceCount--;
            }
        }
        return removed;
    }

    @Override
    public String print() throws TicketRuleViolationException {
        // Check for rules before calling super.print(), which closes the ticket.
        if (getPrintStrategy() instanceof CompanyPrintStrategy) {
            // This is the "Mixed" strategy. It must have at least one Product and one Service.
            if (productCount == 0 || serviceCount == 0) {
                if (productCount == 0 && serviceCount == 0) {
                    throw new TicketRuleViolationException("Error: Cannot print an empty company ticket.");
                } else {
                    // It's not empty, but it's not mixed. It's either product-only or service-only with CompanyPrintStrategy.
                    // This is a violation of the "Mixed" rule for CompanyPrintStrategy.
                    throw new TicketRuleViolationException("Error: Mixed ticket must contain at least one StandardProduct and one Service.");
                }
            }
        } else if (getPrintStrategy() instanceof es.upm.etsisi.poo.infrastructure.printing.ServicePrintStrategy) {
            // This is a Service-only strategy.
            if (productCount > 0) {
                throw new TicketRuleViolationException("Error: Service-only ticket cannot contain StandardProducts.");
            }
            if (serviceCount == 0) {
                throw new TicketRuleViolationException("Error: Service-only ticket must contain at least one Service.");
            }
        } else if (getPrintStrategy() instanceof es.upm.etsisi.poo.infrastructure.printing.StandardPrintStrategy) {
            // This is a Product-only strategy.
            if (serviceCount > 0) {
                throw new TicketRuleViolationException("Error: Product-only ticket cannot contain Services.");
            }
            if (productCount == 0) {
                throw new TicketRuleViolationException("Error: Product-only ticket must contain at least one StandardProduct.");
            }
        }

        return super.print();
    }

    @Override
    public boolean accepts(es.upm.etsisi.poo.domain.product.Product p) {
        return true;
    }

    @Override
    public void validateProductAddition(Product p) throws TicketRuleViolationException {
        // Company tickets accept all products (Standard and Service).
        // No specific validation needed for now.
    }
}