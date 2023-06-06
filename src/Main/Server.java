package Main;

import java.io.*;
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
        System.out.println("Reading game mode from client...");
        int gameMode = in.readInt();
        System.out.println(gameMode);

        // Start Gaming
        if (gameMode == 1) {
            new TicTacToe(100,in,out);
        } else {
//            System.out.println("Mode 2");
            new TicTacToeGUI(100,Tool.X,client); // Default setting: Server is X
        }
    }
}
