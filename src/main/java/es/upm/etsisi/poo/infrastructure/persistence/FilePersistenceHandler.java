package es.upm.etsisi.poo.infrastructure.persistence;

import java.io.*;
import es.upm.etsisi.poo.application.Store;

// [Class] Saves/Loads data to a file.
public class FilePersistenceHandler {
    private static final String FILE_NAME = "store_data.dat";

    public void save(Store store) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(store);
            System.out.println("System state saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving state: " + e.getMessage());
        }
    }

    public Store load() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            // If file doesn't exist, create empty Store.
            return new Store(); 
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Store) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading state (starting fresh): " + e.getMessage());
            return new Store();
        }
    }
}