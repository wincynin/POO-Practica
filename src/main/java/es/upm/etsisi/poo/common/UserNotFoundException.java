package es.upm.etsisi.poo.common;

// Custom exception to signal that a User (Client or Cashier) ID was not found.
// This is used by Store.findClientById/findCashierById and handled by CommandHandler.
public class UserNotFoundException extends Exception {
    public UserNotFoundException(String message) {
        super(message);
    }
}