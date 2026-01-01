package es.upm.etsisi.poo.infrastructure.printing;

import java.io.Serializable;
import es.upm.etsisi.poo.domain.ticket.Ticket;

// [Interface] Defines how to print a ticket.
public interface PrintStrategy extends Serializable {
    String formatTicket(Ticket<?> ticket);
}