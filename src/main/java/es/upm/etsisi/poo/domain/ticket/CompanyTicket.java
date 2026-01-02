package es.upm.etsisi.poo.domain.ticket;

import java.io.Serializable;
import java.time.LocalDateTime;

import es.upm.etsisi.poo.domain.exceptions.TicketRuleViolationException;
import es.upm.etsisi.poo.domain.printing.PrintStrategy;
import es.upm.etsisi.poo.domain.product.Product;
import es.upm.etsisi.poo.domain.product.Service;

// [Class] Ticket implementation for specific Client types.
public class CompanyTicket extends Ticket<Product> {
    private int productCount;
    private int serviceCount;
    private ValidationPolicy validationPolicy;

    public CompanyTicket(String id, TicketPrintType printType) {
        super(id);
        this.validationPolicy = ValidationPolicyFactory.getPolicy(printType);
        this.productCount = 0;
        this.serviceCount = 0;
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