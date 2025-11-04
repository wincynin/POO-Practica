package es.upm.etsisi.poo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner inputScanner;
        if (args.length > 0) {
            inputScanner = new Scanner(new File(args[0]));
        } else {
            inputScanner = new Scanner(System.in);
        }
        System.out.println("Welcome to the ticket module App.");
        System.out.println("Ticket module. Type 'help' to see commands.");

        Store store = new Store();
        CommandHandler handler = new CommandHandler(store);

        boolean running = true;

        while (running) {
            String inputLine;
            if (args.length > 0) { // Reading from file
                if (inputScanner.hasNextLine()) {
                    inputLine = inputScanner.nextLine().trim();
                    System.out.println("tUPM> " + inputLine); // Print command from file
                } else {
                    running = false; // End of file
                    continue;
                }
            } else { // Interactive mode
                System.out.print("tUPM> ");
                inputLine = inputScanner.nextLine().trim();
            }

            if (inputLine.equalsIgnoreCase("exit")) {
                System.out.println("Closing application.");
                System.out.println("Goodbye!");
                running = false;
            } else if (!inputLine.isEmpty()) {
                handler.handle(inputLine);
            }
        }

        inputScanner.close();
    }
}
