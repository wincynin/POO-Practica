package es.upm.etsisi.poo.ui;

// Represents a command to be executed.
public interface Command {
    void execute(String[] args) throws IllegalArgumentException;
}