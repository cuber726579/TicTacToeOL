package Main;

/**
   A class playing a game of TicTacToe
*/

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class TicTacToe {
  public Tool curPlayer;
  public Tool serverPlayer;
  public Tool clientPlayer;
  public Board board;
  public TicTacToeAgent agent;
  public GameMode gameMode;
  private static DataInputStream in;
  private static DataOutputStream out;
  private static Random generator = new Random();

  public TicTacToe(int agentIQ, Tool firstPlayer, GameMode gameMode) {
    this.curPlayer = firstPlayer;
    this.gameMode = gameMode;
    board = new Board();
    agent = agentCreator(agentIQ);
  }

  public void updatePlayer(Tool newPerson) {
    this.clientPlayer = newPerson;
    this.serverPlayer = this.clientPlayer == Tool.X ? Tool.O : Tool.X;
    this.curPlayer = this.clientPlayer == Tool.X ? this.clientPlayer : this.serverPlayer;
  }

//  public boolean play() {
//    board.show();
//
//    boolean gameOver = false;
//    while (!gameOver) {
//      Move move = getAMove();
//      if (move == null) {
//        return false;
//      }
//
//      board.handleMove(move, player);
//
//      board.show();
//
//      if (board.isGameWon() || board.isFull())
//        gameOver = true;
//      else
//        player = oppositePlayer();
//
//    }
//
//    showGameResult();
//    return true;
//  }
  /*
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
*/

  public boolean play() {
    if (gameMode == GameMode.PVE) {
      //return playAgainstAI();
    } else if (gameMode == GameMode.PVP) {
      return playAgainstHuman();
    } else {
        System.out.println("Invalid game mode!");
        return false;
    }

    return false;
  }

  private boolean playAgainstHuman() {
    boolean gameOver = false;
    while (!gameOver) {
      board.show();
      board.showTurn(curPlayer);

      Move move = getAMoveFromSocket(curPlayer);
      board.handleMove(move, curPlayer);

      if (board.isGameWon() || board.isFull()) {
        board.show();
        board.showResult(board.isGameWon(), curPlayer);
        return true;
      }

      curPlayer = oppositePlayer();
    }
    return true;
  }

  private Move getAMoveFromSocket(Tool player) {
    Move move = null;
    while (move == null) {
      try {
        String message = in.readUTF();
        String[] parts = message.split(" ");
        int row = Integer.parseInt(parts[0]);
        int col = Integer.parseInt(parts[1]);
        move = new Move(row, col);
        if (!board.isValid(move)) {
          out.writeUTF("Invalid move! Try again.");
          move = null;
        }
      } catch (IOException e) {
        System.out.println("Error reading input. Please try again.");
      }
    }
    return move;
  }

  private Move getAMoveFromHuman() {
    Move move = null;
    while (move == null) {
      try {
        int row = in.readInt();  // Read the row sent by the client
        int col = in.readInt();  // Read the col sent by the client
        move = new Move(row, col);
        if (!board.isValid(move)) {
          out.writeUTF("Invalid move! Try again.");  // Send a message to the client
          move = null;
        }
      } catch (IOException e) {
        System.out.println("Error reading input. Please try again.");
      }
    }
    return move;
  }



  private TicTacToeAgent agentCreator(int iQ) {
    TicTacToeAgent agent;
    switch (iQ) {
      case 0:
        agent = new RandomAgent(board);
        break;
      case 100:
        agent = new AIAssistance(board, serverPlayer, clientPlayer);
        break;
      default:
        agent = new WisdomAgent(board, serverPlayer, clientPlayer, iQ);
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
      clientPlayer = Tool.X;
      serverPlayer = Tool.O;
    } else {
      clientPlayer = Tool.O;
      serverPlayer = Tool.X;
    }
    curPlayer = Tool.X;
  }

  public Tool oppositePlayer() {
    return (curPlayer == serverPlayer) ? clientPlayer : serverPlayer;
  }

  private void showGameResult() {
      try {
          if (board.isGameWon())
            out.writeUTF(curPlayer == clientPlayer ? "You won!" : "I won!");
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

    if (player == serverPlayer) {
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

  // Creates a move, either a random generated move or as input from the user
//  private Move getAMove() {
//    Move move = null;
//
//    if (player == computer) {
//      try {
//        out.writeUTF("It is my move.  I am " + player);
//        out.flush();
//        move = agent.nextMove();
//      } catch (Exception e) {
//        e.printStackTrace();
//      }
//    } else {
//      try {
//        out.writeUTF("It is your move.  You are " + player);
//        out.flush();
//        move = getAValidMoveFromPerson();
//      } catch (Exception e) {
//        e.printStackTrace();
//      }
//    }
//
//    return move;
//  }


//  private Move getAValidMoveFromPerson() {
//    Move move = null;
//    while (true) {
//      try {
//        out.writeUTF("Enter a row and column on one line: ");
//        out.flush();
//        out.writeUTF("-1");
//        out.flush();
//        String command = in.readUTF();
//        if (command.equals("EXIT")) {
//          return null;
//        }
//        String[] m = command.split(" ");
//        move = new Move(Integer.parseInt(m[0]), Integer.parseInt(m[1]));
//        // Generates an exception if can't make a move from r and c
//
//        if (board.isValid(move))
//          return move;
//
//        out.writeUTF("Invalid move. Try again!");
//        out.flush();
//      } catch (Exception e) {
//        out.writeUTF("Input error. Try again!");
//      }
//    }
//  }

  private static final String HINT_MESSAGE = "\n" +
      "************************************************\n" +
      "Let's play Tic Tac Toe!\n" +
      "When asked for a move, enter location you want.\n" +
      "Enter the row first and then the column, both on the same line.\n" +
      "The row and column must in the range 1 .. 3\n" +
      "************************************************\n";
}
