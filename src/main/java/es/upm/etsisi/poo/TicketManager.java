package es.upm.etsisi.poo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Manages all tickets in the system for E2.
 */
public class TicketManager {

    private Map<String, Ticket> ticketsById; // Key: Ticket ID (String), Value: Ticket object
    private Set<String> usedTicketIds; // To ensure ticket ID uniqueness across states

    /**
     * Constructor for TicketManager.
     */
    public TicketManager() {
        this.ticketsById = new HashMap<>();
        this.usedTicketIds = new HashSet<>();
    }

    /**
     * Creates a new ticket.
     * 
     * @param id          Optional initial ID.
     * @param cashierId   ID of the creating cashier.
     * @param clientId    ID of the associated client.
     * @param userManager Reference to UserManager to check user existence.
     * @return The newly created Ticket object, or null on failure.
     */
    public Ticket createTicket(String id, String cashierId, String clientId, UserManager userManager) {
        if (!userManager.cashierExists(cashierId)) {
            System.out.println("Error creating ticket: Cashier ID " + cashierId + " not found.");
            return null;
        }
        if (!userManager.clientExists(clientId)) {
            System.out.println("Error creating ticket: Client ID " + clientId + " not found.");
            return null;
        }

        try {
            Ticket newTicket = new Ticket(id, cashierId, clientId, this.usedTicketIds);
            ticketsById.put(newTicket.getId(), newTicket);

            // Link ticket to cashier
            Cashier c = userManager.getCashier(cashierId);
            if (c != null)
                c.addCreatedTicketId(newTicket.getId());

            // TODO: Link ticket to client? E2 says client knows its tickets.
            // Client cl = userManager.getClient(clientId);
            // if(cl != null) cl.addTicketId(newTicket.getId());

            System.out.println("Created new ticket with ID: " + newTicket.getId());
            return newTicket;
        } catch (IllegalArgumentException e) {
            System.out.println("Error creating ticket: " + e.getMessage());
            return null;
        }
    }

    /**
     * Retrieves a ticket by its ID.
     * 
     * @param ticketId The ID of the ticket.
     * @return The Ticket object, or null if not found.
     */
    public Ticket getTicket(String ticketId) {
        return ticketsById.get(ticketId);
    }

    /**
     * Gets the status of a specific ticket.
     * 
     * @param ticketId The ID of the ticket.
     * @return The TicketState as a string, or "NOT_FOUND".
     */
    public String getTicketStatus(String ticketId) {
        Ticket ticket = ticketsById.get(ticketId);
        return (ticket != null) ? ticket.getState().toString() : "NOT_FOUND";
    }

    /**
     * Adds a product to a specific ticket.
     * 
     * @param ticketId        ID of the ticket.
     * @param actingCashierId ID of the cashier attempting the action.
     * @param product         Product to add.
     * @param quantity        Amount to add.
     * @param store           Reference to Store to validate product existence.
     * @return true if successful, false otherwise.
     */
    public boolean addProductToTicket(String ticketId, String actingCashierId, int productId, int quantity,
            Store store) {
        Ticket ticket = getTicket(ticketId);
        if (ticket == null) {
            System.out.println("Error: Ticket ID " + ticketId + " not found.");
            return false;
        }
        // E2 Check: Same cashier?
        if (!ticket.getCashierId().equals(actingCashierId)) {
            System.out.println("Error: Cashier " + actingCashierId + " cannot modify ticket " + ticketId
                    + " created by " + ticket.getCashierId());
            return false;
        }

        Product product = store.getProduct(productId);
        if (product == null) {
            System.out.println("Error: Product ID " + productId + " not found in store.");
            return false;
        }

        // Delegate the actual add logic (and state checks) to the Ticket object
        if (ticket.addProduct(product, quantity)) {
            // Provisional total printed by ticket.addProduct()
            return true;
        } else {
            // Error message printed by ticket.addProduct()
            return false;
        }
        // TODO: Handle custom product details (--p<txt>) here or pass them to
        // ticket.addProduct
    }

