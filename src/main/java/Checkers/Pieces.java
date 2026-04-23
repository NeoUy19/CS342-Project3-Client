package Checkers;

import java.io.Serializable;

public class Pieces implements Serializable {

    public enum Color{BLACK,RED}
    public enum PieceType{NORMAL, KING}
    private Color color;
    private PieceType pieceType;
    public Pieces(Color color) {
        this.color = color;
        this.pieceType = PieceType.NORMAL;
    }
    public Pieces(Color color, PieceType pieceType) {
        this.color = color;
        this.pieceType = pieceType;
    }
    public void setColor(Color color) {
        this.color = color;
    }
    public void setPieceType(PieceType pieceType) {
        this.pieceType = pieceType;
    }

    public PieceType getPieceType() {
        return pieceType;
    }

    public Color getColor() {
        return this.color;
    }

}
