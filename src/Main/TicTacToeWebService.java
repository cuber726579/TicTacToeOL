package Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 */
public class TicTacToeWebService implements Runnable {
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private static final String hint = "New game....\n1.One player\n" +
            "2.Two players\nEnter the number:";

    public TicTacToeWebService(Socket client) {
        this.client = client;
    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream());
            beginGame();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void beginGame() throws IOException {
        int agentIQ = 100;
        while (true) {
            out.println(hint);
            out.println("-1");
            out.flush();

            String command = in.readLine();
            if (command == null || command.equals("EXIT")) {
                return;
            }
            int gameMode = Integer.parseInt(command);

            if (gameMode == 1) {
                TicTacToe game = new TicTacToe(100,in,out);
            } else if (gameMode == 2) {
                new TicTacToeGUI(100,in,out,null);
            }
        }
    }
}