    /**
     * Removes a product from a specific ticket.
     * 
     * @param ticketId        ID of the ticket.
     * @param actingCashierId ID of the cashier attempting the action.
     * @param productId       ID of the product to remove.
     * @return true if successful, false otherwise.
     */
    public boolean removeProductFromTicket(String ticketId, String actingCashierId, int productId) {
        Ticket ticket = getTicket(ticketId);
        if (ticket == null) {
            System.out.println("Error: Ticket ID " + ticketId + " not found.");
            return false;
        }
        // E2 Check: Same cashier?
        if (!ticket.getCashierId().equals(actingCashierId)) {
            System.out.println("Error: Cashier " + actingCashierId + " cannot modify ticket " + ticketId
                    + " created by " + ticket.getCashierId());
            return false;
        }

        // Delegate remove logic (and state checks) to Ticket object
        if (ticket.removeProduct(productId)) {
            // Provisional total printed by ticket.removeProduct()
            return true;
        } else {
            System.out.println("Error: Product ID " + productId + " not found in ticket " + ticketId + ".");
            return false;
        }
    }

    /**
     * Prints and closes a ticket.
     * 
     * @param ticketId        ID of the ticket to print and close.
     * @param actingCashierId ID of the cashier attempting the action.
     * @return true if successful, false otherwise.
     */
    public boolean printAndCloseTicket(String ticketId, String actingCashierId) {
        Ticket ticket = getTicket(ticketId);
        if (ticket == null) {
            System.out.println("Error: Ticket ID " + ticketId + " not found.");
            return false;
        }
        // E2 Check: Same cashier?
        if (!ticket.getCashierId().equals(actingCashierId)) {
            System.out.println("Error: Cashier " + actingCashierId + " cannot print/close ticket " + ticketId
                    + " created by " + ticket.getCashierId());
            return false;
        }

        // If already closed, just print details again
        if (ticket.getState() == Ticket.TicketState.CERRADO) {
            System.out.println("Ticket " + ticketId + " is already closed. Printing details again:");
            ticket.printTicketDetails();
            return true;
        }

        // Attempt to close the ticket (updates state and ID)
        String oldId = ticket.getId();
        if (ticket.closeTicket(this.usedTicketIds)) {
            // Update the map key if ID changed on close
            if (!oldId.equals(ticket.getId())) {
                ticketsById.remove(oldId);
                ticketsById.put(ticket.getId(), ticket);
                System.out.println("Ticket ID updated on close to: " + ticket.getId());
            }
            ticket.printTicketDetails(); // Print the final invoice details
            return true;
        } else {
            System.out.println("Error: Failed to close ticket " + ticketId + ".");
            // Error message possibly printed by ticket.closeTicket()
            return false;
        }
    }

    /**
     * Removes multiple tickets, typically used when a cashier is deleted.
     * 
     * @param ticketIdsToRemove List of ticket IDs to remove.
     */
    public void removeTickets(List<String> ticketIdsToRemove, UserManager userManager) {
        if (ticketIdsToRemove == null)
            return;
        int count = 0;
        for (String ticketId : ticketIdsToRemove) {
            Ticket removed = ticketsById.remove(ticketId);
            if (removed != null) {
                usedTicketIds.remove(ticketId); // Free up ID
                // TODO: Remove ticket reference from Client?
                // Client client = userManager.getClient(removed.getClientId());
                // if (client != null) client.removeTicketId(ticketId);
                count++;
            }
        }
        if (count > 0) {
            System.out.println("Removed " + count + " ticket(s) associated with the deleted cashier.");
        }
    }

    /**
     * Gets a list of all tickets, sorted by Cashier ID, then Ticket ID.
     * 
     * @return Sorted list of all tickets.
     */
    public List<Ticket> getAllTicketsSorted() {
        List<Ticket> ticketList = new ArrayList<>(ticketsById.values());

        // Sort primarily by Cashier ID, secondarily by Ticket ID
        ticketList.sort(Comparator.comparing(Ticket::getCashierId)
                .thenComparing(Ticket::getId));
        return ticketList;
    }
}