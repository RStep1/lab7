package host;

import commands.SaveCommand;
import data.CommandArguments;
import processing.RequestHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final String host;
    private final int port;
    private ServerSocket serverSocket;
    private final RequestHandler requestHandler;
    private static final CommandArguments SAVE_COMMAND = 
                new CommandArguments(SaveCommand.getName(), null, null,
                        null, null);

    public Server(RequestHandler requestHandler, String host, int port) {
        this.requestHandler = requestHandler;
        this.host = host;
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
        try {
            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("New client connected: " + client.getInetAddress());
            
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