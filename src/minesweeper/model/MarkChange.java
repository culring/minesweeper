package minesweeper.model;

/**
 * Used to pass information about marking a field.
 */
public class MarkChange {
    /**
     * Initializes the object.
     */
    MarkChange(){
        this.isStateChanged = false;
    }

    /**
     * Initializes the object.
     * @param state A state of a field.
     * @param bombCounter A current bomb counter value.
     */
    MarkChange(State state, int bombCounter){
        this.isStateChanged = true;
        this.state = state;
        this.bombCounter = bombCounter;
    }

    /**
     * Returns isStateChanged.
     * @return True if a field connected to this object changed its state after the marking operation.
     */
    public boolean isStateChanged() {
        return isStateChanged;
    }

    /**
     * Returns a state.
     * @return A state of related field.
     */
    public State getState(){
        return state;
    }

    /**
     * Returns bombCounter.
     * @return Current bomb counter value.
     */
    public int getBombCounter() {
        return bombCounter;
    }

    /**
     * Indicates if a field changed its state during marking operation.
     */
    private boolean isStateChanged;
    /**
     * Contains current field state.
     */
    private State state;
    /**
     * Current value of bomb counter.
     */
    private int bombCounter;

    /**
     * Possible states of a field after successful marking operation.
     */
    public enum State{
        MARKED,
        UNMARKED
    }
}
