package run;

import mods.MessageType;
import processing.BufferedDataBase;
import processing.Console;
import processing.RequestHandler;
import processing.CommandInvoker;

import java.util.Scanner;

import commands.*;
import host.Server;
import utility.DatabaseCollectionManager;
import utility.DatabaseHandler;
import utility.DatabaseUserManager;
import utility.FileHandler;
import utility.MessageHolder;
import utility.ScriptGenerator;

/**
 * The entry point to the program, declares and initializes all the necessary
 * classes.
 * Starts interactive mode for the user.
 */
public class Main {
    public static void main(String[] args) {
        // FileHandler.clearFile(FileType.TEST_SCRIPT);
        // ScriptGenerator scriptGenerator = new ScriptGenerator(50000);
        // scriptGenerator.generateInserts();
        String databaseUsername = "s368737";
        // String[] hostAndPort = args[0].split("\\s+");
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String password = args[2];

        if (!FileHandler.checkEnvVariable()) {
            Console.printUserErrors();
            MessageHolder.clearMessages(MessageType.USER_ERROR);
            return;
        }
        DatabaseHandler databaseHandler = new DatabaseHandler("jdbc:postgresql://" + host + ":5432/studs", databaseUsername, password);
        DatabaseUserManager databaseUserManager = new DatabaseUserManager(databaseHandler);
        DatabaseCollectionManager databaseCollectionManager = new DatabaseCollectionManager(databaseHandler);
        BufferedDataBase bufferedDataBase = new BufferedDataBase(databaseHandler, databaseUserManager, databaseCollectionManager);
        CommandInvoker invoker = new CommandInvoker(new HelpCommand(bufferedDataBase),
                new InfoCommand(bufferedDataBase), new ShowCommand(bufferedDataBase),
                new InsertCommand(bufferedDataBase), new UpdateCommand(bufferedDataBase),
                new RemoveKeyCommand(bufferedDataBase), new ClearCommand(bufferedDataBase),
                new SaveCommand(bufferedDataBase), new ExecuteScriptCommand(bufferedDataBase),
                new ExitCommand(bufferedDataBase), new RemoveGreaterCommand(bufferedDataBase),
                new RemoveLowerCommand(bufferedDataBase),
                new RemoveGreaterKeyCommand(bufferedDataBase),
                new RemoveAllByEnginePowerCommand(bufferedDataBase),
                new CountByFuelTypeCommand(bufferedDataBase),
                new FilterLessThanFuelTypeCommand(bufferedDataBase),
                new RegisterCommand(bufferedDataBase),
                new LoginCommand(bufferedDataBase));
        RequestHandler requestHandler = new RequestHandler(invoker);
        Server server = new Server(invoker, port);
        bufferedDataBase.setCommandInvoker(invoker);
        Console.println("Server is running...");

        Thread mainProggrammThread = new Thread(server::run);
        mainProggrammThread.start();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String nextLine = scanner.nextLine().trim();
            if (nextLine.equals(SaveCommand.getName())) {
                requestHandler.processRequest(Server.getSaveCommand());
            }
            if (!mainProggrammThread.isAlive()) {
                break;
            }
        }
        scanner.close();
    }
}
