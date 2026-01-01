package es.upm.etsisi.poo.domain.ticket;

import java.io.Serializable;
import java.time.LocalDateTime;

import es.upm.etsisi.poo.domain.exceptions.TicketRuleViolationException;
import es.upm.etsisi.poo.domain.product.Product;
import es.upm.etsisi.poo.domain.product.Service;
import es.upm.etsisi.poo.infrastructure.printing.CompanyPrintStrategy;
import es.upm.etsisi.poo.infrastructure.printing.PrintStrategy;
import es.upm.etsisi.poo.infrastructure.printing.ServicePrintStrategy;
import es.upm.etsisi.poo.infrastructure.printing.StandardPrintStrategy;

// [Class] Ticket implementation for specific Client types.
public class CompanyTicket extends Ticket<Product> {
    private int productCount;
    private int serviceCount;
    private ValidationPolicy validationPolicy;

    public CompanyTicket(String id) {
        super(id);
        // This will trigger setPrintStrategy and initialize the validationPolicy
        setPrintStrategy(new CompanyPrintStrategy());
        this.productCount = 0;
        this.serviceCount = 0;
    }

    @Override
    public void setPrintStrategy(PrintStrategy printStrategy) {
        super.setPrintStrategy(printStrategy);
        // Map the print strategy to a validation policy.
        // This isolates the coupling to this configuration method.
        this.validationPolicy = ValidationPolicyFactory.getPolicy(printStrategy);
    }

    @Override
    public void addProduct(Product product, int quantity, java.util.List<String> customTexts) throws TicketRuleViolationException {
        int linesBefore = getLines().size();
        super.addProduct(product, quantity, customTexts);
        int linesAfter = getLines().size();

        if (linesAfter > linesBefore) { // A new line was added
            if (!product.isService()) {
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
            if (!productToRemove.isService()) {
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
        // Delegate validation to the active policy.
        validationPolicy.validatePrint(productCount, serviceCount);

        return super.print();
    }

    @Override
    public boolean accepts(es.upm.etsisi.poo.domain.product.Product p) {
        return true;
    }

    @Override
    public void validateProduct(Product p) throws TicketRuleViolationException {
        // E3: Service Usage Limit - Check expiration date.
        if (p.isService()) {
            Service s = (Service) p;
            if (s.getExpirationDate().isBefore(LocalDateTime.now())) {
                throw new TicketRuleViolationException("Error: Service " + s.getName() + " has expired.");
            }
        }
        // Delegate strategy-specific product validation.
        validationPolicy.validateProduct(p);
    }

    @Override
    public double getTotalPrice() {
        double productTotal = 0.0;
        double serviceTotal = 0.0;
        int totalServiceQuantity = 0;

        for (TicketLine<Product> line : getLines()) {
            Product p = line.getProduct();
            // Use instanceof to detect Service
            if (p.isService()) {
                serviceTotal += line.getLineTotal();
                totalServiceQuantity += line.getQuantity();
            } else {
                productTotal += line.getLineTotal();
            }
        }

        // E3 Rule: 15% discount on PRODUCTS for EACH service.
        double discountFactor = 0.15 * totalServiceQuantity;
        if (discountFactor > 1.0) discountFactor = 1.0; // Cap at 100%

        return serviceTotal + (productTotal * (1.0 - discountFactor));
    }

    // --- Internal Strategy Pattern for Validation ---

    private interface ValidationPolicy extends Serializable {
        void validatePrint(int productCount, int serviceCount) throws TicketRuleViolationException;
        void validateProduct(Product p) throws TicketRuleViolationException;
    }

    private static class ValidationPolicyFactory {
        static ValidationPolicy getPolicy(PrintStrategy strategy) {
            if (strategy instanceof CompanyPrintStrategy) return new MixedPolicy();
            if (strategy instanceof ServicePrintStrategy) return new ServiceOnlyPolicy();
            if (strategy instanceof StandardPrintStrategy) return new ProductOnlyPolicy();
            return new DefaultPolicy();
        }
    }

    private static class MixedPolicy implements ValidationPolicy {
        @Override
        public void validatePrint(int pCount, int sCount) {
            if (pCount == 0 && sCount == 0) throw new TicketRuleViolationException("Error: Cannot print an empty company ticket.");
            if (pCount == 0 || sCount == 0) throw new TicketRuleViolationException("Error: Mixed ticket must contain at least one StandardProduct and one Service.");
        }
        @Override
        public void validateProduct(Product p) { /* Accepts all */ }
    }

    private static class ServiceOnlyPolicy implements ValidationPolicy {
        @Override
        public void validatePrint(int pCount, int sCount) {
            if (pCount > 0) throw new TicketRuleViolationException("Error: Service-only ticket cannot contain StandardProducts.");
            if (sCount == 0) throw new TicketRuleViolationException("Error: Service-only ticket must contain at least one Service.");
        }
        @Override
        public void validateProduct(Product p) {
            if (!p.isService()) throw new TicketRuleViolationException("Error: Service-only tickets cannot contain StandardProducts.");
        }
    }

    private static class ProductOnlyPolicy implements ValidationPolicy {
        @Override
        public void validatePrint(int pCount, int sCount) {
            if (sCount > 0) throw new TicketRuleViolationException("Error: Product-only ticket cannot contain Services.");
            if (pCount == 0) throw new TicketRuleViolationException("Error: Product-only ticket must contain at least one StandardProduct.");
        }
        @Override
        public void validateProduct(Product p) {
            if (p.isService()) throw new TicketRuleViolationException("Error: Product-only tickets cannot contain Services.");
        }
    }

    private static class DefaultPolicy implements ValidationPolicy {
        @Override
        public void validatePrint(int pCount, int sCount) {}
        @Override
        public void validateProduct(Product p) {}
    }
}