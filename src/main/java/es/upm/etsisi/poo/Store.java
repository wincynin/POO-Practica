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

    public void addProduct(Product product) {
        if (product.getId() == 0) {
            int newId = generateProductId();
            Product newProduct;
            if (product instanceof CustomizableProduct) {
                CustomizableProduct cp = (CustomizableProduct) product;
                newProduct = new CustomizableProduct(newId, cp.getName(), cp.getCategory(), cp.getPrice(), cp.getMaxCustomizableTexts());
            } else if (product instanceof Food) {
                Food f = (Food) product;
                newProduct = new Food(newId, f.getName(), f.getPrice(), f.getExpirationDate(), f.getMaxParticipants());
            } else if (product instanceof Meeting) {
                Meeting m = (Meeting) product;
                newProduct = new Meeting(newId, m.getName(), m.getPrice(), m.getExpirationDate(), m.getMaxParticipants());
            } else {
                newProduct = new Product(newId, product.getName(), product.getCategory(), product.getPrice());
            }
            catalog.addProduct(newProduct);
        } else {
            catalog.addProduct(product);
        }
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

    public void addCashier(String id, String name, String email) {
        String cashierId = id;
        if (cashierId == null || cashierId.isEmpty()) {
            cashierId = generateCashierId();
        }
        Cashier newCashier = new Cashier(cashierId, name, email);
        addCashier(newCashier);
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
        Cashier cashier = findCashierById(cashierId);
        if (cashier == null) {
            throw new IllegalArgumentException("Error: Cashier not found.");
        }
        Client client = findClientById(userId);
        if (client == null) {
            throw new IllegalArgumentException("Error: Client not found.");
        }

        Ticket newTicket = new Ticket(id, cashierId, userId);
        tickets.add(newTicket);
        cashier.addTicket(newTicket);
        return newTicket;
    }

    public void addProductToTicket(String ticketId, String cashierId, int prodId, int amount, List<String> customTexts) {
        Ticket ticket = getTicket(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Error: Ticket not found.");
        }
        if (!ticket.getCashierId().equals(cashierId)) {
            throw new IllegalArgumentException("Error: Only the creating cashier can modify this ticket.");
        }
        Product product = catalog.getProduct(prodId);
        if (product == null) {
            throw new IllegalArgumentException("Error: Product not found.");
        }
        ticket.addProduct(product, amount, customTexts);
    }

    public void removeProductFromTicket(String ticketId, String cashierId, int prodId) {
        Ticket ticket = getTicket(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Error: Ticket not found.");
        }
        if (!ticket.getCashierId().equals(cashierId)) {
            throw new IllegalArgumentException("Error: Only the creating cashier can modify this ticket.");
        }
        if (!ticket.removeProduct(prodId)) {
            throw new IllegalArgumentException("Error: Product not found in ticket.");
        }
    }

    public void printTicket(String ticketId, String cashierId) {
        Ticket ticket = getTicket(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Error: Ticket not found.");
        }
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

    public int generateProductId() {
        int id;
        do {
            id = (int) (Math.random() * 100000) + 1; // Generate a random ID between 1 and 100000
        } while (catalog.getProduct(id) != null); // Check if ID already exists in catalog
        return id;
    }
}