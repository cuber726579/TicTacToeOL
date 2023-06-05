package Main;

import java.io.*;
import java.net.Socket;

/**
 *
 */
public class TicTacToeWebService implements Runnable {
    private Socket client;
    private DataInputStream in;
    private DataOutputStream out;
    private static final String hint = "New game....\n1.One player\n" +
            "2.Two players\nEnter the number:";

    public TicTacToeWebService(Socket client) {
        this.client = client;
    }

    public void run() {
        try {
            in = new DataInputStream(client.getInputStream());
            out = new DataOutputStream(client.getOutputStream());
            beginGame();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void beginGame() throws IOException {
        int agentIQ = 100;
        while (true) {
            System.out.println(hint);
            System.out.println("-1");
            out.flush();

            int gameMode = in.readInt();
//            if (command == null || command.equals("EXIT")) {
//                return;
//            }
//            int gameMode = Integer.parseInt(command);

            if (gameMode == 1) {
                TicTacToe game = new TicTacToe(100,Tool.X);
            } else if (gameMode == 2) {
                new TicTacToeGUI(100,in,out,Tool.O); // O is server
            }
        }
    }
}
