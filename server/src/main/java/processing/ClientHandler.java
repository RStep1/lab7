package processing;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import data.CommandArguments;
import utility.ServerAnswer;

public class ClientHandler implements Runnable {
    private Socket client;
    private RequestHandler requestHandler;

    public ClientHandler(Socket client, RequestHandler requestHandler) {
        this.client = client;
        this.requestHandler = requestHandler;
    }

    @Override
    public void run() {
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new DataInputStream(client.getInputStream()));
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new DataOutputStream(client.getOutputStream()));
            ) {
            ServerAnswer serverAnswer = null;
            CommandArguments commandArguments = null;
            while (true) {
                commandArguments = (CommandArguments) TCPExchanger.read(bufferedInputStream);
                serverAnswer = requestHandler.processRequest(commandArguments);
                TCPExchanger.write(bufferedOutputStream, serverAnswer);
                bufferedOutputStream.flush();
            }
        } catch (IOException e) {
            // e.printStackTrace();
            System.out.println("Client disconnection");
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Client " + client + " exit");
        }
    }
}

