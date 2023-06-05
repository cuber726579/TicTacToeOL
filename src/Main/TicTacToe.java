package Main;

/**
   A class playing a game of TicTacToe
*/

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Random;

public class TicTacToe {
  public Tool player;
  public Tool computer;
  public Tool person;
  public Board board;
  public TicTacToeAgent agent;

  public TicTacToe(int agentIQ, BufferedReader in, PrintWriter out) {
    this.in = in;
    this.out = out;
    showStartHint();
    randomFirstPlayer();

    board = new Board(out);
    agent = agentCreator(agentIQ);
  }

  public TicTacToe(BufferedReader in, PrintWriter out) {
    this(100, in, out);
  }

  public void updatePlayer(Tool newPerson) {
    this.person = newPerson;
    this.computer = this.person == Tool.X ? Tool.O : Tool.X;
    this.player = this.person == Tool.X ? this.person : this.computer;
  }

  public boolean play() {
    board.show();

    boolean gameOver = false;
    while (!gameOver) {
      Move move = getAMove();
      if (move == null) {
        return false;
      }

      board.handleMove(move, player);

      board.show();

      if (board.isGameWon() || board.isFull())
        gameOver = true;
      else
        player = oppositePlayer();

    }

    showGameResult();
    return true;
  }

  private TicTacToeAgent agentCreator(int iQ) {
    TicTacToeAgent agent;
    switch (iQ) {
      case 0:
        agent = new RandomAgent(board);
        break;
      case 100:
        agent = new AIAssistance(board, computer, person);
        break;
      default:
        agent = new WisdomAgent(board, computer, person, iQ);
        break;
    }
    return agent;
  }

  private void showStartHint() {
    out.println(HINT_MESSAGE);
  }

  public void randomFirstPlayer() {
    if (generator.nextBoolean()) {
      person = Tool.X;
      computer = Tool.O;
    } else {
      person = Tool.O;
      computer = Tool.X;
    }
    player = Tool.X;
  }

  public Tool oppositePlayer() {
    return (player == computer) ? person : computer;
  }

  private void showGameResult() {
    if (board.isGameWon())
      out.println(player == person ? "You won!" : "I won!");
    else if (board.isFull())
      out.println("We tied!");
    else
      out.println("Something went wrong!");
    out.println(board.getMoves());
    out.flush();
  }

  Move getAMoveWithGUI(Integer row, Integer col, Tool player) {
    Move move = null;

    if (player == computer) {
      System.out.println("[TicTacToe LOG] It is my move.  I am " + player);
      move = agent.nextMove();
    } else {
      System.out.println("[TicTacToe LOG] It is your move.  You are " + player);
      if (row != null && col != null) {
        move = new Move(row, col);
        if (!board.isValid(move)) {
          move = null;
          System.out.println("[TicTacToe LOG] Invalid move by user\n");
        }
      }
    }
    return move;
  }

  // Creates a move, either a random generated move or as input from the user
  private Move getAMove() {
    Move move = null;

    if (player == computer) {
      out.println("It is my move.  I am " + player);
      move = agent.nextMove();
    } else {
      out.println("It is your move.  You are " + player);
      move = getAValidMoveFromPerson();
    }

    return move;
  }

  private Move getAValidMoveFromPerson() {
    Move move = null;
    while (true) {
      try {
        out.println("Enter a row and column on one line: ");
        out.println("-1");
        out.flush();
        String command = in.readLine();
        if (command.equals("EXIT")) {
          return null;
        }
        String[] m = command.split(" ");
        move = new Move(Integer.parseInt(m[0]), Integer.parseInt(m[1]));
        // Generates an exception if can't make a move from r and c

        if (board.isValid(move))
          return move;

        out.println("Invalid move. Try again!");
      } catch (Exception e) {
        out.println("Input error. Try again!");
      }
    }
  }

  private static BufferedReader in;
  private static PrintWriter out;
  private static Random generator = new Random();

  private static final String HINT_MESSAGE = "\n" +
      "************************************************\n" +
      "Let's play Tic Tac Toe!\n" +
      "When asked for a move, enter location you want.\n" +
      "Enter the row first and then the column, both on the same line.\n" +
      "The row and column must in the range 1 .. 3\n" +
      "************************************************\n";
}
