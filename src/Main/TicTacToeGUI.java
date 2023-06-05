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
    private Tool clientPlayer;
    private DataInputStream in;
    private DataOutputStream out;
    private JFrame frame;
    private JPanel mainPanel;
    private JPanel boardPanel;
    private JButton[][] buttons;

    public TicTacToeGUI(int agentIQ, DataInputStream in, DataOutputStream out, Tool firstPLayer, GameMode gameMode) {
        this.game = new TicTacToe(agentIQ, Tool.X, gameMode);

        this.in = in;
        this.out = out;

        user = firstPLayer;
        isUserTurn = (game.curPlayer == this.user);


        frame = new JFrame("Tic Tac Toe Game");
        mainPanel = new JPanel(new BorderLayout());
        boardPanel = new JPanel(new GridLayout(Board.SIZE, Board.SIZE)); // For button
        buttons = new JButton[Board.SIZE][Board.SIZE];

        String hintMessage = "\n" +
                "************************************************\n" +
                "Let's play Tic Tac Toe!\n" +
                "When asked for a move, click the location you want.\n" +
                (game.curPlayer == user ? "You move first.\n" : "Computer moves first.\n") +
                "************************************************\n";

        JOptionPane.showMessageDialog(frame, hintMessage, "Welcome to Tic Tac Toe!", JOptionPane.INFORMATION_MESSAGE);

        initBoard();
        //listenForOpponentMove(in);
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
                button.addActionListener(new ButtonClickListener(i, j, in, out));
                buttons[i][j] = button;
                boardPanel.add(button);
            }
        }
        if (!isUserTurn) {
            listenForOpponentMove(in);
        }
    }

    private void updateBoard() {
        for (int i = 0; i < Board.SIZE; i++) {
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
            if (!isUserTurn) return;

            if (game.board.isGameWon() || game.board.isFull()) {
                JOptionPane.showMessageDialog(frame, getGameResult(), "Game Over", JOptionPane.INFORMATION_MESSAGE);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            }

            if (!game.board.isGameWon() && !game.board.isFull()) {
                boolean validMove = playUserMove(row + 1, col + 1, out);
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
        Move userMove = new Move(row,col);
        System.out.println("Button" + row);
        System.out.println("Button" + col);
        if (userMove != null) {
            game.board.handleMove(userMove, user);
            updateBoard();

            try {
                out.writeInt(row);
                out.writeInt(col);
                out.flush();
            } catch (IOException e) {
//                throw new RuntimeException(e);
                JOptionPane.showMessageDialog(frame, "Error sending move to opponent. Game will be terminated.", "Communication Error", JOptionPane.ERROR_MESSAGE);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            }

            return true;
        } else {
            JOptionPane.showMessageDialog(frame, "Invalid move. Please try again.", "Invalid Move", JOptionPane.WARNING_MESSAGE);
            return false; // move was not successful
        }
    }

//    private void playAgentMove(DataInputStream in) {
//        int row,col;
//        try {
//            row = in.read();
//            col = in.read();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        Move agentMove = new Move(row,col);
//        if (agentMove != null) {
//            board.handleMove(agentMove, computer);
//            updateBoard();
//            if (!board.isGameWon() && !board.isFull()) {
//                user = game.oppositePlayer();
//            }
//        }
//    }
/*
    public void listenForOpponentMove(DataInputStream in) {
        new Thread(() -> {
            final boolean[] keepRunning = {true};
            while (keepRunning[0]) {
                try {
                    int row = in.readInt();
                    int col = in.readInt();
                    System.out.println("Listener:" + row);
                    System.out.println("Listener:" + col);

                    SwingUtilities.invokeLater(() -> {
                        // update board with opponent's move
                        Tool opponent = (this.user == Tool.X) ? Tool.O : Tool.X;
                        game.board.handleMove(new Move(row, col), opponent);
                        updateBoard();
                        isUserTurn = true;

                        if (game.board.isGameWon() || game.board.isFull()) {
                            JOptionPane.showMessageDialog(frame, getGameResult(), "Game Over", JOptionPane.INFORMATION_MESSAGE);
                            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                            keepRunning[0] = false; // 替代break语句
                        }
                    });
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(frame, "Error receiving move from opponent. Game will be terminated.", "Communication Error", JOptionPane.ERROR_MESSAGE);
                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    keepRunning[0] = false; // 替代break语句
                }
            }
        }).start();
    }
*/
    public void listenForOpponentMove(DataInputStream in) {
        final boolean[] isGameActive = {true}; // 标志游戏是否继续进行

        new Thread(() -> {
            while (isGameActive[0]) {
                try {
                    int row = in.readInt();
                    int col = in.readInt();
                    System.out.println("Listener:" + row);
                    System.out.println("Listener:" + col);

                    SwingUtilities.invokeLater(() -> {
                        // update board with opponent's move
                        Tool opponent = (this.user == Tool.X) ? Tool.O : Tool.X;
                        game.board.handleMove(new Move(row, col), opponent);
                        updateBoard();
                        isUserTurn = true;

                        if (game.board.isGameWon() || game.board.isFull()) {
                            JOptionPane.showMessageDialog(frame, getGameResult(), "Game Over", JOptionPane.INFORMATION_MESSAGE);
                            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                            isGameActive[0] = false; // 退出循环
                        }
                    });
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(frame, "Error receiving move from opponent. Game will be terminated.", "Communication Error", JOptionPane.ERROR_MESSAGE);
                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    isGameActive[0] = false; // 退出循环
                }
            }
        }).start();
    }





//    public void updateTurnLabel() {
//        if (!board.isGameWon() && !board.isFull()) {
//            turnLabel.setText("Current turn: " + (user == person ? "Person" : "Computer"));
//        } else {
//            turnLabel.setText(getGameResult());
//        }
//    }

}
