package es.upm.etsisi.poo.domain.product;

import java.util.List;
import java.util.Collections;
import java.time.LocalDateTime;

// Represents a service in the system as defined by E3.
public class Service extends Product {
    private final ServiceType serviceType;
    private final LocalDateTime expirationDate;

    public Service(LocalDateTime expirationDate, ServiceType serviceType) {
        super(String.format("Service-%s", serviceType.name()), null, 0.0); // Price is 0.0
        this.expirationDate = expirationDate;
        this.serviceType = serviceType;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    @Override
    public List<String> getCustomTexts() {
        return Collections.emptyList();
    }

    @Override
    public void addCustomText(List<String> customTexts, String text) {
        throw new UnsupportedOperationException("Services do not support custom texts.");
    }

    @Override
    public double getLineTotal(int quantity, List<String> customTexts) {
        return getPrice() * quantity;
    }

    @Override
    public boolean isBookable() {
        return false;
    }

    @Override
    public void validate() {
        // No specific validation for generic services.
    }
}