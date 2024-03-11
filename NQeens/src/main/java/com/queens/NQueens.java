package com.queens;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class NQueens {
    private final int numberOfQueens;
    private final int maxNumberOfMoves;
    private int[] positionsOfQueens;
    private int[] conflictsInColumn;
    private int[] conflictsInMainDiagonal;
    private int[] conflictsInSecondDiagonal;
    private int numberOfMoves;

    public NQueens(int numberOfQueens, int maxNumberOfMoves) {
        this.numberOfQueens = numberOfQueens;
        this.maxNumberOfMoves = maxNumberOfMoves;
    }

    private void initialize() {
        numberOfMoves = 0;
        positionsOfQueens = new int[numberOfQueens];
        conflictsInColumn = new int[numberOfQueens];
        conflictsInMainDiagonal = new int[2 * numberOfQueens - 1];
        conflictsInSecondDiagonal = new int[2 * numberOfQueens - 1];
        for (int i = 0; i < numberOfQueens; i++) {
            int col = getPositionWithMinConflictsInRow(i);
            placeQueenAtPosition(i, col);
        }
    }

    private void placeQueenAtPosition(int row, int col) {
        positionsOfQueens[row] = col;
        updateConflicts(row, col, 1);
    }

    private void removeQueenFromPosition(int row, int col) {
        positionsOfQueens[row] = col;
        updateConflicts(row, col, -1);
    }

    private int getSecondDiagonal(int row, int col) {
        return row + col;
    }

    private int getMainDiagonal(int row, int col) {
        return row - col + numberOfQueens - 1;
    }

    private void updateConflicts(int row, int col, int conflictUpdate) {
        conflictsInColumn[col] += conflictUpdate;

        int mainDiagonal = getMainDiagonal(row, col);
        conflictsInMainDiagonal[mainDiagonal] += conflictUpdate;

        int secondDiagonal = getSecondDiagonal(row, col);
        conflictsInSecondDiagonal[secondDiagonal] += conflictUpdate;
    }

    private int getConflictsInPosition(int row, int col) {
        int mainDiagonal = getMainDiagonal(row, col);
        int secondDiagonal = getSecondDiagonal(row, col);
        return conflictsInColumn[col] + conflictsInMainDiagonal[mainDiagonal] + conflictsInSecondDiagonal[secondDiagonal];
    }

    private int[] getConflictsInRow(int row) {
        int[] conflicts = new int[numberOfQueens];
        for (int i = 0; i < numberOfQueens; i++) {
            conflicts[i] = getConflictsInPosition(row, i);
        }
        return conflicts;
    }

    private List<Integer> getMinConflictsInRow(int[] row) {
        int minElement = Integer.MAX_VALUE;
        for (int element : row) {
            minElement = Math.min(minElement, element);
        }

        List<Integer> minConflictsInRow = new ArrayList<>();
        for (int i = 0; i < row.length; i++) {
            if (minElement == row[i]) {
                minConflictsInRow.add(i);
            }
        }

        return minConflictsInRow;
    }

    private int getPositionWithMinConflictsInRow(int row) {
        int[] conflictsInRow = getConflictsInRow(row);
        List<Integer> minConflictsInRow = getMinConflictsInRow(conflictsInRow);
        Random random = new Random();
        return minConflictsInRow.get(random.nextInt(minConflictsInRow.size()));
    }

    private int getPositionWithMaxConflicts() {
        List<Integer> maxConflictsPositions = new ArrayList<>();
        int maxConflicts = Integer.MIN_VALUE;
        for (int i = 0; i < numberOfQueens; i++) {
            int conflicts = getConflictsInPosition(i, positionsOfQueens[i]);
            if (conflicts > maxConflicts) {
                maxConflicts = conflicts;
                maxConflictsPositions.clear();
                maxConflictsPositions.add(i);
            } else if (conflicts == maxConflicts) {
                maxConflictsPositions.add(i);
            }
        }
        Random random = new Random();
        return maxConflictsPositions.get(random.nextInt(maxConflictsPositions.size()));
    }

    private int getMaxNumberOfConflicts() {
        int maxConflicts = Integer.MIN_VALUE;

        for (int i = 0; i < numberOfQueens; i++) {
            int conflicts = getConflictsInPosition(i, positionsOfQueens[i]);
            maxConflicts = Math.max(maxConflicts, conflicts);
        }

        return maxConflicts;
    }

    private boolean isSolved() {
        return getMaxNumberOfConflicts() == 3;
    }

    private void resolveConflicts() {
        while (numberOfMoves < maxNumberOfMoves) {
            if (isSolved()) {
                break;
            }
            int newRow = getPositionWithMaxConflicts();
            int newCol = getPositionWithMinConflictsInRow(newRow);
            removeQueenFromPosition(newRow, positionsOfQueens[newRow]);
            placeQueenAtPosition(newRow, newCol);
            numberOfMoves++;
        }
    }

    private void findSolution() {
        long startTime = System.currentTimeMillis();
        if (numberOfQueens == 2 || numberOfQueens == 3) {
            System.out.println(-1);
            return;
        }
        initialize();
        int numberOfRestarts = 0;
        while (!isSolved()) {
            resolveConflicts();
            if (isSolved()) {
                break;
            }
            initialize();
            numberOfRestarts++;
        }
        System.out.println("Execution time: " + (System.currentTimeMillis() - startTime) / 1000.0 + " milliseconds");
        System.out.println("Restarts: " + numberOfRestarts);
        System.out.println("Moves: " + numberOfMoves);
        printSolution();
    }

    private void printSolution() {
        if (numberOfQueens > 10) {
            return;
        }
        for (int i = 0; i < numberOfQueens; i++) {
            char[] row = new char[numberOfQueens];
            Arrays.fill(row, '_');
            row[positionsOfQueens[i]] = '*';
            System.out.println(new String(row));
        }
    }

    public static void main(String[] args) {
        //NQueens queens = new NQueens(10000, 100);
//        NQueens queens = new NQueens(9, 100);
        NQueens queens = new NQueens(10000, 100);
        queens.findSolution();
    }
}
