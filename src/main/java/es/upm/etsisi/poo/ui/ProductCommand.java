package es.upm.etsisi.poo.ui;

import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import es.upm.etsisi.poo.domain.product.*;
import es.upm.etsisi.poo.application.Store;
import es.upm.etsisi.poo.domain.exceptions.UPMStoreDomainException;
import es.upm.etsisi.poo.domain.exceptions.InvalidProductDataException;

// [Command] Product CRUD operations.
class ProductCommand extends AbstractCommand {

    public ProductCommand(Store store) {
        super(store);
    }

    @Override
    public void execute(String[] args) throws IllegalArgumentException, UPMStoreDomainException {
        if (args.length == 0) {
            throw new IllegalArgumentException("Usage: prod add | addFood | addMeeting | list | update | remove");
        }

        List<String> argList = parseArgs(args[0]);
        if (argList.isEmpty()) {
            return;
        }
        String command = argList.get(0);
        argList.remove(0);

        switch (command) {
            case "add":
                // E3: prod add <expiration: yyyy-MM-dd> <category>
                if (argList.size() == 2) {
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        LocalDate expirationDate = LocalDate.parse(argList.get(0), formatter);
                        ServiceType serviceType = ServiceType.valueOf(argList.get(1).toUpperCase());
                        Service service = new Service(expirationDate.atStartOfDay(), serviceType);
                        store.addProduct(service);
                        System.out.println(service);
                        System.out.println("prod add: ok");
                        break;
                    } catch (InvalidProductDataException e) {
                        // Not a service, fall through to standard product creation
                    }
                }

                String id = null;
                // Check if first arg is an ID (numeric or ends with 'S')
                if (isId(argList.get(0))) {
                    id = argList.get(0);
                    argList.remove(0);
                }

                String name;
                double price;
                int maxPers = -1;           // -1 means not customizable
                ProductCategory category;

                name = argList.get(0);
                category = ProductCategory.valueOf(argList.get(1).toUpperCase());
                price = Double.parseDouble(argList.get(2));
                
                // E2: Check for optional customizable parameter
                if (argList.size() > 3) {
                    maxPers = Integer.parseInt(argList.get(3));
                }

                Product prod;
                if (id != null) {
                    if (maxPers != -1) {
                        prod = new CustomizableProduct(id, name, category, price, maxPers);
                    } else {
                        prod = new StandardProduct(id, name, category, price);
                    }
                } else {
                    if (maxPers != -1) {
                        prod = new CustomizableProduct(name, category, price, maxPers);
                    } else {
                        prod = new StandardProduct(name, category, price);
                    }
                }

                store.addProduct(prod);
                System.out.println(prod);
                System.out.println("prod add: ok");
                break;
            case "addFood":
            case "addMeeting":
                String eventId = null;
                if (isId(argList.get(0))) {
                    eventId = argList.get(0);
                    argList.remove(0);
                }

                int maxPeople;
                String eventName;
                double eventPrice;
                LocalDate expirationDate;

                // Rule: Date format yyyy-MM-dd (E2)
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                eventName = argList.get(0);
                eventPrice = Double.parseDouble(argList.get(1));
                expirationDate = LocalDate.parse(argList.get(2), formatter);
                maxPeople = Integer.parseInt(argList.get(3));

                EventType eventType = command.equals("addFood") ? EventType.FOOD : EventType.MEETING;
                
                EventProduct eventProduct;
                if (eventId != null) {
                    eventProduct = new EventProduct(eventId, eventName, eventPrice, expirationDate.atStartOfDay(), maxPeople, eventType);
                } else {
                    eventProduct = new EventProduct(eventName, eventPrice, expirationDate.atStartOfDay(), maxPeople, eventType);
                }

                eventProduct.validate();
                
                store.addProduct(eventProduct);
                System.out.println(eventProduct);
                System.out.println("prod " + command + ": ok");
                break;
            case "list":
                List<Product> productList = store.getProducts();
                System.out.println("Catalog:");
                for (Product p : productList) {
                    System.out.println("  " + p);
                }
                System.out.println("prod list: ok");
                break;
            case "update":
                if (argList.size() >= 3) {
                    String productId = argList.get(0);
                    String field = argList.get(1);
                    String updateValue = argList.get(2);
                    store.updateProduct(productId, field, updateValue);
                    System.out.println(store.getCatalog().getProduct(productId));
                    System.out.println("prod update: ok");
                }
                break;
            case "remove":
                if (!argList.isEmpty()) {
                    String removeId = argList.get(0);
                    Product removedProduct = store.removeProduct(removeId);
                    System.out.println(removedProduct);
                    System.out.println("prod remove: ok");
                }
                break;
            default:
                System.out.println("Usage: prod add | addFood | addMeeting | list | update | remove");
        }
    }

    private boolean isId(String s) {
        try {
            Integer.valueOf(s);
            return true;
        } catch (NumberFormatException e) {
            if (s.endsWith("S") && s.length() > 1) {
                String prefix = s.substring(0, s.length() - 1);
                for (int i = 0; i < prefix.length(); i++) {
                    if (!Character.isDigit(prefix.charAt(i))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
}