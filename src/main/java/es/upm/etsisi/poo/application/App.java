package es.upm.etsisi.poo.application;

import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;


import es.upm.etsisi.poo.ui.CommandHandler;
import es.upm.etsisi.poo.infrastructure.persistence.FilePersistenceHandler;
import es.upm.etsisi.poo.domain.exceptions.PersistenceException;

// Main application class for the ticket module.
public class App {
    public static void main(String[] args) throws FileNotFoundException {
        FilePersistenceHandler persistence = new FilePersistenceHandler();
        Store store;
        try {
            store = persistence.load(); // Load state
            System.out.println("Data loaded successfully.");
        } catch (PersistenceException e) {
            System.out.println("Warning: Could not load data (" + e.getMessage() + "). Starting with empty store.");
            store = new Store(); // Fallback
        }
        store.refreshCounters(); // Refresh static counters

        Scanner inputScanner;

        // Check if we need to read from a file.
        if (args.length > 0) {
            inputScanner = new Scanner(new File(args[0]));
        } else {
            inputScanner = new Scanner(System.in);
        }

        System.out.println("Welcome to the ticket module App.");
        System.out.println("Ticket module. Type 'help' to see commands.");

        // Create the main controller.
        CommandHandler handler = new CommandHandler(store);

        boolean running = true;
        while (running) {
            String inputLine;
            if (args.length > 0) {
                // Logic: Read commands from file.
                if (inputScanner.hasNextLine()) {
                    inputLine = inputScanner.nextLine().trim();
                    System.out.println("tUPM> " + inputLine);
                } else {
                    running = false;
                    continue;
                }
            } else {
                // Logic: Read commands from keyboard.
                System.out.print("tUPM> ");
                inputLine = inputScanner.nextLine().trim();
            }

            if (inputLine.equalsIgnoreCase("exit")) {
                // Exit the loop when user types 'exit'.
                System.out.println("Closing application.");
                try {
                    persistence.save(store); // Save state
                    System.out.println("Data saved successfully.");
                } catch (PersistenceException e) {
                    System.err.println("CRITICAL: Failed to save data: " + e.getMessage());
                }
                System.out.println("Goodbye!");
                running = false;
            } else if (!inputLine.isEmpty()) {
                handler.handle(inputLine);
            }
        }
        inputScanner.close();
    }
}