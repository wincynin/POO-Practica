package es.upm.etsisi.poo.domain.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import es.upm.etsisi.poo.domain.exceptions.DuplicateEntryException;

public class ClientRepository implements Serializable {
    private final List<Client> clients = new ArrayList<>();

    public void add(Client client) {
        // Validation: Prevent duplicate IDs.
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

    public List<Client> getAll() {
        return new ArrayList<>(clients);
    }

    public void remove(String id) {
        clients.removeIf(c -> c.getId().equals(id));
    }
}