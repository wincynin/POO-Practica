package es.upm.etsisi.poo.infrastructure.printing;

import es.upm.etsisi.poo.domain.printing.PrintStrategy;
import es.upm.etsisi.poo.domain.product.Product;
import es.upm.etsisi.poo.domain.product.StandardProduct;
import es.upm.etsisi.poo.domain.ticket.Ticket;
import es.upm.etsisi.poo.domain.ticket.TicketLine;

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
            if (product != null && !(product instanceof StandardProduct)) {
                sb.append(String.format("Name: %s, Price: HIDDEN\n", product.getName()));
                totalServicePrice += line.getLineTotal();
            }
        }

        sb.append("--------------------\n");
        sb.append(String.format("Total (Services): %.2f\n", totalServicePrice));
        return sb.toString();
    }
}