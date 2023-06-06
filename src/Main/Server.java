package Main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    private static final int SERVER_PORT = 8888;

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        ServerSocket server = new ServerSocket(SERVER_PORT);
        System.out.println("Waiting for clients to connect....");

        Socket client = server.accept();
        System.out.println("Client connected.");

        DataInputStream in = new DataInputStream(client.getInputStream());
        DataOutputStream out = new DataOutputStream(client.getOutputStream());

        // Retrieve Game mode from client

        // Start Gaming
        System.out.println(client.isConnected());
        new TicTacToeGUI(100,Tool.X,client); // Default setting: Client is X

//        server.close();
    }
}
