package es.upm.etsisi.poo.infrastructure.printing;

import es.upm.etsisi.poo.domain.ticket.Ticket;

import java.io.Serializable;

public interface PrintStrategy extends Serializable {
    String formatTicket(Ticket<?> ticket);
}
