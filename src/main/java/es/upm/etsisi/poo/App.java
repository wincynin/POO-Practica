package es.upm.etsisi.poo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner inputSource = null;
        boolean fromFile = false;

        if (args.length > 0) {
            try {
                String filename = args[0];
                inputSource = new Scanner(new File(filename));
                System.out.println("Reading commands from file: " + filename);
                fromFile = true;
            } catch (FileNotFoundException e) {
                System.err.println("Error: Input file not found: " + args[0]);
                System.out.println("Switching to interactive mode.");
                inputSource = new Scanner(System.in);
                fromFile = false;
            }
        } else {
            inputSource = new Scanner(System.in);
            fromFile = false;
        }

        System.out.println("Welcome to the ticket module App.");
        System.out.println("Ticket module. Type 'help' to see commands.");

        Store store = new Store();
        UserManager userManager = new UserManager();
        TicketManager ticketManager = new TicketManager(); // <-- Instantiate TicketManager

        // Pass all managers to CommandHandler
        CommandHandler handler = new CommandHandler(store, userManager, ticketManager); // <-- Pass TicketManager

        boolean running = true;

        while (running) {
            String inputLine;
            if (fromFile) {
                if (inputSource.hasNextLine()) {
                    inputLine = inputSource.nextLine().trim();
                    // E2: Print command from file, only if it's not empty/whitespace
                    if (!inputLine.isEmpty()) {
                        System.out.println("Executing: " + inputLine);
                    }
                } else {
                    running = false;
                    continue;
                }
            } else {
                System.out.print("tUPM> ");
                if (inputSource.hasNextLine()) {
                    inputLine = inputSource.nextLine().trim();
                } else {
                    running = false;
                    continue;
                }
            }

            if (inputLine.equalsIgnoreCase("exit")) {
                System.out.println("Closing application.");
                System.out.println("Goodbye!");
                running = false;
            } else if (!inputLine.isEmpty()) {
                handler.handle(inputLine);
            }
        }

        inputSource.close();
    }
}
