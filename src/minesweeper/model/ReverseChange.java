package minesweeper.model;

import javafx.util.Pair;

import java.util.ArrayList;

public class ReverseChange {
    ReverseChange(GameState gameState){
        this.gameStateChanged = true;
        this.gameState = gameState;
    }

    ReverseChange(ArrayList<Pair<Integer, Integer>> positions, ArrayList<Integer> values){
        this.gameStateChanged = false;
        this.positions = positions;
        this.values = values;
    }

    ReverseChange(GameState gameState, ArrayList<Pair<Integer, Integer>> positions, ArrayList<Integer> values){
        this.gameStateChanged = true;
        this.gameState = gameState;
        this.positions = positions;
        this.values = values;
    }

    public ArrayList<Pair<Integer, Integer>> getPositions() {
        return positions;
    }

    public ArrayList<Integer> getValues() {
        return values;
    }

    public boolean gameStateChanged() {
        return gameStateChanged;
    }

    public GameState getGameState() {
        return gameState;
    }

    private ArrayList<Pair<Integer, Integer>> positions;
    private ArrayList<Integer> values;
    private boolean gameStateChanged;
    private GameState gameState;
}
