package es.upm.etsisi.poo.infrastructure.printing;

import es.upm.etsisi.poo.domain.ticket.Ticket;

public interface PrintStrategy {
    String formatTicket(Ticket<?> ticket);
}
