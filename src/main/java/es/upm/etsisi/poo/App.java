package es.upm.etsisi.poo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws FileNotFoundException {
        if (args.length > 0) {
            Scanner input = new Scanner(new File("input.txt"));
        } else {
            Scanner input = new Scanner(System.in);
        }
        System.out.println("Welcome to the ticket module App.");
        System.out.println("Ticket module. Type 'help' to see commands.");

        Store store = new Store();
        CommandHandler handler = new CommandHandler(store);

        Scanner teclado = new Scanner(System.in);
        boolean running = true;

        while (running) {

            System.out.print("tUPM> ");
            String input = teclado.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Closing application.");
                System.out.println("Goodbye!");
                running = false;
            } else {
                handler.handle(input);
            }
        }

        teclado.close();
    }
}
