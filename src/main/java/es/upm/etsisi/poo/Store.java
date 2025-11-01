package es.upm.etsisi.poo;

import java.util.ArrayList;
import java.util.List;

public class Store {
    private final Catalog catalog;
    private final List<Client> clients;
    private final List<Cashier> cashiers;
    private final List<Ticket> tickets; // Added for E2

    public Store() {
        this.catalog = new Catalog();
        this.clients = new ArrayList<>();
        this.cashiers = new ArrayList<>();
        this.tickets = new ArrayList<>(); // Added for E2
    }

    public void addClient(Client client) {
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

    public boolean removeClient(String id) {
        Client clientToRemove = findClientById(id);
        if (clientToRemove != null) {
            return this.clients.remove(clientToRemove);
        }
        return false;
    }

    public void addCashier(Cashier cashier) {
        if (findCashierById(cashier.getId()) != null) {
            throw new IllegalArgumentException("Error: A cashier with that ID already exists.");
        }
        this.cashiers.add(cashier);
    }

    public Cashier findCashierById(String id) {
        for (Cashier cashier : cashiers) {
            if (cashier.getId().equals(id)) {
                return cashier;
            }
        }
        return null;
    }

    public boolean removeCashier(String id) {
        Cashier cashierToRemove = findCashierById(id);
        if (cashierToRemove != null) {
            // Remove tickets associated with the cashier
            for (int i = 0; i < tickets.size(); i++) {
                if (tickets.get(i).getCashierId().equals(id)) {
                    tickets.remove(i);
                    i--; // Decrement to avoid skipping the next element
                }
            }
            return this.cashiers.remove(cashierToRemove);
        }
        return false;
    }

    public Ticket createTicket(String id, String cashierId, String userId) {
        Ticket newTicket = new Ticket(id, cashierId, userId);
        tickets.add(newTicket);
        return newTicket;
    }

    public Ticket getTicket(String ticketId) {
        for (Ticket ticket : tickets) {
            if (ticket.getId().equals(ticketId)) {
                return ticket;
            }
        }
        return null;
    }

    public List<Ticket> getTickets() {
        return new ArrayList<>(tickets);
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public List<Client> getClients() {
        return new ArrayList<>(clients);
    }

    public List<Cashier> getCashiers() {
        return new ArrayList<>(cashiers);
    }

    public String generateCashierId() {
        String id;
        do {
            int randomDigits = (int) (Math.random() * 10000000);
            id = "UW" + String.format("%07d", randomDigits);
        } while (findCashierById(id) != null);
        return id;
    }
}