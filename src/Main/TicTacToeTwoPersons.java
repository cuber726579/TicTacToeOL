package Main;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;

public class TicTacToeTwoPersons {
  private Tool player;
  private Tool computer;
  private Tool person;
  private BoardTwoPerson board;

  private JFrame frame;
  private JPanel mainPanel;
  private JPanel boardPanel;
  private JButton[][] buttons;

  public TicTacToeTwoPersons(BufferedReader in, PrintWriter out) {
    inFromSystem = new Scanner(System.in);
    this.in = in;
    this.out = out;
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

  private void showStartHint() {
    out.println(HINT_MESSAGE);
    System.out.println(HINT_MESSAGE);
  }

  private void randomFirstPlayer() {
    if (generator.nextBoolean()) {
      person = Tool.X;
      computer = Tool.O;
    } else {
      person = Tool.O;
      computer = Tool.X;
    }
    player = Tool.X;
  }

  private Tool oppositePlayer() {
    return (player == computer) ? person : computer;
  }

  private void showGameResult() {
    if (board.isGameWon()) {
      out.println(player == person ? "You won!" : "I won!");
      System.out.println(player != person ? "You won!" : "I won!");
    } else if (board.isFull()) {
      out.println("We tied!");
      System.out.println("We tied!");
    } else {
      out.println("Something went wrong!");
      System.out.println("Something went wrong!");
    }

    out.println(board.getMoves());
    System.out.println(board.getMoves());
    out.flush();
  }

  // Creates a move, either a random generated move or as input from the user
  private Move getAMove() {
    Move move = null;

    if (player == computer) {
      out.println("It is my move.  I am " + player);
      System.out.println("It is your move.  You are " + player);
      move = getAValidMoveFromMe();
    } else {
      out.println("It is your move.  You are " + player);
      System.out.println("It is my move.  I am " + player);
      move = getAValidMoveFromPerson();
    }

    return move;
  }

  private Move getAValidMoveFromMe() {
    Move move = null;
    while (true) {
      try {
        System.out.println("Enter a row and column on one line: ");
        String command = inFromSystem.nextLine();
        if (command.equals("EXIT")) {
          return null;
        }
        String[] m = command.split(" ");
        move = new Move(Integer.parseInt(m[0]), Integer.parseInt(m[1]));
        // Generates an exception if can't make a move from r and c

        if (board.isValid(move))
          return move;

        System.out.println("Invalid move. Try again!");
      } catch (Exception e) {
        System.out.println("Input error. Try again!");
      }
    }
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

  private static Scanner inFromSystem;
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