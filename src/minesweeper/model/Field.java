package minesweeper.model;

class Field {
    Field(boolean containsBomb, int value){
        this.containsBomb = containsBomb;
        this.state = State.UNMARKED;
        this.value = value;
    }

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

    void reverse(){
        if(containsBomb){
            state = State.BOMB_DETONATED;
        }
        else{
            state = State.REVERSED;
        }
    }

    void reset(){
        state = State.UNMARKED;
    }

    boolean containsBomb(){
        return containsBomb;
    }

    State getState() {
        return state;
    }

    int getValue() {
        return value;
    }

    private boolean containsBomb;
    private State state;
    private int value;

    public enum State{
        UNMARKED,
        MARKED,
        REVERSED,
        BOMB_DETONATED
    }
}
