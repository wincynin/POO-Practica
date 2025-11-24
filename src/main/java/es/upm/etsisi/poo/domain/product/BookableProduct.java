package es.upm.etsisi.poo.domain.product;

import java.time.LocalDateTime;

public abstract class BookableProduct extends Product {

    public BookableProduct(String name, double price) {
        super(name, null, price);
    }

    public abstract LocalDateTime getExpirationDate();

    @Override
    public boolean isBookable() {
        return true;
    }
}
