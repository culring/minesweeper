package minesweeper.model;

import javafx.util.Pair;

import java.util.ArrayList;

/**
 * Class used to contain information about a reverse operation.
 */
public class ReverseChange {
    /**
     * Initializes an object from given game state.
     * @param gameState Current game state.
     */
    ReverseChange(GameState gameState){
        this.gameStateChanged = true;
        this.gameState = gameState;
    }

    /**
     * Initializes an object from given positions and values of reversed fields
     * providing that a game state did not change.
     * @param positions Positions of reversed fields.
     * @param values Values of reversed fields.
     */
    ReverseChange(ArrayList<Pair<Integer, Integer>> positions, ArrayList<Integer> values){
        this.gameStateChanged = false;
        this.positions = positions;
        this.values = values;
    }

    /**
     * Initializes an object from given positions and values of reversed fields.
     * @param gameState New game state.
     * @param positions Positions of reversed fields.
     * @param values Values of reversed fields.
     */
    ReverseChange(GameState gameState, ArrayList<Pair<Integer, Integer>> positions, ArrayList<Integer> values){
        this.gameStateChanged = true;
        this.gameState = gameState;
        this.positions = positions;
        this.values = values;
    }

    /**
     * Returns positions of reversed fields.
     * @return Positions of reversed fields.
     */
    public ArrayList<Pair<Integer, Integer>> getPositions() {
        return positions;
    }

    /**
     * Returns values of reversed fields.
     * @return Values of reversed fields.
     */
    public ArrayList<Integer> getValues() {
        return values;
    }

    /**
     * Returns true if game state changed after current
     * reverse operation.
     * @return True if game state changed after current reverse operation.
     */
    public boolean gameStateChanged() {
        return gameStateChanged;
    }

    /**
     * Returns current game state (not always set!).
     * @return Current game state.
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * Positions of reversed fields.
     */
    private ArrayList<Pair<Integer, Integer>> positions;
    /**
     * Values of reversed fields.
     */
    private ArrayList<Integer> values;
    /**
     * True if game state changed.
     */
    private boolean gameStateChanged;
    /**
     * Current game state (not always set and valid!).
     */
    private GameState gameState;
}
