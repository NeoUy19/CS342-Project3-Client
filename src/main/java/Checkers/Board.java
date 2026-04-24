package Checkers;


public class Board {
    private Pieces[][] board = new Pieces[8][8];

    private Pieces.Color currentMove = Pieces.Color.RED;

    public Board(Pieces[][] tempBoard) {
        this.board = tempBoard;
    }
    public Board() {
        this.setBoard();
    }
    private void setBoard() { //fill the board
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (row <= 2) {
                    if ((row + col) % 2 != 0) {
                        board[row][col] = new Pieces(Pieces.Color.RED);
                    } else {
                        board[row][col] = null;
                    }
                }
                if (row >= 5) {
                    if ((row + col) % 2 != 0) {
                        board[row][col] = new Pieces(Pieces.Color.BLACK);
                    } else {
                        board[row][col] = null;
                    }
                }
            }
        }
    }

    public Pieces getPiece(int row, int col) {
        return board[row][col];
    }

    public void setPiece(int row, int col, Pieces piece) {
        board[row][col] = piece;
    }

    public Pieces.Color getCurrentMove() {
        return currentMove;
    }

    public void switchTurn() {
        if (this.getCurrentMove() == Pieces.Color.RED) {
            this.currentMove = Pieces.Color.BLACK;
        } else if (this.getCurrentMove() == Pieces.Color.BLACK) {
            this.currentMove = Pieces.Color.RED;
        }
    }
}

