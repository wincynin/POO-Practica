//CLASE TICKET
package es.upm.etsisi.poo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Ticket {
    private static final int MAX_ITEMS = 100;
    private List<Product> items;

    public Ticket() {
        this.items = new ArrayList<Product>();
    }

    /**
     * Añade un producto al ticket N veces (cantidad).
     */
    public boolean addProduct(Product prod, int quantity) {
        if (items.size() + quantity > MAX_ITEMS) {
            System.out.println("Error: Se alcanzó el máximo de productos en el ticket.");
            return false;
        }
        for (int i = 0; i < quantity; i++) {
            items.add(prod);
        }
        return true;
    }

    /**
     * Elimina todas las apariciones de un producto por ID.
     */
    public boolean removeProduct(int id) {
        boolean removed = false;
        List<Product> toRemove = new ArrayList<Product>();
        for (Product prod : items) {
            if (prod.getId() == id) {
                toRemove.add(prod);
                removed = true;
            }
        }
        items.removeAll(toRemove);
        return removed;
    }

    /**
     * Reinicia el ticket actual.
     */
    public void clear() {
        items.clear();
    }

    /**
     * Imprime el ticket con precios, descuentos y totales.
     */
    public void printTicket() {
        double totalPrice;
        totalPrice = totalPrice();
        double totalDiscount = totalDiscount();
        double finalPrice = totalPrice - totalDiscount;

        // Ordenamos por nombre alfabéticamente
        Collections.sort(items, new Comparator<Product>() {
            @Override
            public int compare(Product p1, Product p2) {
                return p1.getName().compareToIgnoreCase(p2.getName());
            }
        });

        for (Product prod : items) {
            double discount = getDiscountForProduct(prod);
            if (discount > 0) {
                System.out.printf("%s **discount -%.1f%n", prod.toString(), discount);
            } else {
                System.out.println(prod.toString());
            }
        }

        System.out.printf("Total price: %.1f%n", totalPrice);
        System.out.printf("Total discount: %.1f%n", totalDiscount);
        System.out.printf("Final Price: %.1f%n", finalPrice);
    }

    /**
     * Calcula el precio total de los productos.
     */
    public double totalPrice() {
        double suma = 0.0;
        for (Product prod : items) {
            suma += prod.getPrice();
        }
        return suma;
    }

    /**
     * Calcula el descuento total aplicado al ticket.
     */
    public double totalDiscount() {
        double suma = 0.0;
        for (Product prod : items) {
            suma += getDiscountForProduct(prod);
        }
        return suma;
    }

    /**
     * Devuelve el descuento aplicable a un producto.
     * Si hay 2 o más productos de la misma categoría, se aplica el descuento de esa categoría.
     */
    public double getDiscountForProduct(Product p) {
        int count = 0;
        for (Product prod : items) {
            if (prod.getCategory() == p.getCategory()) {
                count++;
            }
        }
        if (count >= 2) {
            return p.getPrice() * p.getCategory().getDiscount();
        }
        return 0.0;
    }

    /**
     * Indica si el ticket está vacío.
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }
}