package es.upm.etsisi.poo.ui;

/**
 * Represents a command to be executed.
 */
public interface Command {
    /**
     * Executes the command with the given arguments.
     *
     * @param args The arguments for the command.
     * @throws IllegalArgumentException if the arguments are invalid.
     */
    void execute(String[] args) throws IllegalArgumentException;
}
