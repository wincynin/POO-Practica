package es.upm.etsisi.poo.infrastructure.printing;

import es.upm.etsisi.poo.domain.product.Product;
import es.upm.etsisi.poo.domain.ticket.Ticket;
import es.upm.etsisi.poo.domain.ticket.TicketLine;

public class StandardPrintStrategy implements PrintStrategy {

    @Override
    public String formatTicket(Ticket<?> ticket) {
        StringBuilder sb = new StringBuilder();
        sb.append("Ticket ID: ").append(ticket.getId()).append("\n");
        sb.append("--------------------\n");

        for (TicketLine<?> line : ticket.getLines()) {
            Product product = line.getProduct();
            sb.append(String.format("Name: %s, Price: %.2f\n", product.getName(), product.getPrice()));
        }

        sb.append("--------------------\n");
        sb.append(String.format("Total price: %.2f\n", ticket.getTotalPrice()));
        return sb.toString();
    }
}

