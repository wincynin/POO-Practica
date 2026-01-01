package es.upm.etsisi.poo.ui;

import java.util.Collections;
import java.util.List;

import es.upm.etsisi.poo.application.Store;
import es.upm.etsisi.poo.domain.exceptions.UPMStoreDomainException;
import es.upm.etsisi.poo.domain.user.Client;
import es.upm.etsisi.poo.domain.user.CompanyClient;
import es.upm.etsisi.poo.domain.user.IndividualClient;

// [Command] Client CRUD operations.
class ClientCommand extends AbstractCommand {

    public ClientCommand(Store store) {
        super(store);
    }

    @Override
    @SuppressWarnings("Convert2Lambda")
    public void execute(String[] args) throws IllegalArgumentException, UPMStoreDomainException {
        if (args.length == 0) {
            throw new IllegalArgumentException("Usage: client add | remove | list");
        }
        List<String> argList = parseArgs(args[0]);
        if (argList.isEmpty()) {
            return;
        }
        String command = argList.get(0);
        argList.remove(0);

        switch (command) {
            case "add":
                String name = argList.get(0);
                String id = argList.get(1);
                String email = argList.get(2);
                String cashierId = argList.get(3);

                Client client;
                // Logic: Discriminator (DNI vs NIF).
                boolean isCompany = id.matches("^[A-Za-z].*");
                if (isCompany) {
                    client = new CompanyClient(id, name, email, cashierId);
                } else {
                    client = new IndividualClient(id, name, email, cashierId);
                }
                if (!client.validateId(id)) {
                    throw new IllegalArgumentException("Error: Invalid client ID format.");
                }
                store.addClient(client);
                System.out.println("client add: ok");

                break;
            case "remove":
                if (!argList.isEmpty()) {
                    String dniToRemove = argList.get(0);
                    store.removeClient(dniToRemove);
                    System.out.println("client remove: ok");
                } else {
                    throw new IllegalArgumentException("Usage: client remove <DNI>");
                }
                break;
            case "list":
                List<Client> clientList = store.getClients();
                
                // E2 Requirement: Sort by name
                Collections.sort(clientList);
                System.out.println("Clients:");
                for (Client c : clientList) {
                    System.out.println("  " + c);
                }
                System.out.println("client list: ok");
                break;
            default:
                System.out.println("Unknown client command.");
        }
    }
}