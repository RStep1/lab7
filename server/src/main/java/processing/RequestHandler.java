package processing;

import commands.HelpCommand;
import commands.InsertCommand;
import commands.LoginCommand;
import commands.RegisterCommand;
import commands.UpdateCommand;
import data.CommandArguments;
import mods.AnswerType;
import mods.ExecuteMode;
import mods.MessageType;
import utility.MessageHolder;
import utility.ServerAnswer;

import java.util.ArrayList;

public class RequestHandler {
    private CommandInvoker invoker;

    public RequestHandler(CommandInvoker invoker) {
        this.invoker = invoker;
    }

    public ServerAnswer processRequest(CommandArguments commandArguments) {
        MessageHolder.clearMessages(MessageType.OUTPUT_INFO);
        MessageHolder.clearMessages(MessageType.USER_ERROR);
        System.out.println(commandArguments.getUser());
        if (commandArguments.getUser() == null && 
            !(commandArguments.getCommandName().equals(LoginCommand.getName()) || 
            commandArguments.getCommandName().equals(RegisterCommand.getName()) || 
            commandArguments.getCommandName().equals(HelpCommand.getName()))) {
                MessageHolder.putMessage("You need to be logged in to run commands", MessageType.USER_ERROR);
            return new ServerAnswer(MessageHolder.getOutputInfo(), MessageHolder.getUserErrors(),
                         false, AnswerType.EXECUTION_RESPONSE);
        }
        boolean exitStatus = invoker.execute(commandArguments);

        ArrayList<String> outputInfo = MessageHolder.getOutputInfo();
        ArrayList<String> userErrors = MessageHolder.getUserErrors();

        AnswerType answerType = AnswerType.EXECUTION_RESPONSE;

        //если это команды update или insert и при этом нет дополнительных аргументов и это не команда из скрипта,
        // то выставляем запрос на получение дополниетльных аргументов
        if (isChangingCommand(commandArguments) && isExtraArgsNull(commandArguments) && isCommandMode(commandArguments)) {
            answerType = AnswerType.DATA_REQUEST;
        }
        return new ServerAnswer(outputInfo, userErrors, exitStatus, answerType); 
    }

    public static boolean isChangingCommand(CommandArguments commandArguments) {
        return commandArguments.getCommandName().equals(UpdateCommand.getName()) ||
        commandArguments.getCommandName().equals(InsertCommand.getName());
    }

    private boolean isCommandMode(CommandArguments commandArguments) {
        return commandArguments.getExecuteMode() == ExecuteMode.COMMAND_MODE;
    }

    private boolean isExtraArgsNull(CommandArguments commandArguments) {
        return commandArguments.getExtraArguments() == null;
    }
}
