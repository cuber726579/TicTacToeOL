package Main;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class TicTacToeGUI {
    private Tool user;
    private TicTacToe game;
    private Tool person;
    private Tool computer;
    private Board board;
    private Socket client;
    private DataInputStream in;
    private DataOutputStream out;
    private boolean isUserTurn;

    private JFrame frame;
    private JPanel mainPanel;
    private JPanel boardPanel;
    private JButton[][] buttons;

    private JLabel turnLabel;
    private JPanel turnPanel;

    public TicTacToeGUI(int agentIQ, Tool user, Socket client) {
        this.game = new TicTacToe(agentIQ); // Client always first

        try {
            this.client = client;
            this.in = new DataInputStream(client.getInputStream());
            this.out = new DataOutputStream(client.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.user = user; // Client or server
        this.isUserTurn = (user == game.player);

        this.board = game.board;
        this.computer = game.server;
        this.person = game.client;

        frame = new JFrame("Tic Tac Toe Game") {
            @Override
            public void paint(Graphics g) {  // override paint method to maintain the frame's aspect ratio
                super.paint(g);
                Dimension originalSize = getSize();
                Dimension newSize = new Dimension(originalSize.width, originalSize.width);
                setSize(newSize);
            }
        };


        JPanel outerPanel = new JPanel(); // New outer panel
        outerPanel.setLayout(new BorderLayout());

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        boardPanel = new JPanel(new GridLayout(Board.SIZE, Board.SIZE));
        buttons = new JButton[Board.SIZE][Board.SIZE];

        String hintMessage = "\n" +
                "************************************************\n" +
                "Let's play Tic Tac Toe!\n" +
                "When asked for a move, click the location you want.\n" +
                (this.user == person ? "You move first.\n" : "Client moves first.\n") +
                "************************************************\n";

        JOptionPane.showMessageDialog(frame, hintMessage, "Welcome to Tic Tac Toe!", JOptionPane.INFORMATION_MESSAGE);


        turnLabel = new JLabel();
        turnLabel.setHorizontalAlignment(JLabel.CENTER);  // 让文字在标签中居中
        turnLabel.setFont(new Font("Arial", Font.BOLD, 24));  // 设置大一些的字体
        turnLabel.setForeground(Color.decode("#5AA4AE"));  // 设置文字颜色为浅蓝色

        turnPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Enable antialiasing for smooth corners
                g2d.setColor(getBackground()); // Use panel's background color
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15); // Fill round rectangle
            }
        };
        turnPanel.setOpaque(false); // Make JPanel transparent to show round rectangle
        turnPanel.setBackground(Color.WHITE);  // 设置背景颜色为浅灰色
        turnPanel.setLayout(new BorderLayout());  // 设置布局管理器为 BorderLayout
        turnPanel.add(turnLabel, BorderLayout.CENTER);  // 将 turnLabel 添加到 turnPanel 中
        outerPanel.add(turnPanel, BorderLayout.NORTH);  // 将 turnPanel 添加到 outerPanel 中，而不是 contentPanel
        updateTurnLabel();

        initBoard();
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (buttons != null && buttons[0][0] != null) {  // only call adjustFontSize when buttons is not null
                    adjustFontSize();
                }
            }
        });

        GridBagConstraints gbc = new GridBagConstraints(); // New GridBagConstraints object
        gbc.fill = GridBagConstraints.BOTH; // This makes the component fill its display area
        gbc.weightx = 1.0; // This determines how space gets divided up among components
        gbc.weighty = 1.0; // This determines how space gets divided up among components

        //Border emptyBorder = BorderFactory.createEmptyBorder(20, 0, 20, 0);

        //outerPanel.setBorder(emptyBorder); // 将空白边框应用到outerPanel
        outerPanel.add(turnPanel, BorderLayout.NORTH); // 添加turnPanel到outerPanel的中间
        outerPanel.add(mainPanel, BorderLayout.CENTER); // 添加mainPanel到outerPanel的底部

        mainPanel.add(boardPanel, BorderLayout.CENTER);
        //outerPanel.add(mainPanel, BorderLayout.CENTER); // Add mainPanel to outerPanel
        outerPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30)); // Set a 10 pixel border
        Border emptyBorder = BorderFactory.createEmptyBorder(10, 30, 50, 30);
        outerPanel.setBorder(emptyBorder); // 将空白边框应用到outerPanel
        outerPanel.setBackground(Color.WHITE);
        mainPanel.setBackground(Color.WHITE);
        boardPanel.setBackground(Color.WHITE);
        frame.setContentPane(outerPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setVisible(true);
        frame.setMinimumSize(new Dimension(300, 300));
        frame.setLocationRelativeTo(null);


        if (user != game.player) {
            playOpponentMove();
        } else {
            System.out.println("Waiting for button being clicked");
        }
        System.out.println(client.isConnected());
    }

    private void adjustFontSize() {
        int size = frame.getSize().width / (Board.SIZE * 2);  // calculate new font size based on frame size
        Font newFont = new Font("Arial", Font.BOLD, size);
        for (int i = 0; i < Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {
                buttons[i][j].setFont(newFont);  // set new font size to each button
            }
        }
        int labelSize = frame.getSize().width / 15; // Adjust the size value as per your requirement
        Font labelFont = new Font("Arial", Font.BOLD, labelSize);
        turnLabel.setFont(labelFont); // set new font size to turnLabel
    }

    private void initBoard() {
        for (int i = 0; i < Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {
                JButton button = new JButton("");
                int top = (i == 0) ? 0 : 2;
                int left = (j == 0) ? 0 : 2;
                int bottom = (i == Board.SIZE-1) ? 0 : 2;
                int right = (j == Board.SIZE-1) ? 0 : 2;
                button.setBackground(Color.WHITE);
                button.setBorder(new MatteBorder(top, left, bottom, right, Color.LIGHT_GRAY));
                button.setMargin(new Insets(10, 10, 10, 10));  // add margin around the button
                button.setFont(new Font("Arial", Font.BOLD, 24));  // set font size to occupy more space in button
                button.addActionListener(new ButtonClickListener(i, j));
                buttons[i][j] = button;
                boardPanel.add(button);
            }
        }
    }





    private class ButtonClickListener implements ActionListener {
        private int row;
        private int col;

        public ButtonClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if(!isUserTurn) return; // 不是当前回合则退出

            if (!board.isGameWon() && !board.isFull()) {
                boolean validMove = playUserMove(row + 1, col + 1);

                if (validMove && !board.isGameWon() && !board.isFull()) {
                    playOpponentMove();
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

    public boolean playUserMove(int row, int col) {
        Move userMove = new Move(row,col);

        if (game.isValid(userMove)) {
            game.handleMove(userMove, game.player);
            this.isUserTurn = false;
            updateButtons();
            updateTurnLabel();
            System.out.println(isUserTurn);

            System.out.println("Transmit:" + ((row-1) * 3 + col));
            try {
                out.writeInt((row-1) * 3 + col);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return true; // move was successful
        }
        else {
            JOptionPane.showMessageDialog(frame, "Invalid move. Please try again.", "Invalid Move", JOptionPane.WARNING_MESSAGE);
            return false; // move was not successful
        }
    }


    private void playOpponentMove() {
        // 监听对手落子
        System.out.println("Listening!");
        int fromOpponent;

        System.out.println(client.isConnected());

        try {
            System.out.println(in.available());
            fromOpponent = in.readInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int row = (fromOpponent - 1) / 3 + 1;
        int col = (fromOpponent - 1) % 3 + 1;
        System.out.println("Row,Col:" + row + "," + col);
        Move opponentMove = new Move(row, col);
        game.handleMove(opponentMove , game.player);
        isUserTurn = true;

        updateButtons();
        updateTurnLabel();
    }

    private void updateButtons() {
        game.board.show();
        for (int i = 0; i < Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {
                Tool tool = game.board.getToolAt(i, j);

                String buttonText = "";
                String color = "#FFFFF";
                if (tool == Tool.X) {
                    buttonText = "X";
                    color = "#ED723F";
                } else if (tool == Tool.O) {
                    buttonText = "O";
                    color = "#5AA4AE";
                }
                buttons[i][j].setText(buttonText);
                buttons[i][j].setForeground(Color.decode(color));
            }
        }
    }

    public void updateTurnLabel() {
        if (!board.isGameWon() && !board.isFull()) {
            String text = isUserTurn? "Your Turn" : "Opponent turn";
            turnLabel.setText(text);
        } else {
            turnLabel.setText(getGameResult());
        }
    }
//    public static void main(String[] args) {
//        new TicTacToeGUI(100, Tool.valueOf("O"));
//    }
}
