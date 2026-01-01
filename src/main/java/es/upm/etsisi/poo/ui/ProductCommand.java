package es.upm.etsisi.poo.ui;

import es.upm.etsisi.poo.application.Store;
import es.upm.etsisi.poo.domain.product.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

class ProductCommand extends AbstractCommand {

    public ProductCommand(Store store) {
        super(store);
    }

    @Override
    public void execute(String[] args) throws IllegalArgumentException {
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
                    } catch (Exception e) {
                        // Not a service, fall through to standard product creation
                    }
                }

                Integer id = null;
                try {
                    id = Integer.valueOf(argList.get(0));
                    argList.remove(0);
                } catch (NumberFormatException e) {
                    // It's a name, not an ID.
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
                Integer eventId = null;
                try {
                    // Try to parse first arg as ID
                    eventId = Integer.valueOf(argList.get(0));
                    // If successful, it's an ID, so remove it from list
                    argList.remove(0);
                } catch (NumberFormatException e) {
                    // It's a name, not an ID.
                }

                int maxPeople;
                String eventName;
                double eventPrice;
                LocalDate expirationDate;

                // E2 Requirement: Date format yyyy-MM-dd
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
                    int productId = Integer.parseInt(argList.get(0));
                    String field = argList.get(1);
                    String updateValue = argList.get(2);
                    store.updateProduct(productId, field, updateValue);
                    System.out.println(store.getCatalog().getProduct(productId)); // Keeping this one for now as there's no `store.getProduct(id)`
                    System.out.println("prod update: ok");
                }
                break;
            case "remove":
                if (!argList.isEmpty()) {
                    int removeId = Integer.parseInt(argList.get(0));
                    Product removedProduct = store.removeProduct(removeId);
                    System.out.println(removedProduct);
                    System.out.println("prod remove: ok");
                }
                break;
            default:
                System.out.println("Usage: prod add | addFood | addMeeting | list | update | remove");
        }
    }
}
