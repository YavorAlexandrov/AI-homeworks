package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

public class LeapFrogPuzzle {

    public static void main(String[] args) {
        int n = readInput();
        int size = (n * 2) + 1;
        char[] board = createBoard(size);

        int zeroState = findZeroState(board);
        List<Move> path = BFS(new Move(board, zeroState, generateValidMoves(board, zeroState), null));
        for (Move m : path) {
            System.out.println(m.getBoard());
        }
    }

    public static int readInput() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextInt();
    }

    public static List<Move> BFS(Move move) {
        Queue<Move> queue = new LinkedList<>();
        queue.add(move);

        while (!queue.isEmpty()) {
            Move curr = queue.poll();

            if (isGoalState(curr.getBoard())) {
                return constructPath(curr);
            }

            List<Move> children = curr.getChildren();

            if (children == null) {
                break;
            }

            for (Move m : children) {
                if (isValidMove(m.getBoard(), m.getZeroState())) {
                    queue.add(new Move(m.getBoard(), m.getZeroState(), generateValidMoves(m.getBoard(), m.getZeroState()), curr));
                }

                if (isGoalState(m.getBoard())) {
                    queue.add(new Move(m.getBoard(), m.getZeroState(), null, curr));
                }
            }
        }

        throw new IllegalArgumentException("No path found!");
    }

    private static List<Move> constructPath(Move curr) {
        List<Move> path = new ArrayList<>();
        while (curr != null) {
            path.add(curr);
            curr = curr.getParent();
        }
        Collections.reverse(path);
        return path;
    }

    public static boolean isGoalState(char[] board) {
        int size = board.length;
        char[] newBoard = new char[size];
        for (int i = 0; i < size; i++) {
            if (i == size / 2) {
                newBoard[i] = '_';
            } else if (i < size / 2) {
                newBoard[i] = 'R';
            } else {
                newBoard[i] = 'L';
            }
        }
        return Arrays.equals(board, newBoard);
    }

    public static boolean isValidMove(char[] board, int zeroState) {
        return (zeroState - 1 >= 0 && board[zeroState] == '_' && board[zeroState - 1] == 'L')
                || (zeroState - 2 >= 0 && board[zeroState] == '_' && board[zeroState - 2] == 'L')
                || (zeroState + 1 < board.length && board[zeroState] == '_' && board[zeroState + 1] == 'R')
                || (zeroState + 2 < board.length && board[zeroState] == '_' && board[zeroState + 2] == 'R');
    }

    public static List<Move> generateValidMoves(char[] board, int zeroState) {
        List<Move> validMoves = new ArrayList<>();

        if (zeroState - 1 >= 0 && board[zeroState] == '_' && board[zeroState - 1] == 'L') {
            char[] newBoard = board.clone();
            newBoard[zeroState] = 'L';
            newBoard[zeroState - 1] = '_';
            validMoves.add(new Move(newBoard, zeroState - 1));
        }

        if (zeroState - 2 >= 0 && board[zeroState] == '_' && board[zeroState - 2] == 'L') {
            char[] newBoard = board.clone();
            newBoard[zeroState] = 'L';
            newBoard[zeroState - 2] = '_';
            validMoves.add(new Move(newBoard, zeroState - 2));
        }

        if (zeroState + 1 < board.length && board[zeroState] == '_' && board[zeroState + 1] == 'R') {
            char[] newBoard = board.clone();
            newBoard[zeroState] = 'R';
            newBoard[zeroState + 1] = '_';
            validMoves.add(new Move(newBoard, zeroState + 1));
        }

        if (zeroState + 2 < board.length && board[zeroState] == '_' && board[zeroState + 2] == 'R') {
            char[] newBoard = board.clone();
            newBoard[zeroState] = 'R';
            newBoard[zeroState + 2] = '_';
            validMoves.add(new Move(newBoard, zeroState + 2));
        }

        return validMoves;
    }

    public static char[] createBoard(int size) {
        char[] board = new char[size];
        for (int i = 0; i < size; i++) {
            if (i == size / 2) {
                board[i] = '_';
            } else if (i < size / 2) {
                board[i] = 'L';
            } else {
                board[i] = 'R';
            }
        }
        return board;
    }

    public static int findZeroState(char[] board) {
        for (int i = 0; i < board.length; i++) {
            if (board[i] == '_') {
                return i;
            }
        }
        throw new IllegalArgumentException("Missing empty spot!");
    }

}
