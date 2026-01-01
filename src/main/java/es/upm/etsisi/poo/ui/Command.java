package es.upm.etsisi.poo.ui;

// [Interface] For executing commands.
public interface Command {
    void execute(String[] args) throws IllegalArgumentException;
}