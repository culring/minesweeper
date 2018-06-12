package minesweeper.model;

/**
 * Class representing a filed of a board.
 */
class Field {
    /**
     * Field constructor.
     * @param containsBomb True if this field contains a bomb.
     * @param value Number linked to a field.
     */
    Field(boolean containsBomb, int value){
        this.containsBomb = containsBomb;
        this.state = State.UNMARKED;
        this.value = value;
    }

    /**
     * Marks/Removes a mark with a flag in this field.
     * @return True if marking was successful.
     */
    boolean mark(){
        if(state == State.UNMARKED){
            state = State.MARKED;
            return true;
        }
        else if(state == State.MARKED){
            state = State.UNMARKED;
            return true;
        }
        return false;
    }

    /**
     * Reverse this field.
     */
    void reverse(){
        if(containsBomb){
            state = State.BOMB_DETONATED;
        }
        else{
            state = State.REVERSED;
        }
    }

    /**
     * Returns the boolean indicating if this field contains a bomb.
     * @return ContainsBomb boolean field.
     */
    boolean containsBomb(){
        return containsBomb;
    }

    /**
     * Gets the current field state.
     * @return The current field state.
     */
    State getState() {
        return state;
    }

    /**
     * Returns the value of this field.
     * @return The value of this field.
     */
    int getValue() {
        return value;
    }

    /**
     * True if this field contains a bomb.
     */
    private boolean containsBomb;
    /**
     * The current state of the field.
     */
    private State state;
    /**
     * The value of the field.
     */
    private int value;

    /**
     * States in which a field can be.
     */
    public enum State{
        UNMARKED,
        MARKED,
        REVERSED,
        BOMB_DETONATED
    }
}
