package user;

import processing.TCPExchanger;
import utility.ServerAnswer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import data.CommandArguments;

public class Client {
    private static Socket clientSocket;
    private static BufferedInputStream bufferedInputStream;
    private static BufferedOutputStream bufferedOutputStream;

    public Client(String host, int port) throws IOException {
        clientSocket = new Socket(host, port);
        bufferedInputStream = new BufferedInputStream(new DataInputStream(clientSocket.getInputStream()));
        bufferedOutputStream = new BufferedOutputStream(new DataOutputStream(clientSocket.getOutputStream()));
    }

    public static void stop() {
        try {
            clientSocket.close();
            bufferedInputStream.close();
            bufferedOutputStream.close();
        } catch (IOException e) {
            // e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return clientSocket;
    }

    public ServerAnswer dataExchange(CommandArguments request) {
        ServerAnswer serverAnswer;
        try {
            TCPExchanger.write(bufferedOutputStream, request);
            serverAnswer = (ServerAnswer) TCPExchanger.read(bufferedInputStream);
        } catch (ClassCastException | IOException e) {
            return null;
        }
        return serverAnswer;
    }
}
