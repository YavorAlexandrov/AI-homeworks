package com.tic.tac.toe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class TicTacToe {
    private static final int BOARD_SIZE = 3;
    private static final char PLAYER_X = 'X';
    private static final char PLAYER_O = 'O';
    private static final char EMPTY_CELL = ' ';
    private static final Map<Character, Integer> playerValues = new HashMap<>();

    static {
        playerValues.put(PLAYER_X, 1);
        playerValues.put(PLAYER_O, -1);
        playerValues.put(EMPTY_CELL, 0);
    }

    private static char findWinner(char[][] board) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board[i][0] == board[i][1] && board[i][1] == board[i][2] && board[i][0] != EMPTY_CELL) {
                return board[i][0];
            }
            if (board[0][i] == board[1][i] && board[1][i] == board[2][i] && board[0][i] != EMPTY_CELL) {
                return board[0][i];
            }
        }
        if (board[0][0] == board[1][1] && board[1][1] == board[2][2] && board[0][0] != EMPTY_CELL) {
            return board[0][0];
        }
        if (board[0][2] == board[1][1] && board[1][1] == board[2][0] && board[0][2] != EMPTY_CELL) {
            return board[0][2];
        }

        return EMPTY_CELL;
    }

    private static int calculateHeuristicValue(char[][] board) {
        char winner = findWinner(board);
        return playerValues.get(winner) * (getPossibleMoves(board).size() + 1);
    }

    private static List<List<Integer>> getPossibleMoves(char[][] board) {
        List<List<Integer>> possibleMoves = new ArrayList<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == EMPTY_CELL) {
                    possibleMoves.add(List.of(i, j));
                }
            }
        }
        return possibleMoves;
    }

    private static boolean isFinal(char[][] board) {
        return findWinner(board) != EMPTY_CELL || getPossibleMoves(board).isEmpty();
    }

    private static int minimaxAlphaBeta(char[][] board, int alpha, int beta, boolean isMaximizingPlayer) {
        if (isFinal(board)) {
            return calculateHeuristicValue(board);
        }

        int bestValue;
        if (isMaximizingPlayer) {
            bestValue = Integer.MIN_VALUE;
            for (List<Integer> move : getPossibleMoves(board)) {
                int i = move.get(0);
                int j = move.get(1);
                board[i][j] = PLAYER_X;
                int score = minimaxAlphaBeta(board, alpha, beta, false);
                board[i][j] = EMPTY_CELL;
                bestValue = Math.max(bestValue, score);
                alpha = Math.max(alpha, bestValue);
                if (alpha >= beta) {
                    break;
                }
            }
        } else {
            bestValue = Integer.MAX_VALUE;
            for (List<Integer> move : getPossibleMoves(board)) {
                int i = move.get(0);
                int j = move.get(1);
                board[i][j] = PLAYER_O;
                int score = minimaxAlphaBeta(board, alpha, beta, true);
                board[i][j] = EMPTY_CELL;
                bestValue = Math.min(bestValue, score);
                beta = Math.min(beta, bestValue);
                if (alpha >= beta) {
                    break;
                }
            }
        }
        return bestValue;
    }

    private static void makeBestMove(char[][] board) {
        int bestScore = Integer.MIN_VALUE;
        List<Integer> bestMove = null;
        for (List<Integer> move : getPossibleMoves(board)) {
            int i = move.get(0);
            int j = move.get(1);
            board[i][j] = PLAYER_X;
            int currentScore = minimaxAlphaBeta(board, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
            board[i][j] = EMPTY_CELL;

            if (currentScore > bestScore) {
                bestScore = currentScore;
                bestMove = move;
            }
        }

        if (bestMove != null) {
            int i = bestMove.get(0);
            int j = bestMove.get(1);
            board[i][j] = PLAYER_X;
        }
    }

    private static char chooseStartingPlayer() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the first player (X or O): ");
        char firstPlayer = scanner.next().charAt(0);

        while (firstPlayer != PLAYER_X && firstPlayer != PLAYER_O) {
            System.out.print("Invalid player. Please enter X or O: ");
            firstPlayer = scanner.next().charAt(0);
        }

        return firstPlayer;
    }

    private static char switchPlayer(char player) {
        return (player == PLAYER_X) ? PLAYER_O : PLAYER_X;
    }

    private static List<Integer> getUserMove(char[][] board) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your move (row and column separated by a space): ");
        int i = scanner.nextInt() - 1;
        int j = scanner.nextInt() - 1;

        List<List<Integer>> possibleMoves = getPossibleMoves(board);

        List<Integer> move = List.of(i, j);

        while (!possibleMoves.contains(move)) {
            System.out.print("This move is not possible. Please try again: ");
            i = scanner.nextInt() - 1;
            j = scanner.nextInt() - 1;
        }

        return List.of(i, j);
    }

    private static void printBoard(char[][] board) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            StringBuilder row = new StringBuilder();
            for (int j = 0; j < BOARD_SIZE; j++) {
                row.append(board[i][j]);
                if (j < BOARD_SIZE - 1) {
                    row.append('|');
                }
            }
            System.out.println(row);
            if (i < BOARD_SIZE - 1) {
                System.out.println("-----");
            }
        }
    }

    private static void printWinner(char[][] board) {
        char winner = findWinner(board);

        if (winner == PLAYER_X) {
            System.out.println("The computer won!");
        } else if (winner == PLAYER_O) {
            System.out.println("You won!");
        } else {
            System.out.println("Tie");
        }
    }

    private static void playTicTacToe() {
        System.out.println("Computer plays with X, you play with O. Choose who is first: ");
        char player = chooseStartingPlayer();
        char[][] board = {
                { EMPTY_CELL, EMPTY_CELL, EMPTY_CELL },
                { EMPTY_CELL, EMPTY_CELL, EMPTY_CELL },
                { EMPTY_CELL, EMPTY_CELL, EMPTY_CELL },
        };

        while (!isFinal(board)) {
            if (player == PLAYER_X) {
                makeBestMove(board);
                printBoard(board);
            } else if (player == PLAYER_O) {
                List<Integer> userMove = getUserMove(board);
                int i = userMove.get(0);
                int j = userMove.get(1);
                board[i][j] = PLAYER_O;
            }
            player = switchPlayer(player);
        }

        printWinner(board);
    }

    public static void main(String[] args) {
        System.out.println("Tic Tac Toe");
        playTicTacToe();
    }
}