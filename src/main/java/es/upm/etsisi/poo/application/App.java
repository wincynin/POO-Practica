package es.upm.etsisi.poo.application;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import es.upm.etsisi.poo.domain.exceptions.PersistenceException;
import es.upm.etsisi.poo.infrastructure.persistence.FilePersistenceHandler;
import es.upm.etsisi.poo.ui.CommandHandler;

// [Main] Entry point for the application.
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

        // Logic: Check for input file argument.
        if (args.length > 0) {
            inputScanner = new Scanner(new File(args[0]));
        } else {
            inputScanner = new Scanner(System.in);
        }

        System.out.println("Welcome to the ticket module App.");
        System.out.println("Ticket module. Type 'help' to see commands.");

        // Init: Create the CommandHandler.
        CommandHandler handler = new CommandHandler(store);

        boolean running = true;
        while (running) {
            String inputLine;
            if (args.length > 0) {
                // Mode: Read from file.
                if (inputScanner.hasNextLine()) {
                    inputLine = inputScanner.nextLine().trim();
                    System.out.println("tUPM> " + inputLine);
                } else {
                    running = false;
                    continue;
                }
            } else {
                // Mode: Interactive.
                System.out.print("tUPM> ");
                inputLine = inputScanner.nextLine().trim();
            }

            if (inputLine.equalsIgnoreCase("exit")) {
                // Shutdown: Save and exit.
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