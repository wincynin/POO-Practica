package es.upm.etsisi.poo.domain.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// [Class] Manages the list of Cashiers.
public class CashierRepository implements Serializable {
    private final List<Cashier> cashiers;

    public CashierRepository() {
        this.cashiers = new ArrayList<>();
    }

    public void add(Cashier cashier) {
        if (findById(cashier.getId()) != null) {
            throw new IllegalArgumentException("Cashier with ID " + cashier.getId() + " already exists.");
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

    public void remove(String id) {
        Cashier cashierToRemove = findById(id);
        if (cashierToRemove != null) {
            cashiers.remove(cashierToRemove);
        }
    }

    public List<Cashier> getAll() {
        return new ArrayList<>(cashiers);
    }
}
