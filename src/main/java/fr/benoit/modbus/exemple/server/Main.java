package fr.benoit.modbus.exemple.server;

import fr.benoit.modbus.exemple.SocketReader;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {


    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(502);
            Socket client = server.accept();
            SocketReader reader = new SocketReader(client);
            reader.join();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
