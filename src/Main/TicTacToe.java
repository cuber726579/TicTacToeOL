/**
   A class playing a game of TicTacToe
*/
package Main;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class TicTacToe {
  Tool player; // 当前需要落子的用户
  Tool server;
  Tool client;
  
  Board board;


  public TicTacToeAgent agent;

  public TicTacToe (int agentIQ, Tool user, DataInputStream in, DataOutputStream out) {
    client = user;
    server = (user == Tool.O) ? Tool.X : Tool.O;

    player = Tool.X;
  
    board = new Board();
    agent = agentCreator(agentIQ);

    while(!board.isFull() && !board.isGameWon()) {
      if (player == client){
        System.out.println("Listening!");
        int fromOpponent;

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
        handleMove(opponentMove, client);

        Move serverMove = agent.nextMove();
        handleMove(serverMove, server);

        row = serverMove.getRow();
        col = serverMove.getColumn();
        System.out.println(row + "," +col);
        try {
          out.writeInt((row-1) * 3 + col);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      } else {
        Move serverMove = agent.nextMove();
        handleMove(serverMove, server);

        int row = serverMove.getRow();
        int col = serverMove.getColumn();
        System.out.println(row + "," +col);
        try {
          out.writeInt((row-1) * 3 + col);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }

  public TicTacToe (int agentIQ , Tool user) {
    client = Tool.O;
    server = Tool.X;
    player = Tool.O;

    board = new Board();
    agent = agentCreator( agentIQ);
  }

//  public TicTacToe () {
//    this( 100);
//  }

  public void handleMove(Move move, Tool user) {
    this.board.handleMove(move,user);
    if (!board.isGameWon() && !board.isFull()) {
      player = oppositePlayer();
    }
  }

  public boolean isValid(Move move) {
    return board.isValid(move);
  }

//  public void play () {
//    board.show();
//
//    boolean gameOver = false;
//    while (!gameOver) {
//       Move move = getAMove();
//       board.handleMove( move, player);
//
//       board.show();
//
//       if (board.isGameWon() || board.isFull())
//          gameOver = true;
//       else
//          player = oppositePlayer();
//    }
//
//    showGameResult();
//  }

  private TicTacToeAgent agentCreator (int iQ) {
    TicTacToeAgent agent;
    switch (iQ) {
      case 0   :  agent = new RandomAgent( board); break;
      case 100 :  agent = new AIAssistance( board, server, client); break;
      default  :  agent = new WisdomAgent( board, server, client, iQ); break;
    }
    return agent;
  }

  Tool oppositePlayer() {
    return (player == server) ? client : server;
  }
  
//  private void showGameResult () {
//    if (board.isGameWon())
//      System.out.println( player== client ? "You won!" : "I won!" );
//    else if (board.isFull())
//      System.out.println( "We tied!" );
//    else
//      System.out.println( "Something went wrong!" );
//    System.out.println( board.getMoves());
//  }
  
  // Creates a move, either a random generated move or as input from the user
//  private Move getAMove () {
//    Move move = null;
//
//    if (player == server) {
//       System.out.println( "[TicTacToe LOG] It is my move. I am " + player );
//       move = agent.nextMove();
//    } else {
//       System.out.println( "[TicTacToe LOG] It is your move. You are " + player );
//       move = getAValidMoveFromPerson();
//    }
//    return move;
//  }

  Move inputAMoveFromGUI(Integer row, Integer col, Tool player) {
    Move move = null;

    if (player == server) {
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

//  public void updatePlayer(Tool newPerson) {
//    this.client = newPerson;
//    this.server = this.client == Tool.X ? Tool.O : Tool.X;
//    this.player = this.client == Tool.X ? this.client : this.server;
//  }


//  private Move getAValidMoveFromPerson () {
//    Move move = null;
//    while (true) {
//      try {
//        System.out.print( "Enter a row and column on one line: " );
//        move = new Move( in.nextInt(), in.nextInt() );
//        // Generates an exception if can't make a move from r and c
//
//        if (board.isValid( move )) return move;
//
//        System.out.println( "Invalid move. Try again!" );
//      } catch (Exception e) {
//        System.out.println( "Input error. Try again!" );
//      }
//    }
//  }

  private static Scanner in = new Scanner( System.in );
  private static Random generator = new Random();
  
  private static final String HINT_MESSAGE = "\n" +
    "************************************************\n" +
    "Let's play Tic Tac Toe!\n" +
    "When asked for a move, enter location you want.\n" +
    "Enter the row first and then the column, both on the same line.\n" +
    "The row and column must in the range 1 .. 3\n" +
    "************************************************\n" ;
}
