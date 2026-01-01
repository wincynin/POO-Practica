package es.upm.etsisi.poo.application;

import es.upm.etsisi.poo.domain.user.*;
import es.upm.etsisi.poo.domain.ticket.Ticket;
import es.upm.etsisi.poo.domain.product.Catalog;
import es.upm.etsisi.poo.domain.product.Product;
import es.upm.etsisi.poo.domain.ticket.CommonTicket;
import es.upm.etsisi.poo.domain.ticket.CompanyTicket;
import es.upm.etsisi.poo.domain.product.StandardProduct;
import es.upm.etsisi.poo.domain.ticket.TicketRepository;

import java.util.List;
import java.util.ArrayList;


// Represents the store, acts as the model, holding all the application's data as per E2 requirements.
public class Store implements java.io.Serializable {
    private final Catalog catalog;
    private final TicketRepository ticketRepository;
    private final ClientRepository clientRepository;
    private final CashierRepository cashierRepository;
    

    @SuppressWarnings("Convert2Diamond")
    public Store() {
        this.catalog = new Catalog();
        this.ticketRepository = new TicketRepository();
        this.clientRepository = new ClientRepository();
        this.cashierRepository = new CashierRepository();
    }

    public void addProduct(Product product) {
        catalog.addProduct(product);
    }

    public void addClient(Client client) throws IllegalArgumentException {
        // If findClientById doesn't return null, the client exists.
        clientRepository.add(client);
    }

    public Client findClientById(String id) {
        return clientRepository.findById(id);
    }

    public void removeClient(String id) {
        clientRepository.remove(id);
    }

    public void addCashier(Cashier cashier) throws IllegalArgumentException {
        // If findCashierByID doesn't return null, the cashier exists.
        cashierRepository.add(cashier);
    }

    // Overloaded method to handle automatic ID generation for cashiers, this one is called by the handler.
    public void addCashier(String id, String name, String email) {
        String cashierId = id;
        if (cashierId == null || cashierId.isEmpty()) {
            cashierId = Cashier.generateCashierId(this.cashierRepository.getAll());
        }
        addCashier(new Cashier(cashierId, name, email));
    }

    public Cashier findCashierById(String id) {
        return cashierRepository.findById(id);
    }

    public void removeCashier(String id) {
        Cashier cashierToRemove = findCashierById(id);
        if (cashierToRemove != null) {
            // Remove tickets associated with the cashier.
            List<Ticket<?>> ticketsToRemove = cashierToRemove.getTickets();
            for(Ticket<?> ticket : ticketsToRemove) {
                ticketRepository.remove(ticket);
            }
            this.cashierRepository.remove(id);
        }
    }

    public Ticket<?> createTicket(String id, String cashierId, String userId, char flag) {
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
        
        Ticket<?> newTicket;
        if (client instanceof IndividualClient) {
            newTicket = new CommonTicket(id);
        } else if (client instanceof CompanyClient) {
            newTicket = new CompanyTicket(id);
            switch (flag) {
                case 's':
                    newTicket.setPrintStrategy(new es.upm.etsisi.poo.infrastructure.printing.ServicePrintStrategy());
                    break;
                case 'c':
                    newTicket.setPrintStrategy(new es.upm.etsisi.poo.infrastructure.printing.CompanyPrintStrategy());
                    break;
                case 'p':
                default:
                    newTicket.setPrintStrategy(new es.upm.etsisi.poo.infrastructure.printing.StandardPrintStrategy());
                    break;
            }
        } else {
            throw new IllegalStateException("Error: Unknown client type.");
        }
        
        // Store establishes the relationships
        ticketRepository.add(newTicket);
        cashier.addTicket(newTicket);
        client.addTicket(newTicket);

        return newTicket;
    }

