package es.upm.etsisi.poo.application;

import java.util.List;
import java.util.ArrayList;

import es.upm.etsisi.poo.common.*;
import es.upm.etsisi.poo.domain.user.*;
import es.upm.etsisi.poo.domain.product.*;
import es.upm.etsisi.poo.domain.ticket.Ticket;

// Represents the store, acts as the model, holding all the application's data as per E2 requirements.
public class Store {
    private final Catalog catalog;
    private final List<Ticket> tickets;
    private final List<Client> clients;
    private final List<Cashier> cashiers;
    

    @SuppressWarnings("Convert2Diamond")
    public Store() {
        this.catalog = new Catalog();
        this.tickets = new ArrayList<Ticket>();
        this.clients = new ArrayList<Client>();
        this.cashiers = new ArrayList<Cashier>();
    }

    public void addProduct(Product product) {
        catalog.addProduct(product);
    }

    public void addClient(Client client) throws IllegalArgumentException {
        // If findClientById doesn't throw, the client exists.
        try {
            findClientById(client.getId());
            throw new IllegalArgumentException("Error: A client with that ID already exists.");
        } catch (UserNotFoundException e) {
            // This is the expected case, the client does not exist, so we can add it.
            this.clients.add(client);
        }
    }

    public Client findClientById(String id) throws UserNotFoundException {
        for (Client client : clients) {
            if (client.getId().equals(id)) {
                return client;
            }
        }
        throw new UserNotFoundException("Error: Client with ID " + id + " not found.");
    }

    public void removeClient(String id) throws UserNotFoundException {
        Client clientToRemove = findClientById(id);
        this.clients.remove(clientToRemove);
    }

    public void addCashier(Cashier cashier) throws IllegalArgumentException {
        // If findCashierByID doesn't throw, the client exists.
        try {
            findCashierById(cashier.getId());
            throw new IllegalArgumentException("Error: A cashier with that ID already exists.");
        } catch (UserNotFoundException e) {
            // This is the expected case, the cashier does not exist, so we can add it.
            this.cashiers.add(cashier);
        }
    }

    // Overloaded method to handle automatic ID generation for cashiers, this one is called by the handler.
    public void addCashier(String id, String name, String email) {
        String cashierId = id;
        if (cashierId == null || cashierId.isEmpty()) {
            cashierId = Cashier.generateCashierId(this.cashiers);
        }
        addCashier(new Cashier(cashierId, name, email));
    }

    public Cashier findCashierById(String id) throws UserNotFoundException {
        for (Cashier cashier : cashiers) {
            if (cashier.getId().equals(id)) {
                return cashier;
            }
        }
        throw new UserNotFoundException("Error: Cashier with ID " + id + " not found.");
    }

    public void removeCashier(String id) throws UserNotFoundException {
        Cashier cashierToRemove = findCashierById(id);
        // Remove tickets associated with the cashier.
        tickets.removeIf(ticket -> ticket.getCashierId().equals(id));
        this.cashiers.remove(cashierToRemove);
    }

    public Ticket createTicket(String id, String cashierId, String userId) throws UserNotFoundException {
        // Find the associated cashier (throws UserNotFoundException if not found).
        Cashier cashier = findCashierById(cashierId);
        Ticket newTicket = new Ticket(id, cashierId, userId);
        tickets.add(newTicket);

        //E2 requirement: the cashiers also keep track of their tickets.
        cashier.addTicket(newTicket);

        return newTicket;
    }

    public void addProductToTicket(String ticketId, String cashierId, int prodId, int amount,
            List<String> customTexts) throws ProductNotFoundException, UserNotFoundException {
        // Find the ticket.
        Ticket ticket = getTicket(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Error: Ticket not found.");
        }

        // E2 requirement: only the cashier who created the ticket can modify it.
        if (!ticket.getCashierId().equals(cashierId)) {
            throw new UserNotFoundException("Error: Only the creating cashier can modify this ticket.");
        }
        
        Product product = catalog.getProduct(prodId);
        ticket.addProduct(product, amount, customTexts);
    }

    public void removeProductFromTicket(String ticketId, String cashierId, int prodId) throws UserNotFoundException {
        // Find the ticket.
        Ticket ticket = getTicket(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Error: Ticket not found.");
        }

        // E2 requirement: only the cashier who created the ticket can modify it.
        if (!ticket.getCashierId().equals(cashierId)) {
            throw new UserNotFoundException("Error: Only the creating cashier can modify this ticket.");
        }
        if (!ticket.removeProduct(prodId)) {
            throw new IllegalArgumentException("Error: Product not found in ticket.");
        }
    }

    public void printTicket(String ticketId, String cashierId) throws UserNotFoundException {
        // Find the ticket.
        Ticket ticket = getTicket(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Error: Ticket not found.");
        }
        
        // E2 requirement: only the cashier who created the ticket can print it.
        if (!ticket.getCashierId().equals(cashierId)) {
            throw new UserNotFoundException("Error: Only the creating cashier can print this ticket.");
        }
        ticket.printAndClose();
    }

    public Ticket getTicket(String ticketId) {
        for (Ticket ticket : tickets) {
            if (ticket.getId().equals(ticketId)) {
                return ticket;
            }
        }
        return null;
    }

    @SuppressWarnings("Convert2Diamond")
    public List<Ticket> getTickets() {
        return new ArrayList<Ticket>(tickets);
    }

    public Catalog getCatalog() {
        return catalog;
    }

    @SuppressWarnings("Convert2Diamond")
    public List<Client> getClients() {
        return new ArrayList<Client>(clients);
    }

    @SuppressWarnings("Convert2Diamond")
    public List<Cashier> getCashiers() {
        return new ArrayList<Cashier>(cashiers);
    }
}