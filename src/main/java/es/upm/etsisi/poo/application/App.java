package es.upm.etsisi.poo.application;

import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;


import es.upm.etsisi.poo.infrastructure.persistence.FilePersistenceHandler;
import es.upm.etsisi.poo.ui.CommandHandler;

// Main application class for the ticket module.
public class App {
    public static void main(String[] args) throws FileNotFoundException {
        FilePersistenceHandler persistence = new FilePersistenceHandler();
        Store store = persistence.load(); // Load state
        store.refreshCounters(); // Refresh static counters

        Scanner inputScanner;

        // E1 Requirement: Check if a file path is provided as an argument.
        if (args.length > 0) {
            inputScanner = new Scanner(new File(args[0]));
        } else {
            inputScanner = new Scanner(System.in);
        }

        System.out.println("Welcome to the ticket module App.");
        System.out.println("Ticket module. Type 'help' to see commands.");

        // Create the command handler.
        CommandHandler handler = new CommandHandler(store);

        boolean running = true;
        while (running) {
            String inputLine;
            if (args.length > 0) {
                // Reading from file
                if (inputScanner.hasNextLine()) {
                    inputLine = inputScanner.nextLine().trim();
                    System.out.println("tUPM> " + inputLine);
                } else {
                    // End of file reached
                    running = false;
                    continue;
                }
            } else {
                // Interactive mode
                System.out.print("tUPM> ");
                inputLine = inputScanner.nextLine().trim();
            }

            if (inputLine.equalsIgnoreCase("exit")) {
                // Input of "exit" triggers application closure.
                System.out.println("Closing application.");
                persistence.save(store); // Save state
                System.out.println("Goodbye!");
                running = false;
            } else if (!inputLine.isEmpty()) {
                // Pass the raw command string to the handler to be processed
                handler.handle(inputLine);
            }
        }
        inputScanner.close();
    }
}