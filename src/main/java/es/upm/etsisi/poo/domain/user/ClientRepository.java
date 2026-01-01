package es.upm.etsisi.poo.domain.user;

import es.upm.etsisi.poo.domain.exceptions.DuplicateEntryException;

import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

// [Class] Manages the list of Clients.
public class ClientRepository implements Serializable {
    private final List<Client> clients;

    public ClientRepository() {
        this.clients = new ArrayList<>();
    }

    public void add(Client client) throws DuplicateEntryException {
        if (findById(client.getId()) != null) {
            throw new DuplicateEntryException("Client with ID " + client.getId() + " already exists.");
        }
        clients.add(client);
    }

    public Client findById(String id) {
        for (Client client : clients) {
            if (client.getId().equals(id)) {
                return client;
            }
        }
        return null;
    }

    public void remove(String id) {
        Client clientToRemove = findById(id);
        if (clientToRemove != null) {
            clients.remove(clientToRemove);
        }
    }

    public List<Client> getAll() {
        return new ArrayList<>(clients);
    }
}