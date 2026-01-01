package es.upm.etsisi.poo.infrastructure.printing;

import java.io.Serializable;
import es.upm.etsisi.poo.domain.ticket.Ticket;

// Strategy interface for printing tickets in different formats.
public interface PrintStrategy extends Serializable {
    String formatTicket(Ticket<?> ticket);
}