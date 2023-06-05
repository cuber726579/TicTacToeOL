package Main;

/**
   A class playing a game of TicTacToe
*/

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

public class TicTacToe {
  public Tool player;
  public Tool computer;
  public Tool person;
  public Board board;
  public TicTacToeAgent agent;
  private static DataInputStream in;
  private static DataOutputStream out;
  private static Random generator = new Random();

  public TicTacToe(int agentIQ, DataInputStream in, DataOutputStream out) {
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
    try {
      out.writeUTF(HINT_MESSAGE);
      out.flush();
    } catch (Exception e) {
      e.printStackTrace();
    }
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
      try {
          if (board.isGameWon())
            out.writeUTF(player == person ? "You won!" : "I won!");
          else if (board.isFull())
            out.writeUTF("We tied!");
          else
            out.writeUTF("Something went wrong!");
            ArrayList<Move> moves = board.getMoves();
            StringBuilder movesStr = new StringBuilder();
            for (Move move : moves) {
                movesStr.append(move.getRow()).append(",").append(move.getColumn()).append(";");
            }
            out.writeUTF(movesStr.toString());
            out.flush();
      } catch (Exception e) {
      e.printStackTrace();
    }
  }

  Move getAMoveWithGUI(Integer row, Integer col, Tool player) {
    Move move = null;

    if (player == computer) {
      try {
        out.writeUTF("[TicTacToe LOG] It is my move.  I am " + player);
        out.flush();
        move = agent.nextMove();
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      try {
        out.writeUTF("[TicTacToe LOG] It is your move.  You are " + player);
        out.flush();
        if (row != null && col != null) {
          move = new Move(row, col);
          if (!board.isValid(move)) {
            move = null;
            out.writeUTF("[TicTacToe LOG] Invalid move by user\n");
            out.flush();
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return move;
  }
/*
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

 */

  // Creates a move, either a random generated move or as input from the user
  private Move getAMove() {
    Move move = null;

    if (player == computer) {
      try {
        out.writeUTF("It is my move.  I am " + player);
        out.flush();
        move = agent.nextMove();
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      try {
        out.writeUTF("It is your move.  You are " + player);
        out.flush();
        move = getAValidMoveFromPerson();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    return move;
  }


  private Move getAValidMoveFromPerson() {
    Move move = null;
    while (true) {
      try {
        out.writeUTF("Enter a row and column on one line: ");
        out.flush();
        out.writeUTF("-1");
        out.flush();
        String command = in.readUTF();
        if (command.equals("EXIT")) {
          return null;
        }
        String[] m = command.split(" ");
        move = new Move(Integer.parseInt(m[0]), Integer.parseInt(m[1]));
        // Generates an exception if can't make a move from r and c

        if (board.isValid(move))
          return move;

        out.writeUTF("Invalid move. Try again!");
        out.flush();
      } catch (Exception e) {
        out.writeUTF("Input error. Try again!");
      }
    }
  }

  private static final String HINT_MESSAGE = "\n" +
      "************************************************\n" +
      "Let's play Tic Tac Toe!\n" +
      "When asked for a move, enter location you want.\n" +
      "Enter the row first and then the column, both on the same line.\n" +
      "The row and column must in the range 1 .. 3\n" +
      "************************************************\n";
}
