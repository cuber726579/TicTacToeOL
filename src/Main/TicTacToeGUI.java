package Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class TicTacToeGUI {
    // Indicate Server or Client
    private Tool user;
    private boolean isUserTurn;

    private TicTacToe game;
    private Tool serverPlayer;
    private DataInputStream in;
    private DataOutputStream out;
    private JFrame frame;
    private JPanel mainPanel;
    private JPanel boardPanel;
    private JButton[][] buttons;

    public TicTacToeGUI(int agentIQ, DataInputStream in, DataOutputStream out, Tool personTool) {
        this.game = new TicTacToe(agentIQ);

        if (personTool != null) {
            game.updatePlayer(personTool);
        }
        else {
            game.randomFirstPlayer();
        }

        this.in = in;
        this.out = out;

        user = game.player;

        frame = new JFrame("Tic Tac Toe Game");
        mainPanel = new JPanel(new BorderLayout());
        boardPanel = new JPanel(new GridLayout(Board.SIZE, Board.SIZE)); // For button
        buttons = new JButton[Board.SIZE][Board.SIZE];

        String hintMessage = "\n" +
                "************************************************\n" +
                "Let's play Tic Tac Toe!\n" +
                "When asked for a move, click the location you want.\n" +
                (game.player == user ? "You move first.\n" : "Computer moves first.\n") +
                "************************************************\n";

        JOptionPane.showMessageDialog(frame, hintMessage, "Welcome to Tic Tac Toe!", JOptionPane.INFORMATION_MESSAGE);

        initBoard();
        listenForOpponentMove(in);
        mainPanel.add(boardPanel, BorderLayout.CENTER);
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 300);
        frame.setVisible(true);

        isUserTurn = (game.player == this.user);
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
//        if (user == game.player) {
//            playAgentMove(in);
//        }
    }

    private void updateBoard() {
        for (int i = 0; i < ExampleCode.GUI.Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {
                Tool tool = game.board.getToolAt(i, j);
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

        public ButtonClickListener(int row, int col, DataInputStream in, DataOutputStream out) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if(!isUserTurn) return;

            if (game.board.isGameWon() || game.board.isFull()) {
                JOptionPane.showMessageDialog(frame, getGameResult(), "Game Over", JOptionPane.INFORMATION_MESSAGE);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            }

            if (!game.board.isGameWon() && !game.board.isFull()) {
                boolean validMove = playUserMove(row + 1, col + 1 ,out);
                isUserTurn = false;

//                if (validMove && !game.board.isGameWon() && !game.board.isFull()) {
//                    playAgentMove(in);
//                }
            }
        }
    }

    private String getGameResult() {
        if (game.board.isGameWon()) {
            Tool winner = game.board.getWinner();
            return winner == user ? "You won!" : "Computer won!";
        } else if (game.board.isFull()) {
            return "It's a draw!";
        }
        return "";
    }

    public boolean playUserMove(int row, int col, DataOutputStream out) {
        Move userMove = game.getAMoveWithGUI(row, col, game.person);

        if (userMove != null) {
            game.board.handleMove(userMove, person);
            updateBoard();

            System.out.println(row);
            System.out.println(col);

            try {
                out.writeInt(row);
                out.writeInt(col);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return true;
        }
        else {
            JOptionPane.showMessageDialog(frame, "Invalid move. Please try again.", "Invalid Move", JOptionPane.WARNING_MESSAGE);
            return false; // move was not successful
        }
    }

    private void playAgentMove(DataInputStream in) {
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
                user = game.oppositePlayer();
            }
        }
    }

    public void listenForOpponentMove(DataInputStream in) {
        new Thread(() -> {
            while (true) {
                try {
                    String moveStr = in.readUTF();
                    String[] move = moveStr.split(",");
                    int row = Integer.parseInt(move[0]);
                    int col = Integer.parseInt(move[1]);
                    // update board with opponent's move
                    board.handleMove(new Move(row, col), game.oppositePlayer());
                    updateBoard();
                    updateTurnLabel();
                    isUserTurn = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }).start();
    }

    public void updateTurnLabel() {
        if (!board.isGameWon() && !board.isFull()) {
            turnLabel.setText("Current turn: " + (user == person ? "Person" : "Computer"));
        } else {
            turnLabel.setText(getGameResult());
        }
    }

}
