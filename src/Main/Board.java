package Main;

/**
   A class for storing, manipulating, and printing TicTacToe boards
   Add a trialToWin method on May 19, 2017
   v2.1: Add a trialDoubleTreats method on May 23, 2017
   v2.8: ...
*/

import java.io.PrintWriter;
import java.util.ArrayList;

import static Main.Tool.EMPTY;

public class Board {
  public static final int SIZE = 3;
  private Tool[][] board;
  private ArrayList<Move> moves;
  private static PrintWriter out;

  public Board(PrintWriter out) {
    this.out = out;
    board = new Tool[SIZE][SIZE];
    clear();
    moves = new ArrayList<Move>();
  }

  private void clear() {
    for (int i = 0; i < SIZE; i++)
      for (int j = 0; j < SIZE; j++)
        board[i][j] = EMPTY;
  }

  public void show() {
    out.println("Here is the current board:");
    out.println();

    for (int r = 1; r <= SIZE; r++) {
      for (int c = 1; c <= SIZE; c++) {
        out.print(board[r - 1][c - 1]);

        if (c != SIZE) // Print strut after all but last column
          out.print("|");
      }

      out.println();

      if (r != SIZE) // Print row line after all but last row
        out.println("-+-+-");

    }

    out.println();
  }

  public Tool getToolAt(int row, int col) {
    return board[row][col];
  }

  public ArrayList<Move> getMoves() {
    return moves;
  }

  public boolean isGameWon() {
    final Tool[][] b = board; // a local variable for shorter expressions

    // Check (short circuit) all rows, columns and diagonals for a win
    return b[0][0] != EMPTY && b[0][0] == b[0][1] && b[0][1] == b[0][2] || // Row 0
        b[1][0] != EMPTY && b[1][0] == b[1][1] && b[1][1] == b[1][2] || // Row 1
        b[2][0] != EMPTY && b[2][0] == b[2][1] && b[2][1] == b[2][2] || // Row 2

        b[0][0] != EMPTY && b[0][0] == b[1][0] && b[1][0] == b[2][0] || // Col 0
        b[0][1] != EMPTY && b[0][1] == b[1][1] && b[1][1] == b[2][1] || // Col 1
        b[0][2] != EMPTY && b[0][2] == b[1][2] && b[1][2] == b[2][2] || // Col 2

        b[1][1] != EMPTY && b[0][0] == b[1][1] && b[1][1] == b[2][2] || // Dia 1
        b[1][1] != EMPTY && b[2][0] == b[1][1] && b[1][1] == b[0][2]; // Dia 2
  }

  public Tool getWinner() {
    final Tool[][] b = board;  // a local variable for shorter expressions

    if (b[0][0]!= EMPTY && b[0][0]==b[0][1] && b[0][1]==b[0][2]) return b[0][0]; // Row 0
    if (b[1][0]!= EMPTY && b[1][0]==b[1][1] && b[1][1]==b[1][2]) return b[1][0]; // Row 1
    if (b[2][0]!= EMPTY && b[2][0]==b[2][1] && b[2][1]==b[2][2]) return b[2][0]; // Row 2

    if (b[0][0]!= EMPTY && b[0][0]==b[1][0] && b[1][0]==b[2][0]) return b[0][0]; // Col 0
    if (b[0][1]!= EMPTY && b[0][1]==b[1][1] && b[1][1]==b[2][1]) return b[0][1]; // Col 1
    if (b[0][2]!= EMPTY && b[0][2]==b[1][2] && b[1][2]==b[2][2]) return b[0][2]; // Col 2

    if (b[1][1]!= EMPTY && b[0][0]==b[1][1] && b[1][1]==b[2][2]) return b[1][1]; // Dia 1
    if (b[1][1]!= EMPTY && b[2][0]==b[1][1] && b[1][1]==b[0][2]) return b[1][1]; // Dia 2

    return EMPTY;
  }

  public boolean isFull() {
    for (int i = 0; i < SIZE; i++)
      for (int j = 0; j < SIZE; j++)
        if (board[i][j] == EMPTY)
          return false;

    return true;
  }

  public ArrayList<Move> getEmptyMoves() {
    ArrayList<Move> emptyMoves = new ArrayList<>();
    for (int i = 0; i < SIZE; i++)
      for (int j = 0; j < SIZE; j++)
        if (board[i][j] == EMPTY)
          emptyMoves.add(new Move(i + 1, j + 1));

    return emptyMoves;
  }

  public boolean isValid(Move move) {
    int r = move.getRow();
    int c = move.getColumn();
    return board[r - 1][c - 1] == EMPTY;
  }

  public void handleMove(Move move, Tool player) {
    int r = move.getRow();
    int c = move.getColumn();

    out.println();
    out.println("The move for " + player + " is " + r + ", " + c);

    board[r - 1][c - 1] = player; // Place the player's mark on the board
    moves.add(move);
  }

  public void setMove(Move move, Tool player) {
    board[move.getRow() - 1][move.getColumn() - 1] = player;
    moves.add(move);
  }

  public void clearMove(Move move) {
    board[move.getRow() - 1][move.getColumn() - 1] = EMPTY;
    moves.remove(moves.size() - 1);
  }

  public boolean trialToWin(Move move, Tool player) {
    if (!isValid(move))
      return false;

    int r = move.getRow();
    int c = move.getColumn();

    board[r - 1][c - 1] = player; // just a trial move
    boolean result = isGameWon();
    board[r - 1][c - 1] = EMPTY; // clear the trial move

    return result;
  }

  public boolean trialDoubleTreats(Move move, Tool player) {
    if (!isValid(move))
      return false;

    int r = move.getRow();
    int c = move.getColumn();

    board[r - 1][c - 1] = player; // the trial move

    boolean singleThreat = false;
    boolean doubleThreat = false;
    for (int i = 0; i < SIZE && !doubleThreat; i++) {
      for (int j = 0; j < SIZE; j++) {
        if (board[i][j] == EMPTY) {
          board[i][j] = player; // the next trial move
          if (isGameWon()) {
            if (!singleThreat) {
              singleThreat = true;
            } else {
              doubleThreat = true;
              board[i][j] = EMPTY; // clear the next trial move
              break; // break the inner for loop
            }
          }
          board[i][j] = EMPTY; // clear the next trial move
        }
      }
    }

    board[r - 1][c - 1] = EMPTY; // clear the trial move

    return doubleThreat;
  }
}
