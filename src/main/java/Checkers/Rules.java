package Checkers;

public class Rules {
    Board board;
    public Rules(Board board) {
        this.board = board;
    }

    public boolean isValidMove(Move move) { //assume non-king
        int pRow = move.getpRow();
        int pCol = move.getpCol();
        int nRow = move.getnRow();
        int nCol = move.getnCol();
        System.out.println("pRow: " + pRow + " pCol: " + pCol + " piece: " + board.getPiece(pRow, pCol));

        if (board.getCurrentMove() == Pieces.Color.RED) { //check if its reds turn
            if (board.getPiece(pRow, pCol) == null) return false;
            System.out.println("currentMove: " + board.getCurrentMove());
            System.out.println("pieceColor: " + board.getPiece(pRow, pCol).getColor());
            if (board.getPiece(pRow, pCol).getColor() != Pieces.Color.RED) { // wrong send error
                return false;
            }
            if (nRow > 7 || nCol > 7 || nRow < 0 || nCol < 0) { //out of bounds error
                return false;
            }
            if (nRow == pRow - 2 && Math.abs(nCol - pCol) == 2) {
                int midRow = pRow - 1;
                int midCol = (pCol + nCol) / 2;
                if (board.getPiece(midRow, midCol) != null &&
                        board.getPiece(midRow, midCol).getColor() == Pieces.Color.BLACK &&
                        board.getPiece(nRow, nCol) == null) {
                    board.setPiece(midRow, midCol, null);
                    board.setPiece(nRow, nCol, board.getPiece(pRow, pCol));
                    board.setPiece(pRow, pCol, null);
                    board.switchTurn();
                    return true;
                }
                return false;
            }
                if (((nRow + nCol) % 2 != 0) && (nRow == pRow - 1 && Math.abs(nCol - pCol) == 1)) { //valid move going forward diagonally
                    System.out.println("nRow: " + nRow + " nCol: " + nCol + " nPiece: " + board.getPiece(nRow, nCol));

                    if (board.getPiece(nRow, nCol) == null) { //check if the new spot is empty
                        board.setPiece(nRow, nCol, board.getPiece(pRow, pCol));
                        board.setPiece(pRow, pCol, null);
                        board.switchTurn();
                        return true;
                    } else { //new spot is not empty check if the next spot is taken if it is throw an error if it is not force a take
                        if (board.getPiece(nRow, nCol).getColor() == board.getPiece(pRow, pCol).getColor()) {//check if its a teammate or not
                            return false; //cannot take teammate error msg popup
                        }
                        else {
                            return false;
                        }
                    }
                }
            }

        if (board.getCurrentMove() == Pieces.Color.BLACK) {
            if (board.getPiece(pRow, pCol) == null) return false;
            if (board.getPiece(pRow, pCol).getColor() != Pieces.Color.BLACK) { // wrong send error
                return false;
            }
            if (nRow > 7 || nCol > 7 || nRow < 0 || nCol < 0) { //out of bounds error
                return false;
            }
            if (nRow == pRow + 2 && Math.abs(nCol - pCol) == 2) {
                int midRow = pRow + 1;
                int midCol = (pCol + nCol) / 2;
                if (board.getPiece(midRow, midCol) != null &&
                        board.getPiece(midRow, midCol).getColor() == Pieces.Color.RED &&
                        board.getPiece(nRow, nCol) == null) {
                    board.setPiece(midRow, midCol, null);
                    board.setPiece(nRow, nCol, board.getPiece(pRow, pCol));
                    board.setPiece(pRow, pCol, null);
                    board.switchTurn();
                    return true;
                }
                return false;
            }

            if (((nRow + nCol) % 2 != 0) && (nRow == pRow +1 && Math.abs(nCol - pCol) == 1)) { //valid move going forward diagonally
                if (board.getPiece(nRow, nCol) == null) { //check if the new spot is empty
                    board.setPiece(nRow, nCol, board.getPiece(pRow, pCol));
                    board.setPiece(pRow, pCol, null);
                    board.switchTurn();
                    return true;
                } else { //new spot is not empty check if the next spot is taken if it is throw an error if it is not force a take
                    if (board.getPiece(nRow, nCol).getColor() == board.getPiece(pRow, pCol).getColor()) {//check if its a teammate or not
                        return false; //cannot take teammate error msg popup
                    }
                    else  {
                        return false;
                    }
                }
            }
        }
        return false; //send an error message on clients screen
    }
    public boolean continueJumpRed(int nRow, int nCol, Pieces.Color color) {
        int row = nRow;
        int col = nCol;
        Pieces.Color curColor = color;

        if (nRow > 7 || nCol > 7 || nRow < 0 || nCol < 0){ //out of bounds
            return false;
        }
        else if(board.getPiece(row-1, col +1) != null && board.getPiece(row-1, col+1).getColor() == Pieces.Color.BLACK && board.getPiece(row-2, col+2) == null){
            return continueJumpRed(row-2, col+2, color);
        }
        else if(board.getPiece(row-1, col-1) != null && board.getPiece(row-1, col-1).getColor() == Pieces.Color.BLACK && board.getPiece(row-2, col-2) == null){
            return continueJumpRed(row-2, col-2, color);
        }
        else {
            return false;
        }
    }

    public boolean continueJumpBlack(int nRow, int nCol, Pieces.Color color) {
        int row = nRow;
        int col = nCol;
        Pieces.Color curColor = color;

        if (nRow > 7 || nCol > 7 || nRow < 0 || nCol < 0){ //out of bounds
            return false;
        }
        else if(board.getPiece(row+1, col +1) != null && board.getPiece(row+1, col+1).getColor() == Pieces.Color.RED && board.getPiece(row+2, col+2) == null){
            return continueJumpBlack(row+2, col+2, color);
        }
        else if(board.getPiece(row+1, col-1) != null && board.getPiece(row+1, col-1).getColor() == Pieces.Color.RED && board.getPiece(row+2, col-2) == null){
            return continueJumpBlack(row+2, col-2, color);

        }
        else {
            return false;
        }
    }



}
