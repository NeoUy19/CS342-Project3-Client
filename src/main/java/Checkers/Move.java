package Checkers;

import java.io.Serializable;

public class Move implements Serializable { //Allows move objects to be sent over a network
    Pieces piece;
    int pRow,  pCol, nRow, nCol;



    public Move(Pieces piece, int pRow, int pCol, int nRow, int nCol){
        this.piece = piece;
        this.pRow = pRow;
        this.pCol = pCol;
        this.nRow = nRow;
        this.nCol = nCol;
    }
    public Pieces getPiece() {
        return piece;
    }
    public int getpRow() {
        return pRow;
    }
    public int getpCol() {
        return pCol;
    }
    public int getnRow() {
        return nRow;
    }
    public int getnCol() {
        return nCol;
    }
}

