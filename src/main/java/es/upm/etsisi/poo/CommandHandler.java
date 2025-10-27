//CLAU - Ya he terminado esta clase, y no me da errores
package es.upm.etsisi.poo;

import java.util.List;

public class CommandHandler {
    //Atributos
    private Catalog catalog;
    private Ticket ticket;

    //Constructor
    public CommandHandler(Catalog catalog, Ticket ticket) {
        this.catalog = catalog;
        this.ticket = ticket;
    }

    //Ayuda comandos
    public void handle(String entrada) {
        if (entrada == null || entrada.trim().isEmpty()) { return; }

        String[] partes = entrada.split(" ", 2); //La entrada se divide en dos partes (1° antes del primer espacio y lo demás la 2°)
        String comando = partes[0];

        switch (comando) {
            case "prod":
                handleProd(partes.length > 1 ? partes[1] : ""); //Devuelve partes[1] si es verdadero, y " " si es falso
                break;
            case "ticket":
                handleTicket(partes.length > 1 ? partes[1] : ""); //Devuelve partes[1] si es verdadero, y " " si es falso
                break;
            case "help":
                printHelp();
                break;
            case "echo":
                if (partes.length > 1) {
                    System.out.println(partes[0]+" "+partes[1]);
                }
                break;
            default:
                System.out.println("Comando no reconocido. Escribe 'help' para ver la lista.");
        }
    }

    /**
     * Comandos de ayuda de Producto
     * @param args
     */
    private void handleProd(String args) {

        String[] comandoProd = args.split(" ",2); //Separa el comando por sus espacios
        if (comandoProd.length == 0) { return; }

        switch (comandoProd[0]) {
            case "add":
                String[] comandosAdd = comandoProd[1].split("\"");
                if (comandosAdd.length >= 3) {
                    try {
                        int id = Integer.parseInt(comandosAdd[0].trim()); //Convierte a un int=id
                        String name = comandosAdd[1]; //String nombre sin las comillas
                        Category category = Category.valueOf(comandosAdd[2].split(" ")[1].toUpperCase()); //Le asigna una categoria
                        double price = Double.parseDouble(comandosAdd[2].split(" ")[2]); //Convierte a un double=precio

                        Product prod = new Product(id, name, category, price); //Crea el producto a partir del comando introducido
                        if (catalog.addProduct(prod)) {
                            System.out.println(prod);
                            System.out.println("prod add: ok");
                        }
                    } catch (Exception e) {
                        System.out.println("Error en prod add: " + e.getMessage());
                    }
                }
                break;
            case "list":
                List<Product> lista = catalog.listProducts();
                System.out.println("Catalog:");
                for (Product prod : lista) {
                    System.out.println("  " + prod);
                }
                System.out.println("prod list: ok");
                break;
            case "update":
                 comandosAdd = comandoProd[1].split(" ",3);
                if (comandosAdd.length >= 3) {
                    try {
                        int idProd = Integer.parseInt(comandosAdd[0]);
                        String campo = comandosAdd[1];
                        String actualizacion = comandosAdd[2].replace("\"", "");
                        if (catalog.updateProduct(idProd, campo, actualizacion)) {
                            System.out.println(catalog.getProduct(idProd));
                            System.out.println("prod update: ok");
                        }
                    } catch (Exception e) {
                        System.out.println("Error en prod update: " + e.getMessage());
                    }
                }
                break;
            case "remove":
                if (comandoProd.length >= 2) {
                    try {
                        int id = Integer.parseInt(comandoProd[1]);
                        Product eliminado = catalog.removeProduct(id);
                        if (eliminado != null) {
                            System.out.println(eliminado);
                            System.out.println("prod remove: ok");
                        }
                    } catch (Exception e) {
                        System.out.println("Error en prod remove: " + e.getMessage());
                    }
                }
                break;
            default:
                System.out.println("Uso de prod: add | list | update | remove");
        }
    }

    /**
     * Comandos de ayuda de Ticket
     * @param args
     */
    private void handleTicket(String args) {
        String[] comandoTicket = args.split(" ");
        if (comandoTicket.length == 0) return;

        switch (comandoTicket[0]) {
            case "new":
                ticket.clear();
                System.out.println("ticket new: ok");
                break;
            case "add":
                if (comandoTicket.length >= 3) {
                    try {
                        int id = Integer.parseInt(comandoTicket[1]);
                        int quantity = Integer.parseInt(comandoTicket[2]);
                        Product prod = catalog.getProduct(id);
                        if (prod != null && ticket.addProduct(prod, quantity)) {
                            ticket.printTicket();
                            System.out.println("ticket add: ok");
                        }
                    } catch (Exception e) {
                        System.out.println("Error en ticket add: " + e.getMessage());
                    }
                }
                break;
            case "remove":
                if (comandoTicket.length >= 2) {
                    try {
                        int id = Integer.parseInt(comandoTicket[1]);
                        if (ticket.removeProduct(id)) {
                            System.out.println("ticket remove: ok");
                        }else
                            System.out.println("ticket remove: error, no existe producto con este id en el ticket.");
                    } catch (Exception e) {
                        System.out.println("Error en ticket remove: " + e.getMessage());
                    }
                }
                break;
            case "print":
                if (ticket.isEmpty()) {
                    System.out.println("El ticket está vacío.");
                } else {
                    ticket.printTicket();
                    System.out.println("ticket print: ok");
                }
                break;
            default:
                System.out.println("Uso de ticket: new | add | remove | print");
        }
    }

    /**
     * Comandos de ayuda
     */
    private void printHelp() {
        System.out.println("Commands:");
        System.out.println("  prod add <id> \"<name>\" <category> <price>");
        System.out.println("  prod list");
        System.out.println("  prod update <id> NAME|CATEGORY|PRICE <value>");
        System.out.println("  prod remove <id>");
        System.out.println("  ticket new");
        System.out.println("  ticket add <prodId> <quantity>");
        System.out.println("  ticket remove <prodId>");
        System.out.println("  ticket print");
        System.out.println("  echo \"<texto>\"");
        System.out.println("  help");
        System.out.println("  exit");
        System.out.println();
        System.out.println("Categories: MERCH, STATIONERY, CLOTHES, BOOK, ELECTRONICS");
        System.out.println("Discounts if there are ≥2 units in the category: MERCH 0%, STATIONERY 5%, CLOTHES 7%, BOOK 10%, ELECTRONICS 3%");
    }
}