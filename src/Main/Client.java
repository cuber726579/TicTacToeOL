package Main;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final int SERVER_PORT = 12138;
    public static void main(String[] args) throws IOException {
        System.out.println("Try to connect....");

        Scanner scanner = new Scanner(System.in);
        Socket client = new Socket("localhost", SERVER_PORT);
        System.out.println("Connect successfully....");

        DataInputStream in = new DataInputStream(client.getInputStream());
        DataOutputStream out = new DataOutputStream(client.getOutputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        // Send Game mode from client

        // Start Gaming
        System.out.println(client.isConnected());
        new TicTacToeGUI(100,Tool.O,client); // Default setting: Client is O

        System.out.println("End of client");
//        client.close();
    }
}
