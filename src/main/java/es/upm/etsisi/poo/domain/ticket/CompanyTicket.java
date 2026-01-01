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
    public boolean removeProduct(int productId) throws TicketRuleViolationException {
        Product productToRemove = null;
        for (TicketLine<Product> line : getLines()) {
            if (line.getProduct().getId() == productId) {
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
        if (getPrintStrategy() instanceof CompanyPrintStrategy) {
            if (productCount > 0 && serviceCount == 0 || productCount == 0 && serviceCount > 0) {
                // This is not a mixed ticket, so it's valid.
            } else if (productCount == 0 && serviceCount == 0) {
                 throw new TicketRuleViolationException("Error: Cannot print an empty company ticket.");
            }
            else if (productCount > 0 && serviceCount > 0) {
                // This is a mixed ticket, which is fine
            }
        }
        return super.print();
    }
}