package Main;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final int SERVER_PORT = 8888;
    private String IP;
    private static final String hint = "New game....\n1.One player\n" +
            "2.Two players\nEnter the number:";
    public static void main(String[] args) throws IOException {
        System.out.println("Try to connect....");

        Scanner scanner = new Scanner(System.in);
        System.out.println("Please input the server IP: ");
        String IP = scanner.next();
        Socket client;
        while (true) {
            client = new Socket(IP, SERVER_PORT);
            if(client.isConnected()) break;
        }
        System.out.println("Connect successfully....");

        DataInputStream in = new DataInputStream(client.getInputStream());
        DataOutputStream out = new DataOutputStream(client.getOutputStream());

        System.out.println("Randomly select first player...");
        int icon = in.readInt();
        Tool clientIcon = (icon == (Tool.X).ordinal()) ? Tool.X : Tool.O;

        // Send Game mode from client
        System.out.println(hint);
        int gameMode = scanner.nextInt();
        out.writeInt(gameMode);

        // Start Gaming
        new TicTacToeGUI(100, clientIcon, client); // Default setting: Client is O}

        System.out.println("End of client");
//        client.close();
    }
}
