package Checkers;

import java.util.ArrayList;

public class Rules {
    Board board;
    public Rules(Board board) {
        this.board = board;
    }

    public boolean isValidMove(Move move) { // non-king
        int pRow = move.getpRow();
        int pCol = move.getpCol();
        int nRow = move.getnRow();
        int nCol = move.getnCol();

        if (board.getPiece(pRow, pCol) != null && board.getPiece(pRow, pCol).getPieceType() == Pieces.PieceType.KING) { //check if the piece is a king
            return isValidKingMove(move);
        }
        if (board.getCurrentMove() == Pieces.Color.RED) { //check if its reds turn
            if (board.getPiece(pRow, pCol) == null) return false;
            if (board.getPiece(pRow, pCol).getColor() != Pieces.Color.RED) { // wrong send error
                return false;
            }
            if (nRow > 7 || nCol > 7 || nRow < 0 || nCol < 0) { //out of bounds error
                return false;
            }
            if (nRow == pRow + 2 && Math.abs(nCol - pCol) == 2) { //takes
                int midRow = pRow + 1;
                int midCol = (pCol + nCol) / 2;
                if (board.getPiece(midRow, midCol) != null &&
                        board.getPiece(midRow, midCol).getColor() == Pieces.Color.BLACK &&
                        board.getPiece(nRow, nCol) == null) {
                    board.setPiece(midRow, midCol, null);
                    board.setPiece(nRow, nCol, board.getPiece(pRow, pCol));
                    board.setPiece(pRow, pCol, null);
                    if (nRow == 7){
                        board.getPiece(nRow, nCol).setPieceType(Pieces.PieceType.KING);
                    }
                    board.switchTurn();
                    return true;
                }
                return false;
            }
            if (((nRow + nCol) % 2 != 0) && (nRow == pRow + 1 && Math.abs(nCol - pCol) == 1)) { //valid move going forward diagonally
                if (board.getPiece(nRow, nCol) == null) { //check if the new spot is empty
                    board.setPiece(nRow, nCol, board.getPiece(pRow, pCol));
                    board.setPiece(pRow, pCol, null);
                    if (nRow == 7){
                        board.getPiece(nRow, nCol).setPieceType(Pieces.PieceType.KING);
                    }
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
            if (nRow == pRow - 2 && Math.abs(nCol - pCol) == 2) {
                int midRow = pRow - 1;
                int midCol = (pCol + nCol) / 2;
                if (board.getPiece(midRow, midCol) != null &&
                        board.getPiece(midRow, midCol).getColor() == Pieces.Color.RED &&
                        board.getPiece(nRow, nCol) == null) {
                    board.setPiece(midRow, midCol, null);
                    board.setPiece(nRow, nCol, board.getPiece(pRow, pCol));
                    board.setPiece(pRow, pCol, null);
                    if (nRow == 0){
                        board.getPiece(nRow, nCol).setPieceType(Pieces.PieceType.KING);
                    }
                    board.switchTurn();
                    return true;
                }
                return false;
            }

            if (((nRow + nCol) % 2 != 0) && (nRow == pRow - 1 && Math.abs(nCol - pCol) == 1)) { //valid move going forward diagonally
                if (board.getPiece(nRow, nCol) == null) { //check if the new spot is empty
                    board.setPiece(nRow, nCol, board.getPiece(pRow, pCol));
                    board.setPiece(pRow, pCol, null);
                    if (nRow == 0){
                        board.getPiece(nRow, nCol).setPieceType(Pieces.PieceType.KING);
                    }
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

    public boolean isValidKingMove(Move move) {
        int pRow = move.getpRow();
        int pCol = move.getpCol();
        int nRow = move.getnRow();
        int nCol = move.getnCol();

        Pieces.Color enemyColor = null;
        if (board.getCurrentMove() != board.getPiece(pRow, pCol).getColor()) return false;
        if (board.getPiece(pRow, pCol).getColor() == Pieces.Color.RED) {
            enemyColor = Pieces.Color.BLACK;
        }
        else if (board.getPiece(pRow,pCol).getColor() == Pieces.Color.BLACK){
            enemyColor = Pieces.Color.RED;
        }

            if (nRow > 7 || nCol > 7 || nRow < 0 || nCol < 0) { //out of bounds error
                return false;
            }
            if (nRow == pRow + 2 && Math.abs(nCol - pCol) == 2) { //takes
                int midRow = pRow + 1;
                int midCol = (pCol + nCol) / 2;
                if (board.getPiece(midRow, midCol) != null &&
                        board.getPiece(midRow, midCol).getColor() == enemyColor &&
                        board.getPiece(nRow, nCol) == null) {
                    board.setPiece(midRow, midCol, null);
                    board.setPiece(nRow, nCol, board.getPiece(pRow, pCol));
                    board.setPiece(pRow, pCol, null);
                    if (nRow == 7){
                        board.getPiece(nRow, nCol).setPieceType(Pieces.PieceType.KING);
                    }
                    board.switchTurn();
                    return true;
                }
                return false;
            }
            if (((nRow + nCol) % 2 != 0) && (nRow == pRow + 1 && Math.abs(nCol - pCol) == 1)) { //valid move going forward diagonally
                if (board.getPiece(nRow, nCol) == null) { //check if the new spot is empty
                    board.setPiece(nRow, nCol, board.getPiece(pRow, pCol));
                    board.setPiece(pRow, pCol, null);
                    if (nRow == 7){
                        board.getPiece(nRow, nCol).setPieceType(Pieces.PieceType.KING);
                    }
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



            if (nRow == pRow - 2 && Math.abs(nCol - pCol) == 2) {
                int midRow = pRow - 1;
                int midCol = (pCol + nCol) / 2;
                if (board.getPiece(midRow, midCol) != null &&
                        board.getPiece(midRow, midCol).getColor() == enemyColor &&
                        board.getPiece(nRow, nCol) == null) {
                    board.setPiece(midRow, midCol, null);
                    board.setPiece(nRow, nCol, board.getPiece(pRow, pCol));
                    board.setPiece(pRow, pCol, null);
                    if (nRow == 0){
                        board.getPiece(nRow, nCol).setPieceType(Pieces.PieceType.KING);
                    }
                    board.switchTurn();
                    return true;
                }
                return false;
            }

            if (((nRow + nCol) % 2 != 0) && (nRow == pRow - 1 && Math.abs(nCol - pCol) == 1)) { //valid move going forward diagonally
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
        return false; //send an error message on clients screen
    }
    public boolean continueJumpRed(int row, int col, Pieces.Color color) {
        if (row > 7 || col > 7 || row < 0 || col < 0) return false;
        if (row + 1 <= 7 && col + 1 <= 7 && row + 2 <= 7 && col + 2 <= 7) {
            Pieces mid = board.getPiece(row + 1, col + 1);
            if (mid != null && mid.getColor() == Pieces.Color.BLACK && board.getPiece(row + 2, col + 2) == null) {
                board.setPiece(row + 1, col + 1, null);
                board.setPiece(row + 2, col + 2, board.getPiece(row, col));
                board.setPiece(row, col, null);
                continueJumpRed(row + 2, col + 2, color);
                return true;
            }
        }
        if (row + 1 <= 7 && col - 1 >= 0 && row + 2 <= 7 && col - 2 >= 0) {
            Pieces mid = board.getPiece(row + 1, col - 1);
            if (mid != null && mid.getColor() == Pieces.Color.BLACK && board.getPiece(row + 2, col - 2) == null) {
                board.setPiece(row + 1, col - 1, null);
                board.setPiece(row + 2, col - 2, board.getPiece(row, col));
                board.setPiece(row, col, null);
                continueJumpRed(row + 2, col - 2, color);
                return true;
            }
        }
        return false;
    }

    public boolean continueJumpBlack(int row, int col, Pieces.Color color) {
        if (row > 7 || col > 7 || row < 0 || col < 0) return false;
        if (row - 1 >= 0 && col + 1 <= 7 && row - 2 >= 0 && col + 2 <= 7) {
            Pieces mid = board.getPiece(row - 1, col + 1);
            if (mid != null && mid.getColor() == Pieces.Color.RED && board.getPiece(row - 2, col + 2) == null) {
                board.setPiece(row - 1, col + 1, null);
                board.setPiece(row - 2, col + 2, board.getPiece(row, col));
                board.setPiece(row, col, null);
                continueJumpBlack(row - 2, col + 2, color);
                return true;
            }
        }
        if (row - 1 >= 0 && col - 1 >= 0 && row - 2 >= 0 && col - 2 >= 0) {
            Pieces mid = board.getPiece(row - 1, col - 1);
            if (mid != null && mid.getColor() == Pieces.Color.RED && board.getPiece(row - 2, col - 2) == null) {
                board.setPiece(row - 1, col - 1, null);
                board.setPiece(row - 2, col - 2, board.getPiece(row, col));
                board.setPiece(row, col, null);
                continueJumpBlack(row - 2, col - 2, color);
                return true;
            }
        }
        return false;
    }

    public ArrayList<Move> getMultiJumps(int row, int col, Pieces.Color color) {
        ArrayList<Move> jumps = new ArrayList<>();

        if (color == Pieces.Color.RED) {
            // Check down-right
            if (row + 2 <= 7 && col + 2 <= 7) {
                Pieces mid = board.getPiece(row + 1, col + 1);
                if (mid != null && mid.getColor() == Pieces.Color.BLACK && board.getPiece(row + 2, col + 2) == null) {
                    Pieces moving = board.getPiece(row, col);
                    board.setPiece(row + 1, col + 1, null);
                    board.setPiece(row + 2, col + 2, moving);
                    board.setPiece(row, col, null);
                    jumps.add(new Move(moving, row, col, row + 2, col + 2));
                    jumps.addAll(getMultiJumps(row + 2, col + 2, color));
                    return jumps;
                }
            }
            // Check down-left
            if (row + 2 <= 7 && col - 2 >= 0) {
                Pieces mid = board.getPiece(row + 1, col - 1);
                if (mid != null && mid.getColor() == Pieces.Color.BLACK && board.getPiece(row + 2, col - 2) == null) {
                    Pieces moving = board.getPiece(row, col);
                    board.setPiece(row + 1, col - 1, null);
                    board.setPiece(row + 2, col - 2, moving);
                    board.setPiece(row, col, null);
                    jumps.add(new Move(moving, row, col, row + 2, col - 2));
                    jumps.addAll(getMultiJumps(row + 2, col - 2, color));
                    return jumps;
                }
            }
        } else {
            // Check up-right
            if (row - 2 >= 0 && col + 2 <= 7) {
                Pieces mid = board.getPiece(row - 1, col + 1);
                if (mid != null && mid.getColor() == Pieces.Color.RED && board.getPiece(row - 2, col + 2) == null) {
                    Pieces moving = board.getPiece(row, col);
                    board.setPiece(row - 1, col + 1, null);
                    board.setPiece(row - 2, col + 2, moving);
                    board.setPiece(row, col, null);
                    jumps.add(new Move(moving, row, col, row - 2, col + 2));
                    jumps.addAll(getMultiJumps(row - 2, col + 2, color));
                    return jumps;
                }
            }
            // Check up-left
            if (row - 2 >= 0 && col - 2 >= 0) {
                Pieces mid = board.getPiece(row - 1, col - 1);
                if (mid != null && mid.getColor() == Pieces.Color.RED && board.getPiece(row - 2, col - 2) == null) {
                    Pieces moving = board.getPiece(row, col);
                    board.setPiece(row - 1, col - 1, null);
                    board.setPiece(row - 2, col - 2, moving);
                    board.setPiece(row, col, null);
                    jumps.add(new Move(moving, row, col, row - 2, col - 2));
                    jumps.addAll(getMultiJumps(row - 2, col - 2, color));
                    return jumps;
                }
            }
        }
        return jumps;
    }

    public Pieces.Color checkForWinner(){
        int numRed = 0;
        int numBlack = 0;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board.getPiece(row,col) != null && board.getPiece(row, col).getColor() == Pieces.Color.RED) {
                    numRed++;
                }
                else if (board.getPiece(row,col) != null && board.getPiece(row, col).getColor() == Pieces.Color.BLACK) {
                    numBlack++;
                }
            }
        }
        if (numRed == 0) return Pieces.Color.BLACK;
        if (numBlack == 0) return Pieces.Color.RED;
        return null;
    }
}
