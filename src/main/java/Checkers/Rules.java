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
                if (forceTake(Pieces.Color.RED)) return false;
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
                if (forceTake(Pieces.Color.BLACK)) return false;
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
            if (board.getPiece(midRow, midCol) != null && board.getPiece(midRow, midCol).getColor() == enemyColor &&  board.getPiece(nRow, nCol) == null) {
                board.setPiece(midRow, midCol, null);
                board.setPiece(nRow, nCol, board.getPiece(pRow, pCol));
                board.setPiece(pRow, pCol, null);
                board.switchTurn();
                return true;
            }
            return false;
        }
        if (((nRow + nCol) % 2 != 0) && (nRow == pRow + 1 && Math.abs(nCol - pCol) == 1)) { //valid move going forward diagonally
            if (forceTake(Pieces.Color.RED)) return false;
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



        if (nRow == pRow - 2 && Math.abs(nCol - pCol) == 2) {
            int midRow = pRow - 1;
            int midCol = (pCol + nCol) / 2;
            if (board.getPiece(midRow, midCol) != null &&
                    board.getPiece(midRow, midCol).getColor() == enemyColor &&
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
            if (forceTake(Pieces.Color.BLACK)) return false;
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
                    if (row + 2 == 7) {
                        moving = new Pieces(Pieces.Color.RED, Pieces.PieceType.KING);
                        board.setPiece(row + 2, col + 2, moving);
                    }
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
                    if (row + 2 == 7) {
                        moving = new Pieces(Pieces.Color.RED, Pieces.PieceType.KING);
                        board.setPiece(row + 2, col - 2, moving);
                    }
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
                    if (row - 2 == 0) {
                        moving = new Pieces(Pieces.Color.BLACK, Pieces.PieceType.KING);
                        board.setPiece(row - 2, col + 2, moving);
                    }
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
                    if (row - 2 == 0) {
                        moving = new Pieces(Pieces.Color.BLACK, Pieces.PieceType.KING);
                        board.setPiece(row - 2, col - 2, moving);
                    }
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

    private boolean forceTake(Pieces.Color color){
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board.getPiece(row,col) != null && board.getPiece(row,col).getColor() == color) {
                    if (color == Pieces.Color.RED) { //check if the pieces of this color have to force takes
                        if (row + 2 <= 7 && col + 2 <= 7 && board.getPiece(row+1,col+1) != null&&board.getPiece(row+1,col+1).getColor() == Pieces.Color.BLACK && board.getPiece(row+2,col+2) == null) {//down right check
                            return true;
                        }
                        else if (row + 2 <= 7 && col - 2 >= 0 && board.getPiece(row+1, col-1) != null && board.getPiece(row+1, col-1).getColor() == Pieces.Color.BLACK && board.getPiece(row+2, col-2) == null) {//down left check
                            return true;
                        }
                    }
                    else if (color == Pieces.Color.BLACK) {
                        if (row - 2 >= 0 && col + 2 <= 7 && board.getPiece(row-1, col+1)!= null&& board.getPiece(row-1, col+1).getColor() ==Pieces.Color.RED && board.getPiece(row-2,col+2) == null ) {
                            return true;
                        }
                        else if(row - 2 >= 0 && col - 2 >= 0 && board.getPiece(row-1, col-1)!=null && board.getPiece(row-1, col-1).getColor() == Pieces.Color.RED && board.getPiece(row-2, col-2) == null ) {
                            return true;
                        }
                    }
                }
            }
        }
        return  false;
    }

    public ArrayList<int[]> validMoves(int row, int col, Pieces.Color color){
        ArrayList<int[]> validMovesList = new ArrayList<>();
        if (color == Pieces.Color.RED) { //case for red pieces
            if (row + 1 <= 7 && col + 1 <= 7 && board.getPiece(row+1, col+1) == null) { //single down right
                validMovesList.add(new int[]{row+1, col+1});
            }
            if (row + 1 <= 7 && col - 1 >= 0 && board.getPiece(row+1, col-1) == null){ //single down left
                validMovesList.add(new int[]{row+1, col-1});

            }
            if (row+2<=7 && col+2<=7 && board.getPiece(row+1,col+1) != null && board.getPiece(row+1,col+1).getColor() == Pieces.Color.BLACK && board.getPiece(row+2,col+2) == null) {
                validMovesList.add(new int[]{row+2, col+2}); //add double down right
            }
            if (row+2<=7 && col-2 >= 0 && board.getPiece(row+1,col-1) != null && board.getPiece(row+1,col-1).getColor() == Pieces.Color.BLACK && board.getPiece(row+2,col-2) == null) {
                validMovesList.add(new int[]{row+2, col-2}); //add double down left
            }
        }
        else if (color == Pieces.Color.BLACK) {
            if (row - 1 >= 0 && col + 1 <= 7 && board.getPiece(row-1, col+1) == null) { //single up right
                validMovesList.add(new int[]{row-1, col+1});
            }
            if (row - 1 >= 0 && col - 1  >= 0 && board.getPiece(row-1, col-1) == null){ //single up left
                validMovesList.add(new int[]{row-1, col-1});

            }
            if (row-2>=0 && col+2<=7 && board.getPiece(row-1,col+1) != null && board.getPiece(row-1,col+1).getColor() == Pieces.Color.RED && board.getPiece(row-2,col+2) == null) {
                validMovesList.add(new int[]{row-2, col+2}); //add double up right
            }
            if (row-2>=0 && col-2 >= 0 && board.getPiece(row-1,col-1) != null && board.getPiece(row-1,col-1).getColor() == Pieces.Color.RED && board.getPiece(row-2,col-2) == null) {
                validMovesList.add(new int[]{row-2, col-2}); //add double up left
            }
        }
        boolean hasJump = false; //check for forced takes
        for (int[] move : validMovesList) {
            if (Math.abs(move[0] - row) == 2) {
                hasJump = true;
                break;
            }
        }
        if (hasJump) {
            validMovesList.removeIf(move -> Math.abs(move[0] - row) != 2);
        }
        return validMovesList;
    }
}
