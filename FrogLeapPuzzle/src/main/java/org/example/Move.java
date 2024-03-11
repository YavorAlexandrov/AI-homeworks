package org.example;

import java.util.List;

public class Move {
    private final char[] board;
    private final int zeroState;
    private List<Move> children;
    private Move parent;

    public Move(char[] board, int zeroState, List<Move> children, Move parent) {
        this.board = board;
        this.zeroState = zeroState;
        this.children = children;
        this.parent = parent;
    }

    public Move(char[] board, int zeroState) {
        this.board = board;
        this.zeroState = zeroState;
    }

    public char[] getBoard() {
        return board;
    }

    public int getZeroState() {
        return zeroState;
    }

    public List<Move> getChildren() {
        return children;
    }

    public Move getParent() {
        return parent;
    }
}
