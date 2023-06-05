package Main;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 */
public class TicTacToeClient {
    private static final int SERVER_PORT = 8888;
    private static BufferedReader inFromServer;
    private static Scanner inFromSystem;
    private static PrintWriter out;

    public static void main(String[] args) {
        System.out.println("Try to connect....");
        // Open socket.
        try (Socket client = new Socket("localhost", SERVER_PORT);
             InputStream inputStream = client.getInputStream();
             OutputStream outputStream = client.getOutputStream()) {
            System.out.println("Connect successfully....");
            // Turn streams into scanners and writers.
            inFromServer = new BufferedReader(new InputStreamReader(inputStream));
            out = new PrintWriter(outputStream);
            inFromSystem = new Scanner(System.in);
            new TicTacToeGUI(100,inFromServer,out,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void playGame(Socket client) throws IOException, InterruptedException {
        while (true) {
            // Server sends message part.
            String msg = null;
            while ((msg = inFromServer.readLine()) != null) {
                // Read message until "-1"
                if (msg.equals("-1"))
                    break;
                System.out.println(msg);
            }
            // Client sends message part.
            String command = inFromSystem.nextLine();
            if (command.equals("EXIT")) {
                return;
            }
            out.println(command);
            out.flush();
        }
    }
}
