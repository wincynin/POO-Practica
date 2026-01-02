package es.upm.etsisi.poo.infrastructure.printing;

import es.upm.etsisi.poo.domain.ticket.Ticket;
import es.upm.etsisi.poo.domain.product.Product;
import es.upm.etsisi.poo.domain.ticket.TicketLine;
import es.upm.etsisi.poo.domain.printing.PrintStrategy;

// [Class] Prints ticket in company format.
public class CompanyPrintStrategy implements PrintStrategy {

    private static final double MIXED_TICKET_DISCOUNT_RATE = 0.15;

    @Override
    public String formatTicket(Ticket<?> ticket) {
        StringBuilder sb = new StringBuilder();
        sb.append("Ticket ID: ").append(ticket.getId()).append(" (Company)\n");
        sb.append("--------------------");

        int numServices = 0;
        double standardProductsTotal = 0;

        for (TicketLine<?> line : ticket.getLines()) {
            Product product = line.getProduct();
            if (product.isService()) {
                numServices++;
            } else {
                standardProductsTotal += line.getLineTotal();
            }
        }

        double discountRate = Math.min(numServices * MIXED_TICKET_DISCOUNT_RATE, 1.0);
        double totalDiscount = standardProductsTotal * discountRate;
        double finalPrice = standardProductsTotal - totalDiscount;
        
        for (TicketLine<?> line : ticket.getLines()) {
            Product product = line.getProduct();
            
            sb.append(String.format("Name: %s%s, %s\n",
                product.getName(),
                product.getExpirationDetails(),
                product.getPrintablePriceDetails()));
        }

        sb.append("--------------------");
        sb.append(String.format("Total (Standard Products): %.2f\n", standardProductsTotal));
        sb.append(String.format("Discount (%.0f%%): -%.2f\n", discountRate * 100, totalDiscount));
        sb.append(String.format("Final Price: %.2f\n", finalPrice));

        return sb.toString();
    }
}