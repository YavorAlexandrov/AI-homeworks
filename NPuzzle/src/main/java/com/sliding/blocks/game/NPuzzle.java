package com.sliding.blocks.game;

import java.util.List;
import java.util.Scanner;

public class NPuzzle {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int numberOfTiles = scanner.nextInt();
        int emptyTilePosition = scanner.nextInt();
        int boardSize = (int) Math.sqrt(numberOfTiles + 1);
        int[][] initialBoard = new int[boardSize][boardSize];
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                initialBoard[i][j] = scanner.nextInt();
            }
        }

        int[][] goalStates = Solver.generateGoalStates(boardSize, emptyTilePosition);

        State initialState = new State(initialBoard, 0, null, null);
        initialState.calculateManhattanDistance(goalStates);

        if (!initialState.isSolvable()) {
            System.out.println(-1);
            return;
        }

        long startTime = System.currentTimeMillis();
        Solver.runIDAStar(initialState, goalStates, 0, 1);
        long endTime = System.currentTimeMillis();
        double solvingTime = (endTime - startTime) / 1000.0;

        System.out.println(Solver.getPathLength());
        List<String> path = Solver.getPath();
        for (String move : path) {
            System.out.println(move);
        }
        //System.out.println(solvingTime);
    }

}
