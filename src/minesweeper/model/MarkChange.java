package minesweeper.model;

public class MarkChange {
    MarkChange(){
        this.isStateChanged = false;
    }

    MarkChange(State state, int bombCounter){
        this.isStateChanged = true;
        this.state = state;
        this.bombCounter = bombCounter;
    }

    public boolean isStateChanged() {
        return isStateChanged;
    }

    public State getState(){
        return state;
    }

    public int getBombCounter() {
        return bombCounter;
    }

    private boolean isStateChanged;
    private State state;
    private int bombCounter;

    public enum State{
        MARKED,
        UNMARKED
    }
}
