/*CLASE CATALOG -
Catalog: gestiona productos disponibles en el sistema.
  Atributos:
    Map<Integer, Product> products (máx. 200).
  Métodos:
    boolean addProduct(Product p)
    boolean removeProduct(int id)
    Product getProduct(int id)
    List<Product> listProducts()
    boolean updateProduct(int id, String field, String value)
 */
package es.upm.etsisi.poo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Clase que gestiona el catálogo de productos disponibles.
 */
public class Catalog {
    private static final int MAX_PRODUCTS = 200;
    private Product[] products;
    private int amount; //Cantidad de productos


    public Catalog() {
            this.products = new Product[MAX_PRODUCTS];
            this.amount = 0;
    }

    /**
     * Agrega un nuevo producto al catalogo si no existe otro igual (!= id)
     */
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

    /**
     * Elimina un producto por ID.
     * @return el producto eliminado o null si no existe
     */
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

    /**
     * Busca un producto por ID
     */
    public Product getProduct(int id) {
        for(int i = 0; i < amount; i++) {
            if (products[i].getId() == id) {
                return products[i];
            }
        }
        return null;
    }

    /**
     * Devuelve la lista de productos ordenada por ID
     */
    public List<Product> listProducts() {
        List<Product> list = new ArrayList<Product>();
        for (int i = 0; i < amount; i++) {
            list.add(products[i]);
        }
        //Ordenar por id
        Collections.sort(list, new Comparator<Product>() {
            @Override
            public int compare(Product prod1, Product prod2) {
                return Integer.compare(prod1.getId(), prod2.getId());
            }
        });
        return list;
    }

    /**
     * Actualiza un campo específico del producto (NAME, CATEGORY o PRICE).
     */
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
                    Category newCategory = Category.valueOf(value.toUpperCase());
                    prod.setCategory(newCategory);
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

    /**
     * @return Indica si el catálogo está vacío
     */
    public boolean isEmpty() {
        return amount == 0;
    }

    /**
     * @return Devuelve el número actual de productos en el catálogo
     */
    public int getSize() {
        return amount;
    }
}

