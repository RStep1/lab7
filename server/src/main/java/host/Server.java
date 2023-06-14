package host;

import commands.SaveCommand;
import data.CommandArguments;
import processing.ClientHandler;
import processing.CommandInvoker;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final int port;
    private ServerSocket serverSocket;
    private final CommandInvoker invoker;
    private static final CommandArguments SAVE_COMMAND = 
                new CommandArguments(SaveCommand.getName(), null, null,
                        null, null, null);

    public Server(CommandInvoker invoker, int port) {
        this.invoker = invoker;
        this.port = port;
    }
    
    private void setup() {
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        setup();
        System.out.println("setup completed");
        try {
            while (true) {
                System.out.println("accept()");
                Socket client = serverSocket.accept();
                System.out.println("New client connected: " + client.getInetAddress());
                
                ClientHandler clientHandler = new ClientHandler(client, invoker);
                Thread clientHandlerThread = new Thread(clientHandler);
                clientHandlerThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static CommandArguments getSaveCommand() {
        return SAVE_COMMAND;
    }
}