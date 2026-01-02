package es.upm.etsisi.poo.infrastructure.printing;

import es.upm.etsisi.poo.domain.ticket.Ticket;
import es.upm.etsisi.poo.domain.product.Product;
import es.upm.etsisi.poo.domain.ticket.TicketLine;
import es.upm.etsisi.poo.domain.printing.PrintStrategy;

// [Class] Prints ticket in service format.
public class ServicePrintStrategy implements PrintStrategy {
    @Override
    public String formatTicket(Ticket<?> ticket) {
        StringBuilder sb = new StringBuilder();
        sb.append("Ticket ID: ").append(ticket.getId()).append(" (Services Only)\n");
        sb.append("--------------------\n");

        double totalServicePrice = 0;

        for (TicketLine<?> line : ticket.getLines()) {
            Product product = line.getProduct();

            // If you strictly only want to sum services:
            if (product.isService()) {
                totalServicePrice += line.getLineTotal();
            }

            // Service.java returns "Price: HIDDEN" and StandardProduct.java returns "Price: 10.00"
            sb.append(String.format("Name: %s, %s\n",
                product.getName(),
                product.getPrintablePriceDetails()));
        }

        sb.append("--------------------\n");
        sb.append(String.format("Total (Services): %.2f\n", totalServicePrice));
        return sb.toString();
    }
}