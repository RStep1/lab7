package data;

import mods.ClientRequestType;
import mods.ExecuteMode;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;

public class CommandArguments implements Serializable {
    private final String commandName;
    private final String[] arguments;
    private String[] extraArguments;
    private ClientRequestType clientRequestType;
    private final ExecuteMode executeMode;
    private File scriptFile;
    private User user;

    public CommandArguments(String commandName, String[] arguments, String[] extraArguments,
                            ClientRequestType clientRequestType, ExecuteMode executeMode, User user) {
        this.commandName = commandName;
        this.arguments = arguments;
        this.extraArguments = extraArguments;
        this.clientRequestType = clientRequestType;
        this.executeMode = executeMode;
        this.user = user;
    }

    public String getCommandName() {
        return commandName;
    }

    public String[] getArguments() {
        return arguments;
    }

    public String[] getExtraArguments() {
        return extraArguments;
    }

    public ClientRequestType getClientRequestType() {
        return clientRequestType;
    }

    public ExecuteMode getExecuteMode() {
        return executeMode;
    }

    public File getScriptFile() {
        return scriptFile;
    }

    public User getUser() {
        return user;
    }

    public void setClientRequestType(ClientRequestType clientRequestType) {
        this.clientRequestType = clientRequestType;
    }

    public void setScriptFile(File scriptFile) {
        this.scriptFile = scriptFile;
    }

    public void setExtraArguments(String[] extraArguments) {
        this.extraArguments = extraArguments;
    }

    @Override
    public String toString() {
        return String.format(
                """
                        Command name: %s
                        Argument: %s
                        Vehicle arguments: %s
                        Execute mode: %s
                        """,
                commandName, Arrays.toString(arguments),
                Arrays.toString(extraArguments), executeMode);
    }
}
