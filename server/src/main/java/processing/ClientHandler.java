package processing;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ForkJoinPool;

import data.CommandArguments;
import utility.ServerAnswer;

public class ClientHandler implements Runnable {
    private Socket client;
    // private RequestHandler requestHandler;
    private CommandInvoker invoker;

    private ForkJoinPool executingForkJoinPool = ForkJoinPool.commonPool();


    public ClientHandler(Socket client, CommandInvoker invoker) {
        this.client = client;
        // this.requestHandler = requestHandler;
        this.invoker = invoker;
    }

    @Override
    public void run() {
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new DataInputStream(client.getInputStream()));
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new DataOutputStream(client.getOutputStream()));
            ) {
            ServerAnswer serverAnswer = null;
            CommandArguments commandArguments = null;
            RequestHandler requestHandler = new RequestHandler(invoker);
            while (true) {
                commandArguments = (CommandArguments) TCPExchanger.read(bufferedInputStream);
                // serverAnswer = executingForkJoinPool.invoke(new ServerExecutor(requestHandler));
                serverAnswer = requestHandler.processRequest(commandArguments);
                // ServerSender serverSender = new ServerSender(serverAnswer, bufferedOutputStream);
                // Thread writingThread = new Thread(serverSender);
                // writingThread.start();
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

