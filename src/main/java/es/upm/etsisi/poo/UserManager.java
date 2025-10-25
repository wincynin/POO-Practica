package es.upm.etsisi.poo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set; // For sorting lists later

/**
 * Manages clients and cashiers in the system.
 */
public class UserManager {

    private Map<String, Client> clientsByDni; // Key: DNI (String), Value: Client object
    private Map<String, Cashier> cashiersById; // Key: Cashier ID (String), Value: Cashier object
    private Set<String> usedCashierIds; // To ensure cashier ID uniqueness

    /**
     * Constructor for UserManager. Initializes the collections.
     */
    public UserManager() {
        this.clientsByDni = new HashMap<>();
        this.cashiersById = new HashMap<>();
        this.usedCashierIds = new HashSet<>();
    }

    /**
     * Adds a new client to the system.
     * 
     * @param name      Client's name.
     * @param dni       Client's DNI (must be unique).
     * @param email     Client's email.
     * @param cashierId ID of the registering cashier (must exist).
     * @return true if the client was added successfully, false otherwise.
     */
    public boolean addClient(String name, String dni, String email, String cashierId) {
        if (clientsByDni.containsKey(dni)) {
            System.out.println("Error: Client with DNI " + dni + " already exists.");
            return false;
        }
        if (!cashiersById.containsKey(cashierId)) {
            System.out.println("Error: Cashier with ID " + cashierId + " does not exist.");
            return false;
        }
        try {
            Client newClient = new Client(name, dni, email, cashierId);
            clientsByDni.put(dni, newClient);
            System.out.println(newClient); // Print client info on success
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println("Error adding client: " + e.getMessage());
            return false;
        }
    }

    /**
     * Removes a client from the system by DNI.
     * 
     * @param dni DNI of the client to remove.
     * @return The removed Client object, or null if not found.
     */
    public Client removeClient(String dni) {
        Client removedClient = clientsByDni.remove(dni);
        if (removedClient == null) {
            System.out.println("Error: Client with DNI " + dni + " not found.");
        } else {
            System.out.println(removedClient); // Print client info on success
        }
        return removedClient;
        // Note: Does not handle associated tickets yet.
    }

    /**
     * Gets a client by DNI.
     * 
     * @param dni DNI of the client to retrieve.
     * @return The Client object, or null if not found.
     */
    public Client getClient(String dni) {
        return clientsByDni.get(dni);
    }

    /**
     * Gets a list of all clients, sorted alphabetically by name.
     * 
     * @return A sorted list of all clients.
     */
    public List<Client> getAllClients() {
        List<Client> clientList = new ArrayList<>(clientsByDni.values());
        // Sort by name as required by "client list"
        clientList.sort(Comparator.comparing(Client::getName, String.CASE_INSENSITIVE_ORDER));
        return clientList;
    }

    /**
     * Adds a new cashier to the system.
     * 
     * @param id    Cashier's ID (UW + 7 digits) or null/empty to auto-generate.
     * @param name  Cashier's name.
     * @param email Cashier's company email.
     * @return true if the cashier was added successfully, false otherwise.
     */
    public boolean addCashier(String id, String name, String email) {
        // ID uniqueness and format validation is handled within the Cashier constructor
        try {
            Cashier newCashier = new Cashier(id, name, email, this.usedCashierIds);
            cashiersById.put(newCashier.getId(), newCashier);
            System.out.println(newCashier); // Print cashier info on success
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println("Error adding cashier: " + e.getMessage());
            return false;
        }
    }

    /**
     * Removes a cashier from the system by ID.
     * Note: This method currently only removes the cashier record.
     * The logic to remove associated tickets must be handled separately.
     * 
     * @param id ID of the cashier to remove.
     * @return The removed Cashier object, or null if not found.
     */
    public Cashier removeCashier(String id) {
        Cashier removedCashier = cashiersById.remove(id);
        if (removedCashier != null) {
            usedCashierIds.remove(id); // Free up the ID
            System.out.println(removedCashier); // Print cashier info on success
            // IMPORTANT: The caller needs to handle the deletion of tickets
            // associated with this cashier, using removedCashier.getCreatedTicketIds()
            // For now, we call a placeholder in Cashier:
            // removedCashier.removeAllTickets(); // Placeholder for now
        } else {
            System.out.println("Error: Cashier with ID " + id + " not found.");
        }
        return removedCashier;
    }

    /**
     * Gets a cashier by ID.
     * @param id ID of the cashier to retrieve.
     * @return The Cashier object, or null if not found.
     */
    public Cashier getCashier(String id) {
        return cashiersById.get(id);
    }

    /**
     * Gets a list of all cashiers, sorted alphabetically by name.
     * 
     * @return A sorted list of all cashiers.
     */
    public List<Cashier> getAllCashiers() {
        List<Cashier> cashierList = new ArrayList<>(cashiersById.values());
        // Sort by name as required by "cash list"
        cashierList.sort(Comparator.comparing(Cashier::getName, String.CASE_INSENSITIVE_ORDER));
        return cashierList;
    }

    // Helper method (optional, could be used by CommandHandler before adding)
    public boolean cashierExists(String id) {
        return cashiersById.containsKey(id);
    }

    // Helper method (optional)
    public boolean clientExists(String dni) {
        return clientsByDni.containsKey(dni);
    }

}