    public void addProductToTicket(String ticketId, String cashierId, int prodId, int amount,
            List<String> customTexts) {
        // Find the ticket.
        Ticket<?> ticket = getTicket(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Error: Ticket not found.");
        }

        Cashier cashier = findCashierById(cashierId);
        // E2 requirement: only the cashier who created the ticket can modify it.
        if (cashier == null || !cashier.hasTicket(ticketId)) {
            throw new IllegalArgumentException("Error: Only the creating cashier can modify this ticket.");
        }
        
        Product product = catalog.getProduct(prodId);
        if (product == null) {
            throw new IllegalArgumentException("Error: Product with ID " + prodId + " not found.");
        }

        if (ticket instanceof CommonTicket) {
            if (!(product instanceof StandardProduct)) {
                throw new IllegalArgumentException("Error: Cannot add this product type to a Common Ticket.");
            }
            @SuppressWarnings("unchecked")
            Ticket<StandardProduct> commonTicket = (Ticket<StandardProduct>) ticket;
            commonTicket.addProduct((StandardProduct) product, amount, customTexts);
        } else if (ticket instanceof CompanyTicket) {
            @SuppressWarnings("unchecked")
            Ticket<Product> companyTicket = (Ticket<Product>) ticket;
            companyTicket.addProduct(product, amount, customTexts);
        } else {
            throw new IllegalStateException("Error: Unknown ticket type, cannot add product.");
        }
    }

    public void removeProductFromTicket(String ticketId, String cashierId, int prodId) {
        // Find the ticket.
        Ticket<?> ticket = getTicket(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Error: Ticket not found.");
        }

        Cashier cashier = findCashierById(cashierId);
        
        // E2 requirement: only the cashier who created the ticket can modify it.
        if (cashier == null || !cashier.hasTicket(ticketId)) {
            throw new IllegalArgumentException("Error: Only the creating cashier can modify this ticket.");
        }
        if (!ticket.removeProduct(prodId)) {
            throw new IllegalArgumentException("Error: Product not found in ticket.");
        }
    }

    public String printTicket(String ticketId, String cashierId) {
        // Find the ticket.
        Ticket<?> ticket = getTicket(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Error: Ticket not found.");
        }
        
        Cashier cashier = findCashierById(cashierId);
        // E2 requirement: only the cashier who created the ticket can print it.
        if (cashier == null || !cashier.hasTicket(ticketId)) {
            throw new IllegalArgumentException("Error: Only the creating cashier can print this ticket.");
        }
        
        return ticket.print();
    }

    // Helper to find which client owns a specific ticket
    public String findClientIdByTicket(Ticket<?> ticket) {
        for (Client client : clientRepository.getAll()) {
            if (client.hasTicket(ticket.getId())) {
                return client.getId();
            }
        }
        return "Unknown";
    }

    // Helper to find which cashier owns a specific ticket
    public String findCashierIdByTicket(Ticket<?> ticket) {
        for (Cashier cashier : cashierRepository.getAll()) {
            if (cashier.hasTicket(ticket.getId())) {
                return cashier.getId();
            }
        }
        return "Unknown";
    }

    public Ticket<?> getTicket(String ticketId) {
        return ticketRepository.findById(ticketId);
    }

    @SuppressWarnings("Convert2Diamond")
    public List<Ticket<?>> getTickets() {
        return ticketRepository.getAll();
    }
    
    // Needed for CommandHandler to list tickets for a cashier
    public List<Ticket<?>> getTicketsByCashierId(String cashierId) {
        return ticketRepository.findAllByCashierId(cashierId);
    }

    public void refreshCounters() {
        int maxProdId = 0;
        for (es.upm.etsisi.poo.domain.product.Product p : getProducts()) {
            if (p.getId() > maxProdId) {
                maxProdId = p.getId();
            }
        }
        es.upm.etsisi.poo.domain.product.Product.updateNextId(maxProdId);
    }

    public Catalog getCatalog() {
        return catalog;
    }

    // Delegate methods to keep Store as the main interface for product management
    public List<Product> getProducts() {
        return catalog.getProducts();
    }

    public void updateProduct(int productId, String field, String updateValue) {
        catalog.updateProduct(productId, field, updateValue);
    }

    public Product removeProduct(int id) {
        return catalog.removeProduct(id);
    }

    public Product getProduct(int id) {
        return catalog.getProduct(id);
    }
    // End of delegate methods

    @SuppressWarnings("Convert2Diamond")
    public List<Client> getClients() {
        return clientRepository.getAll();
    }

    @SuppressWarnings("Convert2Diamond")
    public List<Cashier> getCashiers() {
        return cashierRepository.getAll();
    }
}