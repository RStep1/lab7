package commands;

import data.CommandArguments;
import data.Vehicle;
import processing.BufferedDataBase;

/**
 * Acts as a wrapper for the 'insert' command.
 * Calls the method containing the implementation of this command.
 */
public class LoginCommand implements Command {
    private BufferedDataBase bufferedDataBase;
    private static final String NAME = "login";
    private static final String ARGUMENTS = "<login> <password>";
    private static final String DESCRIPTION = "user authorization";
    private static final int COUNT_OF_ARGUMENTS = 0;
    private static final int COUNT_OF_EXTRA_ARGUMENTS = Vehicle.getCountOfChangeableFields();
    public LoginCommand(BufferedDataBase bufferedDataBase) {
        this.bufferedDataBase = bufferedDataBase;
    }

    @Override
    public boolean execute(CommandArguments commandArguments) {
        return bufferedDataBase.login(commandArguments);
    }

    public static String getName() {
        return NAME;
    }

    public static String getDescription() {
        return DESCRIPTION;
    }

    public static int getCountOfArguments() {
        return COUNT_OF_ARGUMENTS;
    }

    public static int getCountOfExtraArguments() {
        return COUNT_OF_EXTRA_ARGUMENTS;
    }

    @Override
    public String toString() {
        return NAME + ARGUMENTS + ": " + DESCRIPTION;
    }
}