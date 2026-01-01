package es.upm.etsisi.poo.application;

import es.upm.etsisi.poo.domain.ticket.TicketPrintType;
import es.upm.etsisi.poo.domain.exceptions.*;
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


// [Model] Central class that manages all data.
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

    public void addClient(Client client) throws DuplicateEntryException {
        // Check: Client ID must be unique.
        clientRepository.add(client);
    }

    public Client findClientById(String id) {
        return clientRepository.findById(id);
    }

    public void removeClient(String id) {
        clientRepository.remove(id);
    }

    public void addCashier(Cashier cashier) throws DuplicateEntryException {
        // Check: Cashier ID must be unique.
        cashierRepository.add(cashier);
    }

    // Helper: Generate an ID if the user didn't provide one.
    public void addCashier(String id, String name, String email) {
        String cashierId = id;
        if (cashierId == null || cashierId.isEmpty()) {
            cashierId = Cashier.generateCashierId(this.cashierRepository.getAll());
        }
        try {
            addCashier(new Cashier(cashierId, name, email));
        } catch (DuplicateEntryException e) {
            throw new UPMStoreDomainException(e.getMessage());
        }
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

    public Ticket<?> createTicket(String id, String cashierId, String userId, TicketPrintType printType) throws UPMStoreDomainException {
        Cashier cashier = findCashierById(cashierId);
        if (cashier == null) {
            throw new ResourceNotFoundException("Cashier with ID " + cashierId + " not found.");
        }
        Client client = findClientById(userId);
        if (client == null) {
            throw new ResourceNotFoundException("Client with ID " + userId + " not found.");
        }
        
        Ticket<?> newTicket;
        if (client instanceof IndividualClient) {
            newTicket = new CommonTicket(id);
        } else if (client instanceof CompanyClient) {
            newTicket = new CompanyTicket(id);
            switch (printType) {
                case SERVICE:
                    newTicket.setPrintStrategy(new es.upm.etsisi.poo.infrastructure.printing.ServicePrintStrategy());
                    break;
                case COMPANY:
                    newTicket.setPrintStrategy(new es.upm.etsisi.poo.infrastructure.printing.CompanyPrintStrategy());
                    break;
                case STANDARD:
                default:
                    newTicket.setPrintStrategy(new es.upm.etsisi.poo.infrastructure.printing.StandardPrintStrategy());
                    break;
            }
        } else {
            throw new TicketTypeMismatchException("Error: Unknown client type.");
        }
        
        // Link the ticket to the cashier and client.
        ticketRepository.add(newTicket);
        cashier.addTicket(newTicket);
        client.addTicket(newTicket);

        return newTicket;
    }

    public void addProductToTicket(String ticketId, String cashierId, int prodId, int amount,
            List<String> customTexts) throws UPMStoreDomainException {
        Ticket<?> ticket = getTicket(ticketId);
        if (ticket == null) {
            throw new ResourceNotFoundException("Ticket with ID " + ticketId + " not found.");
        }

        Cashier cashier = findCashierById(cashierId);
        // Rule: Only the creator can modify the ticket.
        if (cashier == null || !cashier.hasTicket(ticketId)) {
            throw new UnauthorizedAccessException("Cashier " + cashierId + " does not own ticket " + ticketId);
        }
        
        Product product = catalog.getProduct(prodId);
        if (product == null) {
            throw new ResourceNotFoundException("Product with ID " + prodId + " not found.");
        }

        // Use the polymorphic accepts method to check compatibility.
        if (!ticket.accepts(product)) {
            throw new TicketTypeMismatchException("Error: Product type " + product.getClass().getSimpleName() + " not accepted by ticket type " + ticket.getClass().getSimpleName() + ".");
        }

        // Now that we know the product is accepted, we can safely cast the ticket and product.
        if (ticket instanceof CommonTicket) {
            @SuppressWarnings("unchecked")
            Ticket<StandardProduct> commonTicket = (Ticket<StandardProduct>) ticket;
            commonTicket.addProduct((StandardProduct) product, amount, customTexts);
        } else if (ticket instanceof CompanyTicket) {
            @SuppressWarnings("unchecked")
            Ticket<Product> companyTicket = (Ticket<Product>) ticket;
            companyTicket.addProduct(product, amount, customTexts);
        } else {
            throw new UPMStoreDomainException("Error: Unhandled ticket type during product addition.");
        }
    }

    public void removeProductFromTicket(String ticketId, String cashierId, int prodId) throws UPMStoreDomainException {
        Ticket<?> ticket = getTicket(ticketId);
        if (ticket == null) {
            throw new ResourceNotFoundException("Ticket with ID " + ticketId + " not found.");
        }

        Cashier cashier = findCashierById(cashierId);
        
        // Rule: Only the creator can remove items.
        if (cashier == null || !cashier.hasTicket(ticketId)) {
            throw new UnauthorizedAccessException("Cashier " + cashierId + " does not own ticket " + ticketId);
        }
        if (!ticket.removeProduct(prodId)) {
            throw new ResourceNotFoundException("Product with ID " + prodId + " not found in ticket " + ticketId);
        }
    }

    public String printTicket(String ticketId, String cashierId) throws UPMStoreDomainException {
        Ticket<?> ticket = getTicket(ticketId);
        if (ticket == null) {
            throw new ResourceNotFoundException("Ticket with ID " + ticketId + " not found.");
        }
        
        Cashier cashier = findCashierById(cashierId);
        // Rule: Only the creator can print.
        if (cashier == null || !cashier.hasTicket(ticketId)) {
            throw new UnauthorizedAccessException("Cashier " + cashierId + " does not own ticket " + ticketId);
        }
        
        return ticket.print();
    }

    // Helper: Find which client owns a ticket.
    public String findClientIdByTicket(Ticket<?> ticket) {
        for (Client client : clientRepository.getAll()) {
            if (client.hasTicket(ticket.getId())) {
                return client.getId();
            }
        }
        return "Unknown";
    }

    // Helper: Find which cashier owns a ticket.
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
    
    // Helper: Get all tickets for a specific cashier.
    public List<Ticket<?>> getTicketsByCashierId(String cashierId) {
        Cashier cashier = findCashierById(cashierId);
        if(cashier != null) {
            return cashier.getTickets();
        }
        return new ArrayList<>();
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

    // Catalog methods (Delegation).
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