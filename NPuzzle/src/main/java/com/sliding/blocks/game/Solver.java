package com.sliding.blocks.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class Solver {
    private static int pathLength;
    private static List<String> path;

    public static int getPathLength() {
        return pathLength;
    }

    public static List<String> getPath() {
        return path;
    }

    public static int[][] generateGoalStates(int boardSize, int emptyTilePosition) {
        int[][] goalStates = new int[boardSize * boardSize][2];

        if (emptyTilePosition == -1) {
            goalStates[0][0] = boardSize - 1;
            goalStates[0][1] = boardSize - 1;
        } else {
            goalStates[0][0] = emptyTilePosition / boardSize;
            goalStates[0][1] = emptyTilePosition % boardSize;
        }

        int tilesIndex = 1;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (i == goalStates[0][0] && j == goalStates[0][1]) {
                    continue;
                }
                goalStates[tilesIndex][0] = i;
                goalStates[tilesIndex][1] = j;
                tilesIndex++;
            }
        }

        return goalStates;
    }

    public static void runIDAStar(State initialState, int[][] goalStates, int threshold, int increment) {
        while (true) {
            State finalState = runAStar(initialState, goalStates, threshold);
            if (finalState != null) {
                pathLength = finalState.getMovesCount();
                path = constructPath(finalState);
                return;
            }
            threshold += increment;
        }
    }

    public static State runAStar(State initialState, int[][] goalStates, int threshold) {
        PriorityQueue<State> priorityQueue = new PriorityQueue<>();
        priorityQueue.add(initialState);
        while (!priorityQueue.isEmpty()) {
            State currentState = priorityQueue.poll();

            if (currentState.isSolved()) {
                return currentState;
            }

            List<State> children = currentState.getChildren(goalStates);
            for (State child : children) {
                if (child.getManhattanDistance() + child.getMovesCount() <= threshold) {
                    priorityQueue.add(child);
                }
            }
        }
        return null;
    }

    private static List<String> constructPath(State finalState) {
        List<String> path = new ArrayList<>();
        State currentState = finalState;
        while (currentState.getParent() != null) {
            String move = MOVES.get(currentState.getPreviousMove());
            path.add(move);
            currentState = currentState.getParent();
        }
        Collections.reverse(path);
        return path;
    }

    private static final Map<String, String> MOVES = new HashMap<>();

    static {
        MOVES.put(Arrays.toString(new int[] { 1, 0 }), "up");
        MOVES.put(Arrays.toString(new int[] { -1, 0 }), "down");
        MOVES.put(Arrays.toString(new int[] { 0, 1 }), "left");
        MOVES.put(Arrays.toString(new int[] { 0, -1 }), "right");
    }
}
