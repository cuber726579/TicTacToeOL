package Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import java.io.DataInputStream;
import java.io.InputStreamReader;

public class TicTacToeGUI {
    private Tool player;
    private Board board;
    private TicTacToe game;
    private Tool computer;
    private Tool person;

    private BufferedReader in;
    private PrintWriter out;
    private JFrame frame;
    private JPanel mainPanel;
    private JPanel boardPanel;
    private JButton[][] buttons;

    public TicTacToeGUI(int agentIQ, BufferedReader in, PrintWriter out, Tool personTool) {
        this.game = new TicTacToe(agentIQ,in,out);

        if (personTool != null) {
            game.updatePlayer(personTool);
        }
        else {
            game.randomFirstPlayer();
        }

        this.in = in;
        this.out = out;
        board = game.board;

        player = game.player;
        computer = game.computer;
        person = game.person;

        frame = new JFrame("Tic Tac Toe Game");
        mainPanel = new JPanel(new BorderLayout());
        boardPanel = new JPanel(new GridLayout(Board.SIZE, Board.SIZE)); // For button
        buttons = new JButton[Board.SIZE][Board.SIZE];

        String hintMessage = "\n" +
                "************************************************\n" +
                "Let's play Tic Tac Toe!\n" +
                "When asked for a move, click the location you want.\n" +
                (player == person ? "You move first.\n" : "Computer moves first.\n") +
                "************************************************\n";

        JOptionPane.showMessageDialog(frame, hintMessage, "Welcome to Tic Tac Toe!", JOptionPane.INFORMATION_MESSAGE);

        initBoard();
        mainPanel.add(boardPanel, BorderLayout.CENTER);
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 300);
        frame.setVisible(true);
    }

    private void initBoard() {
        for (int i = 0; i < Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {
                JButton button = new JButton("");
                button.addActionListener(new ButtonClickListener(i, j,in,out));
                buttons[i][j] = button;
                boardPanel.add(button);
            }
        }
        if (player == computer) {
            playAgentMove(in);
        }
    }


    private void updateBoard() {
        for (int i = 0; i < ExampleCode.GUI.Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {
                Tool tool = board.getToolAt(i, j);
                String buttonText = "";
                if (tool == Tool.X) {
                    buttonText = "X";
                } else if (tool == Tool.O) {
                    buttonText = "O";
                }
                buttons[i][j].setText(buttonText);
            }
        }
    }


    private class ButtonClickListener implements ActionListener {
        private int row;
        private int col;

        public ButtonClickListener(int row, int col, BufferedReader in, PrintWriter out) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!board.isGameWon() && !board.isFull()) {
                boolean validMove = playHumanMove(row + 1, col + 1, out);

                if (validMove && !board.isGameWon() && !board.isFull()) {
                    playAgentMove(in);
                }
            }

            if (board.isGameWon() || board.isFull()) {
                JOptionPane.showMessageDialog(frame, getGameResult(), "Game Over", JOptionPane.INFORMATION_MESSAGE);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            }
        }
    }

    private String getGameResult() {
        if (board.isGameWon()) {
            Tool winner = board.getWinner();
            return winner == person ? "You won!" : "Computer won!";
        } else if (board.isFull()) {
            return "It's a draw!";
        }
        return "";
    }

    public boolean playHumanMove(int row, int col, PrintWriter out) {
        Move humanMove = game.getAMoveWithGUI(row, col, game.person);

        if (humanMove != null) {
            board.handleMove(humanMove, person);
            updateBoard();
            out.println(row);
            out.println(col);
            return true; // move was successful
        }
        else {
            JOptionPane.showMessageDialog(frame, "Invalid move. Please try again.", "Invalid Move", JOptionPane.WARNING_MESSAGE);
            return false; // move was not successful
        }
    }

    private void playAgentMove(BufferedReader in) {
        int row,col;
        try {
            row = in.read();
            col = in.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Move agentMove = new Move(row,col);
        if (agentMove != null) {
            board.handleMove(agentMove, computer);
            updateBoard();
            if (!board.isGameWon() && !board.isFull()) {
            player = game.oppositePlayer();
            }
        }
    }
}
