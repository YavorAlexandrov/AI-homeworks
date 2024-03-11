package com.sliding.blocks.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class State implements Comparable<State> {
    private static final int[][] MOVES = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
    private final int[][] board;
    private final int movesCount;
    private final State parent;
    private final String previousMove;
    private int manhattanDistance;
    private int emptyTileRow;
    private int emptyTileCol;

    public State(int[][] board, int movesCount, State parent, String previousMove) {
        this.board = board;
        this.movesCount = movesCount;
        this.parent = parent;
        this.previousMove = previousMove;
        findEmptyTile();
    }

    public int getMovesCount() {
        return movesCount;
    }

    public State getParent() {
        return parent;
    }

    public String getPreviousMove() {
        return previousMove;
    }

    public int getManhattanDistance() {
        return manhattanDistance;
    }

    @Override
    public int compareTo(State other) {
        return Integer.compare(manhattanDistance + movesCount, other.manhattanDistance + other.movesCount);
    }

    public void calculateManhattanDistance(int[][] goalStates) {
        manhattanDistance = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                int currentTile = board[i][j];
                if (currentTile != 0 && (i != goalStates[currentTile][0] || j != goalStates[currentTile][1])) {
                    int rowDiff = Math.abs(i - goalStates[currentTile][0]);
                    int colDiff = Math.abs(j - goalStates[currentTile][1]);
                    manhattanDistance += rowDiff + colDiff;
                }
            }
        }
    }

    public boolean isSolved() {
        return manhattanDistance == 0;
    }

    public boolean isSolvable() {
        int inversions = getInversions();
        return isOddSizedBoardSolvable(inversions) || isEvenSizedBoardSolvable(inversions);
    }

    public List<State> getChildren(int[][] goalStates) {
        List<State> getChildren = new ArrayList<>();
        for (int[] move : MOVES) {
            int newEmptyTileRow = emptyTileRow + move[0];
            int newEmptyTileCol = emptyTileCol + move[1];

            if (newEmptyTileRow >= 0 && newEmptyTileRow < board.length
                    && newEmptyTileCol >= 0 && newEmptyTileCol < board.length) {
                int[][] newBoard = copyBoard(board);

                newBoard[emptyTileRow][emptyTileCol] = newBoard[newEmptyTileRow][newEmptyTileCol];
                newBoard[newEmptyTileRow][newEmptyTileCol] = 0;

                if (parent != null && Arrays.deepEquals(newBoard, parent.board)) {
                    continue;
                }

                State newChild = new State(newBoard, movesCount + 1, this, Arrays.toString(move));
                newChild.calculateManhattanDistance(goalStates);
                getChildren.add(newChild);
            }
        }
        return getChildren;
    }

    private void findEmptyTile() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == 0) {
                    emptyTileRow = i;
                    emptyTileCol = j;
                    return;
                }
            }
        }
    }

    private List<Integer> getFlattenedBoard() {
        List<Integer> flattenedBoard = new ArrayList<>();
        for (int[] row : board) {
            for (int col : row) {
                if (col != 0) {
                    flattenedBoard.add(col);
                }
            }
        }
        return flattenedBoard;
    }

    private int getInversions() {
        int inversions = 0;
        List<Integer> flattenedBoard = getFlattenedBoard();
        for (int i = 0; i < flattenedBoard.size(); i++) {
            for (int j = i + 1; j < flattenedBoard.size(); j++) {
                if (flattenedBoard.get(i) > flattenedBoard.get(j)) {
                    inversions++;
                }
            }
        }
        return inversions;
    }

    private boolean isOddSizedBoardSolvable(int inversions) {
        return board.length % 2 == 1 && inversions % 2 == 0;
    }

    private boolean isEvenSizedBoardSolvable(int inversions) {
        return board.length % 2 == 0 && (inversions + emptyTileRow) % 2 == 1;
    }

    private int[][] copyBoard(int[][] board) {
        int[][] copy = new int[board.length][board[0].length];
        for (int i = 0; i < board.length; i++) {
            copy[i] = Arrays.copyOf(board[i], board[i].length);
        }
        return copy;
    }

}
