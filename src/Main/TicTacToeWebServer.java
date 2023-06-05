package Main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 */
public class TicTacToeWebServer {
    private static final int SERVER_PORT = 8888;

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(SERVER_PORT);
        System.out.println("Waiting for clients to connect....");

        while (true) {
            Socket client = server.accept();
            System.out.println("Client connected.");
            TicTacToeWebService service = new TicTacToeWebService(client);
            Thread t = new Thread(service);

            t.start();
        }
    }
}
