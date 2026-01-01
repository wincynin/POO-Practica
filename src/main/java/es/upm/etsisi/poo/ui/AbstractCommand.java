package es.upm.etsisi.poo.ui;

import java.util.List;
import java.util.ArrayList;
import es.upm.etsisi.poo.application.Store;

// Abstract base class for commands, providing common functionality.
abstract class AbstractCommand implements Command {
    protected final Store store;

    public AbstractCommand(Store store) {
        this.store = store;
    }

    protected List<String> parseArgs(String args) {
        @SuppressWarnings("Convert2Diamond")
        List<String> argList = new ArrayList<String>();
        boolean inQuotes = false;
        StringBuilder currentArg = new StringBuilder();

        for (char c : args.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ' ' && !inQuotes) {
                if (currentArg.length() > 0) {
                    argList.add(currentArg.toString());
                    currentArg = new StringBuilder();
                }
            } else {
                currentArg.append(c);
            }
        }
        if (currentArg.length() > 0) {
            argList.add(currentArg.toString());
        }
        return argList;
    }
}