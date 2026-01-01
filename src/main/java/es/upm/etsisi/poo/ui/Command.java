package es.upm.etsisi.poo.ui;

import es.upm.etsisi.poo.domain.exceptions.UPMStoreDomainException;

// [Interface] For executing commands.
public interface Command {
    void execute(String[] args) throws IllegalArgumentException, UPMStoreDomainException;
}