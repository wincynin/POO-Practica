package es.upm.etsisi.poo.ui;

import es.upm.etsisi.poo.application.Store;
import es.upm.etsisi.poo.domain.user.Client;

import java.util.List;
import java.util.Collections;

// Handles client-related commands such as add, remove, and list.
class ClientCommand extends AbstractCommand {

    public ClientCommand(Store store) {
        super(store);
    }

    @Override
    @SuppressWarnings("Convert2Lambda")
    public void execute(String[] args) throws IllegalArgumentException {
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
                if(isDNI(id)) {
                    client = new es.upm.etsisi.poo.domain.user.IndividualClient(id, name, email, cashierId);
                } else if (isNIF(id)) {
                    client = new es.upm.etsisi.poo.domain.user.CompanyClient(id, name, email, cashierId);
                } else {
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

    private boolean isDNI(String id) {
        if (id == null || id.length() != 9) return false;
        for (int i = 0; i < 8; i++) {
            if (!Character.isDigit(id.charAt(i))) return false;
        }
        return Character.isLetter(id.charAt(8));
    }

    private boolean isNIF(String id) {
        if (id == null || id.length() != 9) return false;
        if (!Character.isLetter(id.charAt(0))) return false;
        for (int i = 1; i < 9; i++) {
            if (!Character.isDigit(id.charAt(i))) return false;
        }
        return true;
    }
}