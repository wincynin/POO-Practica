package es.upm.etsisi.poo.application;

import java.util.ArrayList;
import java.util.List;

import es.upm.etsisi.poo.domain.exceptions.DuplicateEntryException;
import es.upm.etsisi.poo.domain.exceptions.InvalidProductDataException;
import es.upm.etsisi.poo.domain.exceptions.ResourceNotFoundException;
import es.upm.etsisi.poo.domain.exceptions.TicketTypeMismatchException;
import es.upm.etsisi.poo.domain.exceptions.UPMStoreDomainException;
import es.upm.etsisi.poo.domain.exceptions.UnauthorizedAccessException;
import es.upm.etsisi.poo.domain.product.Catalog;
import es.upm.etsisi.poo.domain.product.Product;
import es.upm.etsisi.poo.domain.product.StandardProduct;
import es.upm.etsisi.poo.domain.ticket.CommonTicket;
import es.upm.etsisi.poo.domain.ticket.CompanyTicket;
import es.upm.etsisi.poo.domain.ticket.Ticket;
import es.upm.etsisi.poo.domain.ticket.TicketPrintType;
import es.upm.etsisi.poo.domain.ticket.TicketRepository;
import es.upm.etsisi.poo.domain.user.Cashier;
import es.upm.etsisi.poo.domain.user.CashierRepository;
import es.upm.etsisi.poo.domain.user.Client;
import es.upm.etsisi.poo.domain.user.ClientRepository;
import es.upm.etsisi.poo.domain.user.CompanyClient;
import es.upm.etsisi.poo.domain.user.IndividualClient;


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
        // Validation: Throws error if ID is duplicate.
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
    public void addCashier(String id, String name, String email) throws UPMStoreDomainException {
        String cashierId = id;
        if (cashierId == null || cashierId.isEmpty()) {
            cashierId = Cashier.generateCashierId(this.cashierRepository.getAll());
        }
        // Handler: Catch duplicate error and re-throw as domain error.
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
        // Validation: Ensure Cashier exists.
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
            // Error: Client type must match Ticket type.
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
        // Security: Verify that the cashier owns this ticket.
        if (cashier == null || !cashier.hasTicket(ticketId)) {
            throw new UnauthorizedAccessException("Cashier " + cashierId + " does not own ticket " + ticketId);
        }
        
        Product product = catalog.getProduct(prodId);
        if (product == null) {
            throw new ResourceNotFoundException("Product with ID " + prodId + " not found.");
        }

        // Check: Ensure product is compatible with ticket.
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
        
        // Security: Enforce ownership before removal.
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
        // Security: Enforce ownership before printing.
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

    public void updateProduct(int productId, String field, String updateValue) throws InvalidProductDataException {
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