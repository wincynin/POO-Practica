package es.upm.etsisi.poo.common;

import es.upm.etsisi.poo.domain.product.Catalog;
import es.upm.etsisi.poo.domain.user.Cashier;

import java.util.List;

public class IdGenerator {

    public static String generateCashierId(List<Cashier> existingCashiers) {
        String id;
        do {
            int randomDigits = (int) (Math.random() * 10000000);
            id = "UW" + String.format("%07d", randomDigits);
        } while (isCashierIdTaken(id, existingCashiers));
        return id;
    }

    private static boolean isCashierIdTaken(String id, List<Cashier> existingCashiers) {
        for (Cashier cashier : existingCashiers) {
            if (cashier.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public static int generateProductId(Catalog catalog) {
        int id;
        boolean idTaken;
        do {
            id = (int) (Math.random() * 100000) + 1;
            idTaken = false;
            try {
                catalog.getProduct(id);
                idTaken = true; // If getProduct doesn't throw, ID is taken
            } catch (ProductNotFoundException e) {
                // ID is not taken, so idTaken remains false
            }
        } while (idTaken);
        return id;
    }
}
