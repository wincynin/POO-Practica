package es.upm.etsisi.poo.infrastructure.persistence;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import es.upm.etsisi.poo.application.Store;
import es.upm.etsisi.poo.domain.exceptions.PersistenceException;

// [Class] Saves/Loads data to a file.
public class FilePersistenceHandler {
    private static final String FILE_NAME = "store_data.dat";

    public void save(Store store) throws PersistenceException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(store);
            System.out.println("System state saved successfully.");
        } catch (IOException e) {
            throw new PersistenceException("Failed to save data: " + e.getMessage(), e);
        }
    }

    public Store load() throws PersistenceException {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            // If file doesn't exist, create empty Store.
            return new Store();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Store) ois.readObject();
        } catch (IOException e) {
            throw new PersistenceException("Error loading state: " + e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            throw new PersistenceException("Error loading state (class not found): " + e.getMessage(), e);
        }
    }
}