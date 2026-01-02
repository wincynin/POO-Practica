package es.upm.etsisi.poo.domain.user;

import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

import es.upm.etsisi.poo.domain.exceptions.DuplicateEntryException;

public class CashierRepository implements Serializable {
    private final List<Cashier> cashiers = new ArrayList<>();

    public void add(Cashier cashier) {
        // Validation: Prevent duplicate IDs.
        if (findById(cashier.getId()) != null) {
            throw new DuplicateEntryException("Cashier with ID " + cashier.getId() + " already exists.");
        }
        cashiers.add(cashier);
    }

    public Cashier findById(String id) {
        for (Cashier cashier : cashiers) {
            if (cashier.getId().equals(id)) {
                return cashier;
            }
        }
        return null;
    }

    public List<Cashier> getAll() {
        return new ArrayList<>(cashiers);
    }

    public void remove(String id) {
        cashiers.removeIf(c -> c.getId().equals(id));
    }
}