package es.upm.etsisi.poo.domain.ticket;

import java.io.Serializable;

import es.upm.etsisi.poo.domain.product.Product;
import es.upm.etsisi.poo.domain.exceptions.TicketRuleViolationException;

// [Class] Ticket implementation for specific Client types.
public class CompanyTicket extends Ticket<Product> {
    private final ValidationPolicy validationPolicy;

    public CompanyTicket(String id, TicketPrintType printType) {
        super(id);
        this.validationPolicy = ValidationPolicyFactory.getPolicy(printType);
    }

    @Override
    public boolean accepts(Product p) {
        try {
            validateProduct(p);
            return true;
        } catch (TicketRuleViolationException e) {
            return false;
        }
    }

    @Override
    public void validateProduct(Product p) throws TicketRuleViolationException {
        validationPolicy.validateProduct(p);
    }

    @Override
    public String print() {
        int pCount = 0;
        int sCount = 0;
        for (TicketLine<Product> line : getLines()) {
            if (line.getProduct().isService()) {
                sCount++;
            } else {
                pCount++;
            }
        }
        validationPolicy.validatePrint(pCount, sCount);
        return super.print();
    }

    @Override
    public double getTotalPrice() {
        double standardProductsTotal = 0.0;
        int numServices = 0;

        for (TicketLine<Product> line : getLines()) {
            Product product = line.getProduct();
            if (product.isService()) {
                numServices++;
            } else {
                standardProductsTotal += line.getLineTotal();
            }
        }

        double discountRate = Math.min(numServices * 0.15, 1.0);
        return standardProductsTotal * (1.0 - discountRate);
    }

    private interface ValidationPolicy extends Serializable {
        void validatePrint(int productCount, int serviceCount) throws TicketRuleViolationException;
        void validateProduct(Product p) throws TicketRuleViolationException;
    }

    private static class ValidationPolicyFactory {
        static ValidationPolicy getPolicy(TicketPrintType printType) {
            switch (printType) {
                case COMPANY:
                    return new MixedPolicy();
                case SERVICE:
                    return new ServiceOnlyPolicy();
                case STANDARD:
                    return new ProductOnlyPolicy();
                default:
                    return new DefaultPolicy();
            }
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