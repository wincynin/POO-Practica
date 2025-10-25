/*CLASE PRODUCTS - TERMINADA
Product
   Atributos:
     id / name / category / price
   Métodos:
     Constructor + getters/setters
     toString() con el formato del enunciado
*/
package es.upm.etsisi.poo;

public class Product {
    private int id;
    private String name;
    private ProductCategory productCategory;
    private double price;

    /**
     * Constructor
     * @param id
     * @param name
     * @param productCategory
     * @param price
     */
    public Product(int id, String name, ProductCategory productCategory, double price) {
        if (id <= 0) {
            throw new IllegalArgumentException("Error: ID debe ser positivo.");
        }
        if (name == null || name.length() > 100) {
            throw new IllegalArgumentException("Error: El nombre no puede estar vacío ni superar los 100 caracteres.");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Error: el precio debe ser > 0 euros.");
        }
        this.id = id;
        this.name = name;
        this.productCategory = productCategory;
        this.price = price;
    }
    /**
     * Métodos getters
     */
    public int getId() { return id;}
    public  String getName() { return name; }
    public ProductCategory getCategory() { return productCategory; }
    public double getPrice() { return price; }
    /**
     * Métodos setters
     */
    public void setName(String name) {
        if (name == null || name.length() > 100) {
            throw new IllegalArgumentException("Error: El nombre no puede estar vacío ni superar los 100 caracteres.");
        } else {
            this.name = name;
        }
    }
    public void setCategory(ProductCategory productCategory) { this.productCategory = productCategory; }
    public void setPrice(double price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Error: el precio debe ser > 0 euros.");
        } else {
            this.price = price;
        }
    }

    @Override
    public String toString() {
        return String.format("{class: Product , id:%d, name:'%s', category:%s, price:%.1f}", id, name, productCategory, price);
    }
}
