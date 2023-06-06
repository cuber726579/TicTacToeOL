package Main;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 */
public class TicTacToeClient {
    private static final int SERVER_PORT = 8888;
    private static DataInputStream in;
    private static DataOutputStream out;

    private static Scanner inFromSystem;

    public static void main(String[] args) {
        System.out.println("Try to connect....");
        // Open socket.
        try (Socket client = new Socket("localhost", SERVER_PORT);
             InputStream inputStream = client.getInputStream();
             OutputStream outputStream = client.getOutputStream()) {
            System.out.println("Connect successfully....");
            // Turn streams into scanners and writers.
            in = new DataInputStream(inputStream);
            out = new DataOutputStream(outputStream);
//            out.writeInt(2);

            new TicTacToeGUI(100,in,out,Tool.X,GameMode.PVP); // X is Client
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
