package es.upm.etsisi.poo.application;

import es.upm.etsisi.poo.domain.product.Catalog;
import es.upm.etsisi.poo.domain.product.Product;
import es.upm.etsisi.poo.domain.ticket.Ticket;
import es.upm.etsisi.poo.domain.user.Cashier;
import es.upm.etsisi.poo.domain.user.Client;

import java.util.ArrayList;
import java.util.List;

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
        // If findClientById doesn't return null, the client exists.
        if (findClientById(client.getId()) != null) {
            throw new IllegalArgumentException("Error: A client with that ID already exists.");
        }
        this.clients.add(client);
    }

    public Client findClientById(String id) {
        for (Client client : clients) {
            if (client.getId().equals(id)) {
                return client;
            }
        }
        return null;
    }

    public void removeClient(String id) {
        Client clientToRemove = findClientById(id);
        if (clientToRemove != null) {
            this.clients.remove(clientToRemove);
        }
    }

    public void addCashier(Cashier cashier) throws IllegalArgumentException {
        // If findCashierByID doesn't return null, the cashier exists.
        if (findCashierById(cashier.getId()) != null) {
            throw new IllegalArgumentException("Error: A cashier with that ID already exists.");
        }
        this.cashiers.add(cashier);
    }

    // Overloaded method to handle automatic ID generation for cashiers, this one is called by the handler.
    public void addCashier(String id, String name, String email) {
        String cashierId = id;
        if (cashierId == null || cashierId.isEmpty()) {
            cashierId = Cashier.generateCashierId(this.cashiers);
        }
        addCashier(new Cashier(cashierId, name, email));
    }

    public Cashier findCashierById(String id) {
        for (Cashier cashier : cashiers) {
            if (cashier.getId().equals(id)) {
                return cashier;
            }
        }
        return null;
    }

    public void removeCashier(String id) {
        Cashier cashierToRemove = findCashierById(id);
        if (cashierToRemove != null) {
            // Remove tickets associated with the cashier.
            tickets.removeIf(ticket -> ticket.getCashierId().equals(id));
            this.cashiers.remove(cashierToRemove);
        }
    }

    public Ticket createTicket(String id, String cashierId, String userId) {
        // Find the associated cashier.
        Cashier cashier = findCashierById(cashierId);
        if (cashier == null) {
            throw new IllegalArgumentException("Error: Cashier with ID " + cashierId + " not found.");
        }
        // Find the associated client.
        Client client = findClientById(userId);
        if (client == null) {
            throw new IllegalArgumentException("Error: Client with ID " + userId + " not found.");
        }
        Ticket newTicket = new Ticket(id, cashierId, client);
        tickets.add(newTicket);

        return newTicket;
    }

    public void addProductToTicket(String ticketId, String cashierId, int prodId, int amount,
            List<String> customTexts) {
        // Find the ticket.
        Ticket ticket = getTicket(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Error: Ticket not found.");
        }

        // E2 requirement: only the cashier who created the ticket can modify it.
        if (!ticket.getCashierId().equals(cashierId)) {
            throw new IllegalArgumentException("Error: Only the creating cashier can modify this ticket.");
        }
        
        Product product = catalog.getProduct(prodId);
        if (product == null) {
            throw new IllegalArgumentException("Error: Product with ID " + prodId + " not found.");
        }
        ticket.addProduct(product, amount, customTexts);
    }

    public void removeProductFromTicket(String ticketId, String cashierId, int prodId) {
        // Find the ticket.
        Ticket ticket = getTicket(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Error: Ticket not found.");
        }

        // E2 requirement: only the cashier who created the ticket can modify it.
        if (!ticket.getCashierId().equals(cashierId)) {
            throw new IllegalArgumentException("Error: Only the creating cashier can modify this ticket.");
        }
        if (!ticket.removeProduct(prodId)) {
            throw new IllegalArgumentException("Error: Product not found in ticket.");
        }
    }

    public void printTicket(String ticketId, String cashierId) {
        // Find the ticket.
        Ticket ticket = getTicket(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Error: Ticket not found.");
        }
        
        // E2 requirement: only the cashier who created the ticket can print it.
        if (!ticket.getCashierId().equals(cashierId)) {
            throw new IllegalArgumentException("Error: Only the creating cashier can print this ticket.");
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

    public List<Ticket> getTicketsByCashierId(String cashierId) {
        List<Ticket> cashierTickets = new ArrayList<>();
        for (Ticket ticket : tickets) {
            if (ticket.getCashierId().equals(cashierId)) {
                cashierTickets.add(ticket);
            }
        }
        return cashierTickets;
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