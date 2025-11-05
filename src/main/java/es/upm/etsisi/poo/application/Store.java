package es.upm.etsisi.poo.application;

import java.util.ArrayList;
import java.util.List;
import es.upm.etsisi.poo.common.IdGenerator;
import es.upm.etsisi.poo.common.ProductNotFoundException;
import es.upm.etsisi.poo.common.UserNotFoundException;
import es.upm.etsisi.poo.domain.product.Catalog;
import es.upm.etsisi.poo.domain.product.Product;
import es.upm.etsisi.poo.domain.ticket.Ticket;
import es.upm.etsisi.poo.domain.user.Cashier;
import es.upm.etsisi.poo.domain.user.Client;

public class Store {
    private final Catalog catalog;
    private final List<Client> clients;
    private final List<Cashier> cashiers;
    private final List<Ticket> tickets; // Added for E2

    @SuppressWarnings("Convert2Diamond")
    public Store() {
        this.catalog = new Catalog();
        this.clients = new ArrayList<Client>();
        this.cashiers = new ArrayList<Cashier>();
        this.tickets = new ArrayList<Ticket>(); // Added for E2
    }

    public void addProduct(Product product) {
        if (product.getId() == 0) {
            int newId = IdGenerator.generateProductId(this.catalog);
            Product newProduct = product.copyWithNewId(newId);
            catalog.addProduct(newProduct);
        } else {
            catalog.addProduct(product);
        }
    }

    public void addClient(Client client) throws IllegalArgumentException {
        try {
            findClientById(client.getId());
            // If we get here, the client exists
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
        try {
            findCashierById(cashier.getId());
            throw new IllegalArgumentException("Error: A cashier with that ID already exists.");
        } catch (UserNotFoundException e) {
            this.cashiers.add(cashier);
        }
    }

    public void addCashier(String id, String name, String email) {
        String cashierId = id;
        if (cashierId == null || cashierId.isEmpty()) {
            cashierId = IdGenerator.generateCashierId(this.cashiers);
        }
        try {
            addCashier(new Cashier(cashierId, name, email));
        } catch (IllegalArgumentException e) {
            // This should not happen if the generator works correctly
            System.out.println("Internal error: generated a duplicate cashier ID.");
        }
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
        // Remove tickets associated with the cashier
        tickets.removeIf(ticket -> ticket.getCashierId().equals(id));
        this.cashiers.remove(cashierToRemove);
    }

    public Ticket createTicket(String id, String cashierId, String userId) throws UserNotFoundException {
        Cashier cashier = findCashierById(cashierId);
        Ticket newTicket = new Ticket(id, cashierId, userId);
        tickets.add(newTicket);
        cashier.addTicket(newTicket);
        return newTicket;
    }

    public void addProductToTicket(String ticketId, String cashierId, int prodId, int amount,
            List<String> customTexts) throws ProductNotFoundException, UserNotFoundException {
        Ticket ticket = getTicket(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Error: Ticket not found.");
        }
        if (!ticket.getCashierId().equals(cashierId)) {
            throw new UserNotFoundException("Error: Only the creating cashier can modify this ticket.");
        }
        Product product = catalog.getProduct(prodId);
        ticket.addProduct(product, amount, customTexts);
    }

    public void removeProductFromTicket(String ticketId, String cashierId, int prodId) throws UserNotFoundException {
        Ticket ticket = getTicket(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Error: Ticket not found.");
        }
        if (!ticket.getCashierId().equals(cashierId)) {
            throw new UserNotFoundException("Error: Only the creating cashier can modify this ticket.");
        }
        if (!ticket.removeProduct(prodId)) {
            throw new IllegalArgumentException("Error: Product not found in ticket.");
        }
    }

    public void printTicket(String ticketId, String cashierId) throws UserNotFoundException {
        Ticket ticket = getTicket(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Error: Ticket not found.");
        }
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
