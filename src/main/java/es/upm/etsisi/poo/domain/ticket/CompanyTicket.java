package es.upm.etsisi.poo.domain.ticket;

import java.time.LocalDateTime;

import es.upm.etsisi.poo.domain.exceptions.TicketRuleViolationException;
import es.upm.etsisi.poo.domain.product.Product;
import es.upm.etsisi.poo.domain.product.Service;
import es.upm.etsisi.poo.domain.product.StandardProduct;
import es.upm.etsisi.poo.infrastructure.printing.CompanyPrintStrategy;
import es.upm.etsisi.poo.infrastructure.printing.ServicePrintStrategy;
import es.upm.etsisi.poo.infrastructure.printing.StandardPrintStrategy;

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
        } else if (getPrintStrategy() instanceof ServicePrintStrategy) {
            // This is a Service-only strategy.
            if (productCount > 0) {
                throw new TicketRuleViolationException("Error: Service-only ticket cannot contain StandardProducts.");
            }
            if (serviceCount == 0) {
                throw new TicketRuleViolationException("Error: Service-only ticket must contain at least one Service.");
            }
        } else if (getPrintStrategy() instanceof StandardPrintStrategy) {
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
    public void validateProduct(Product p) throws TicketRuleViolationException {
        // E3: Service Usage Limit - Check expiration date.
        if (p instanceof Service) {
            Service s = (Service) p;
            if (s.getExpirationDate().isBefore(LocalDateTime.now())) {
                throw new TicketRuleViolationException("Error: Service " + s.getName() + " has expired.");
            }
        }
        if (getPrintStrategy() instanceof ServicePrintStrategy) {
            if (!(p instanceof Service)) {
                throw new TicketRuleViolationException("Error: Service-only tickets cannot contain StandardProducts.");
            }
        } else if (getPrintStrategy() instanceof StandardPrintStrategy) {
            if (p instanceof Service) {
                throw new TicketRuleViolationException("Error: Product-only tickets cannot contain Services.");
            }
        }
    }

    @Override
    public double getTotalPrice() {
        double productTotal = 0.0;
        double serviceTotal = 0.0;
        int currentServiceCount = 0;

        for (TicketLine<Product> line : getLines()) {
            Product p = line.getProduct();
            if (p.isService()) {
                serviceTotal += line.getLineTotal();
                currentServiceCount += line.getQuantity();
            } else {
                productTotal += line.getLineTotal();
            }
        }

        // E3 Rule: 15% discount on PRODUCTS for EACH service.
        double discountFactor = 0.15 * currentServiceCount;
        if (discountFactor > 1.0) discountFactor = 1.0; // Cap at 100%

        return serviceTotal + (productTotal * (1.0 - discountFactor));
    }
}