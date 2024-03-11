package com.queens;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class NQueens3 {
    private final int queensCount;
    private final int maxNumberOfMoves;
    private int[] queensPositions;
    private int[] columnConflicts;
    private int[] mainDiagonalConflicts;
    private int[] secondDiagonalConflicts;
    private int movesMade;

    public NQueens3(int queensCount, int maxNumberOfMoves) {
        this.queensCount = queensCount;
        this.maxNumberOfMoves = maxNumberOfMoves;
    }

    private void initializeQueens() {
        this.queensPositions = new int[queensCount];
    }

    private void initializeConflictCollections() {
        this.columnConflicts = new int[queensCount];
        this.mainDiagonalConflicts = new int[2 * queensCount - 1];
        this.secondDiagonalConflicts = new int[2 * queensCount - 1];
    }

    private void initializeBoard() {
        long startTime = System.currentTimeMillis();
        for (int queenIndex = 0; queenIndex < queensCount; queenIndex++) {
            int columnIndex = chooseCellWithMinimalConflictsInRow(queenIndex);
            putQueen(queenIndex, columnIndex);
        }
        System.out.println("Execution time (board init): " + (System.currentTimeMillis() - startTime) / 1000.0 + " milliseconds");
    }

    private void initialize() {
        long startTime = System.currentTimeMillis();
        movesMade = 0;
        initializeQueens();
        initializeConflictCollections();
        initializeBoard();
        System.out.println("Execution time (init): " + (System.currentTimeMillis() - startTime) / 1000.0 + " milliseconds");
    }

    // places a queen in a specific cell and updates conflict counts
    private void putQueen(int queenIndex, int columnIndex) {
        queensPositions[queenIndex] = columnIndex;
        adjustConflicts(queenIndex, columnIndex, 1);
    }

    // removes a queen from a specific cell and updates conflict count
    private void removeQueen(int queenIndex, int columnIndex) {
        queensPositions[queenIndex] = columnIndex;
        adjustConflicts(queenIndex, columnIndex, -1);
    }

    // responsible for updating conflict count when a queen is placed or removed on the board
    // ensures that the conflict information is kept up to date after each move
    private void adjustConflicts(int queenIndex, int columnIndex, int step) {
        // updates the conflict count for the column where the queen is being placed or removed
        // keeps track of how many queens share the same column
        columnConflicts[columnIndex] += step;

        // updates the conflict count for the main diagonal where the queen is being placed or removed
        // keeps track of how many queens share the same column
        int mainDiagonalIndex = getMainDiagonalIndex(queenIndex, columnIndex);
        mainDiagonalConflicts[mainDiagonalIndex] += step;

        int secondDiagonalIndex = getSecondDiagonalIndex(queenIndex, columnIndex);
        secondDiagonalConflicts[secondDiagonalIndex] += step;
    }

    private int getSecondDiagonalIndex(int rowIndex, int columnIndex) {
        return rowIndex + columnIndex;
    }

    private int getMainDiagonalIndex(int rowIndex, int columnIndex) {
        // rowIndex - colIndex represents the distance of the cell from the main diagonal
        // if the result is 0 it means that the cell is on the main diagonal
        // if it is negative, the cell is below the main diagonal
        // if it is positive, the cell is above the main diagonal
        return rowIndex - columnIndex + queensCount - 1;
        // queensCount - 1 is used to ensure that the calculated index falls within a valid range
    }

    // calculates the total number of conflicts for a queen placed ot a specific cell
    private int getCellConflicts(int rowIndex, int columnIndex) {
        int mainDiagonalIndex = getMainDiagonalIndex(rowIndex, columnIndex);
        int secondDiagonalIndex = getSecondDiagonalIndex(rowIndex, columnIndex);
        return columnConflicts[columnIndex] + mainDiagonalConflicts[mainDiagonalIndex] + secondDiagonalConflicts[secondDiagonalIndex];
    }

    // calculates conflicts for each cell in a specific row
    private int[] getRowConflicts(int rowIndex) {
        int[] conflicts = new int[queensCount];
        for (int columnIndex = 0; columnIndex < queensCount; columnIndex++) {
            conflicts[columnIndex] = getCellConflicts(rowIndex, columnIndex);
        }
        return conflicts;
    }

    //get the minimum conflicts of a current row
    private List<Integer> getMinConflictsInRow(int[] collection) {
        int minEl = Integer.MAX_VALUE;
        for (int el : collection) {
            minEl = Math.min(minEl, el);
        }

        List<Integer> minIndices = new ArrayList<>();
        for (int i = 0; i < collection.length; i++) {
            if (minEl == collection[i]) {
                minIndices.add(i);
            }
        }

        return minIndices;
    }

    // chooses a cell in the current row with the minimum number of conflicts
    private int chooseCellWithMinimalConflictsInRow(int rowIndex) {
        int[] conflictsInRow = getRowConflicts(rowIndex);
        List<Integer> minimalConflictsIndices = getMinConflictsInRow(conflictsInRow);
        Random random = new Random();
        return minimalConflictsIndices.get(random.nextInt(minimalConflictsIndices.size()));
    }

    // chooses a queen with the maximum number of conflicts among all queens
    private int chooseQueenWithMaximumConflicts() {
        List<Integer> maxConflictsQueens = new ArrayList<>();
        int maxConflicts = Integer.MIN_VALUE;
        for (int queenIndex = 0; queenIndex < queensCount; queenIndex++) {
            int conflicts = getCellConflicts(queenIndex, queensPositions[queenIndex]);
            if (conflicts > maxConflicts) {
                maxConflicts = conflicts;
                maxConflictsQueens.clear();
                maxConflictsQueens.add(queenIndex);
            } else if (conflicts == maxConflicts) {
                maxConflictsQueens.add(queenIndex);
            }
        }
        Random random = new Random();
        return maxConflictsQueens.get(random.nextInt(maxConflictsQueens.size()));
    }

    private int getMaxQueensConflicts() {
        int maxConflicts = Integer.MIN_VALUE;

        for (int queenIndex = 0; queenIndex < queensCount; queenIndex++) {
            int conflicts = getCellConflicts(queenIndex, queensPositions[queenIndex]);
            maxConflicts = Math.max(maxConflicts, conflicts);
        }

        return maxConflicts;
    }

    //checks if the problem is solved, chen the maximum conflicts reach 3, meaning no queens threaten each other
    private boolean isFinished() {
        return getMaxQueensConflicts() == 3;
    }

    // iteratively resolves conflicts by moving queens to cells with fewer conflicts
    private void resolveConflicts() {
        while (movesMade < maxNumberOfMoves) {
            if (isFinished()) {
                break;
            }
            int queenToMove = chooseQueenWithMaximumConflicts();
            int newColumn = chooseCellWithMinimalConflictsInRow(queenToMove);
            removeQueen(queenToMove, queensPositions[queenToMove]);
            putQueen(queenToMove, newColumn);
            movesMade++;
        }
    }

    // initialized the board, resolves conflicts iteratively until a solution is found, and prints the final solution
    private void solveBoard() {
        long startTime = System.currentTimeMillis();
        initialize();
        int randomRestarts = 0;
        while (!isFinished()) {
            resolveConflicts();
            if (isFinished()) {
                break;
            }
            initialize();
            randomRestarts++;
        }
        System.out.println("Execution time (total): " + (System.currentTimeMillis() - startTime) / 1000.0 + " milliseconds");
        System.out.println("Random restarts: " + randomRestarts);
        System.out.println("Moves made: " + movesMade);
        printBoard();
    }

    private void printBoard() {
        if (queensCount > 10) {
            return;
        }
        for (int queen = 0; queen < queensCount; queen++) {
            char[] row = new char[queensCount];
            Arrays.fill(row, '_');
            row[queensPositions[queen]] = '*';
            System.out.println(new String(row));
        }
    }

    public static void main(String[] args) {
        NQueens3 queens = new NQueens3(10, 100);
        queens.solveBoard();
    }
}
