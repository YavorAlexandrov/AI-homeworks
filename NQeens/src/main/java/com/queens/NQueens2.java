package com.queens;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

public class NQueens2 {

    private int queensCount;
    private int maxNumberOfMoves;
    private int[] queensPositions;
    private int[] columnConflicts;
    private int[] mainDiagonalConflicts;
    private int[] secondDiagonalConflicts;
    private int movesMade;

    public NQueens2(int queensCount, int maxNumberOfMoves) {
        this.queensCount = queensCount;
        this.maxNumberOfMoves = maxNumberOfMoves;
        this.queensPositions = new int[queensCount];
        this.columnConflicts = new int[queensCount];
        this.mainDiagonalConflicts = new int[2 * queensCount - 1];
        this.secondDiagonalConflicts = new int[2 * queensCount - 1];
    }

    private void initializeQueens() {
        Arrays.fill(queensPositions, 0);
    }

    private void initializeConflictCollections() {
        Arrays.fill(columnConflicts, 0);
        Arrays.fill(mainDiagonalConflicts, 0);
        Arrays.fill(secondDiagonalConflicts, 0);
    }

    private void initializeBoard() {
        long startTime = System.currentTimeMillis();
        for (int queenIndex = 0; queenIndex < queensCount; queenIndex++) {
            int columnIndex = chooseCellWithMinimalConflictsInRow(queenIndex);
            putQueen(queenIndex, columnIndex);
        }
        System.out.println("Execution time (board init): " + (System.currentTimeMillis() - startTime) + " milliseconds");
    }

    private void initialize() {
        long startTime = System.currentTimeMillis();
        movesMade = 0;
        initializeQueens();
        initializeConflictCollections();
        initializeBoard();
        System.out.println("Execution time (init): " + (System.currentTimeMillis() - startTime) + " milliseconds");
    }

    private void putQueen(int rowIndex, int columnIndex) {
        queensPositions[rowIndex] = columnIndex;
        adjustConflicts(rowIndex, columnIndex, 1);
    }

    private void removeQueen(int rowIndex, int columnIndex) {
        queensPositions[rowIndex] = columnIndex;
        adjustConflicts(rowIndex, columnIndex, -1);
    }

    private void adjustConflicts(int rowIndex, int columnIndex, int step) {
        columnConflicts[columnIndex] += step;
        int mainDiagonalIndex = getMainDiagonalIndex(rowIndex, columnIndex);
        mainDiagonalConflicts[mainDiagonalIndex] += step;
        int secondDiagonalIndex = getSecondDiagonalIndex(rowIndex, columnIndex);
        secondDiagonalConflicts[secondDiagonalIndex] += step;
    }

    private int getCellConflicts(int rowIndex, int columnIndex) {
        int mainDiagonalIndex = getMainDiagonalIndex(rowIndex, columnIndex);
        int secondDiagonalIndex = getSecondDiagonalIndex(rowIndex, columnIndex);
        return columnConflicts[columnIndex] + mainDiagonalConflicts[mainDiagonalIndex] + secondDiagonalConflicts[secondDiagonalIndex];
    }

    private int[] getRowConflicts(int rowIndex) {
        int[] conflicts = new int[queensCount];
        for (int columnIndex = 0; columnIndex < queensCount; columnIndex++) {
            conflicts[columnIndex] = getCellConflicts(rowIndex, columnIndex);
        }
        return conflicts;
    }

    private int[] findMinimalConflictsInRow(int[] collection) {
        int minimalElement = Arrays.stream(collection).min().orElse(Integer.MAX_VALUE);
        return Arrays.stream(collection)
                .filter(element -> element == minimalElement)
                .toArray();
    }

    private int chooseCellWithMinimalConflictsInRow(int rowIndex) {
        int[] conflictsInRow = getRowConflicts(rowIndex);
        int[] minimalConflictsIndices = findMinimalConflictsInRow(conflictsInRow);
        Random random = new Random();
        return minimalConflictsIndices[random.nextInt(minimalConflictsIndices.length)];
    }

    private int getMainDiagonalIndex(int rowIndex, int columnIndex) {
        return rowIndex - columnIndex + queensCount - 1;
        //return rowIndex - columnIndex;
    }

    private int getSecondDiagonalIndex(int rowIndex, int columnIndex) {
        return rowIndex + columnIndex;
    }

    private int chooseQueenWithMaximumConflicts() {
        int[] queensConflicts = new int[queensCount];
        for (int queenIndex = 0; queenIndex < queensCount; queenIndex++) {
            queensConflicts[queenIndex] = getCellConflicts(queenIndex, queensPositions[queenIndex]);
        }
        int maximumConflicts = Arrays.stream(queensConflicts).max().orElse(0);
        int[] maxConflictQueens = IntStream.range(0, queensCount)
                .filter(queenIndex -> queensConflicts[queenIndex] == maximumConflicts)
                .toArray();
        Random random = new Random();
        return maxConflictQueens[random.nextInt(maxConflictQueens.length)];
    }

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
        System.out.println("Execution time (total): " + (System.currentTimeMillis() - startTime) + " milliseconds");
        System.out.println("Random restarts: " + randomRestarts);
        System.out.println("Moves made: " + movesMade);
        printBoard();
    }

    private boolean isFinished() {
        return Arrays.stream(queensPositions).max().orElse(0) == 3;
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
        //NQueens queens = new NQueens(10000, 100);
        NQueens2 queens = new NQueens2(4, 100);
        queens.solveBoard();
    }
}