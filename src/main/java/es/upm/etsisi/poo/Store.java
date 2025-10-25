package es.upm.etsisi.poo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Store {
    private static final int MAX_PRODUCTS = 200;
    private Product[] products;
    private int amount;

    public Store() {
            this.products = new Product[MAX_PRODUCTS];
            this.amount = 0;
    }

    public boolean addProduct(Product prod) {
        if (amount >= MAX_PRODUCTS) {
            System.out.println("Error: Se alcanzó el máximo de productos permitidos.");
            return false;
        } else if (getProduct(prod.getId()) != null) {
            System.out.println("Error: Ya existe un producto con ese ID.");
            return false;
        }
        products[amount++] = prod;
        return true;
    }

    public Product removeProduct(int id) {
        for (int i = 0; i < amount; i++) {
            if (products[i].getId() == id) {
                Product removed = products[i];
                //Desplazamos el resto una posición
                for (int j = i; j < amount; j++) {
                    products[j] = products[j + 1];
                }
                products[--amount] = null;
                return removed;
            }
        }
        return null;
    }

    public Product getProduct(int id) {
        for(int i = 0; i < amount; i++) {
            if (products[i].getId() == id) {
                return products[i];
            }
        }
        return null;
    }

    public List<Product> listProducts() {
        List<Product> list = new ArrayList<Product>();
        for (int i = 0; i < amount; i++) {
            list.add(products[i]);
        }
        Collections.sort(list, new Comparator<Product>() {
            @Override
            public int compare(Product prod1, Product prod2) {
                return Integer.compare(prod1.getId(), prod2.getId());
            }
        });
        return list;
    }

    public boolean updateProduct(int id, String field, String value) {
        Product prod = getProduct(id);
        if (prod == null) {
            return false;
        }

        switch (field.toUpperCase()) {
            case "NAME":
                prod.setName(value);
                break;
            case "CATEGORY":
                try {
                    ProductCategory newCategory = ProductCategory.valueOf(value.toUpperCase());
                    prod.setCategory(newCategory);;
                } catch (IllegalArgumentException e) {
                    System.out.println("Error: Categoría inválida");
                    return false;
                }
                break;
            case "PRICE":
                try {
                    double newPrice = Double.parseDouble(value);
                    if (newPrice <= 0) {
                        System.out.println("Error: Precio debe ser > 0");
                        return false;
                    }
                    prod.setPrice(newPrice);
                } catch (NumberFormatException e) {
                    System.out.println("Error: Precio inválido");
                    return false;
                }
                break;
            default:
                System.out.println("Error: Campo inválido");
                return false;
        }
        return true;
    }

    public boolean isEmpty() {
        return amount == 0;
    }

    public int getSize() {
        return amount;
    }
}