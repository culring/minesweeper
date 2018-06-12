package minesweeper.model;

import javafx.util.Pair;
import minesweeper.model.exceptions.WrongBoardParametersException;

import java.util.*;

/**
 * Game model class.
 */
public class Model {
    /**
     * Creates a model object.
     * @param difficulty Difficulty of creating game.
     */
    public Model(Difficulty difficulty){
        initBoard(difficulty);
        timer = new Timer();
        numberOfReversedFields = 0;
        gameState = GameState.NOT_STARTED;
        timerObservable = new TimerObservable();
        this.difficulty = difficulty;
    }

    /**
     * Cleans up running threads and removes set observers.
     */
    public void dispose(){
        timer.cancel();
        timer.purge();
        timerObservable.deleteObservers();
    }

    // API

    /**
     * Marks a field.
     * @param x X-coordinate of a field.
     * @param y Y-coordinate of a field.
     * @return MarkChange object.
     */
    public MarkChange mark(int x, int y){
        Field field = getField(x, y);
        if(gameState == GameState.BOMB_DETONATED || gameState == GameState.SOLVED ||
                (field.getState() == Field.State.MARKED && bombIndicator == bombs) ||
                (field.getState() == Field.State.UNMARKED && bombIndicator <= 0)){
            return new MarkChange();
        }

        boolean isStateChanged = field.mark();
        if(!isStateChanged){
            return new MarkChange();
        }
        if(field.getState() == Field.State.MARKED){
            --bombIndicator;
        }
        else{
            ++bombIndicator;
        }
        MarkChange.State markState = field.getState() == Field.State.MARKED ?
                MarkChange.State.MARKED : MarkChange.State.UNMARKED;
        return new MarkChange(markState, bombIndicator);
    }

    /**
     * Reverses a field.
     * @param x X-coordinate of a field.
     * @param y Y-coordinate of a field.
     * @return A ReverseChange object.
     */
    public ReverseChange reverse(int x, int y){
        Field field = getField(x, y);
        if(gameState == GameState.BOMB_DETONATED || gameState == GameState.SOLVED ||
                field.getState() == Field.State.REVERSED || field.getState() == Field.State.MARKED){
            return new ReverseChange(new ArrayList<>(), new ArrayList<>());
        }

        if(field.containsBomb()){
            gameState = GameState.BOMB_DETONATED;
            return new ReverseChange(gameState);
        }

        return reverseField(x, y);
    }

    /**
     * Runs a game.
     */
    public void start(){
        gameState = GameState.RUNNING;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(gameState == GameState.RUNNING){
                    timerObservable.increment();
                    timerObservable.notifyObservers();
                }
            }
        }, 0, 1000);
    }

    /**
     * Gets a bomb indicator.
     * @return Current bomb indicator value.
     */
    public int getBombIndicator(){
        return bombIndicator;
    }

    /**
     * Returns width of a board.
     * @return Width of a board.
     */
    public int getWidth(){
        return width;
    }

    /**
     * Returns height of a board.
     * @return Height of a board.
     */
    public int getHeight(){
        return height;
    }

    /**
     * Add observer to a timer.
     * @param observer Observed to add to timer.
     */
    public void addTimerObserver(Observer observer){
        timerObservable.addObserver(observer);
    }
    // API

    /**
     * Returns difficulty of this game.
     * @return Difficulty of this game.
     */
    public Difficulty getDifficulty(){
        return difficulty;
    }

    /**
     * Reverses all fields beginning from a field of position (x,y).
     * @param x X-position of starting field.
     * @param y Y-position of starting field.
     * @return A ReverseChange object.
     */
    private ReverseChange reverseField(int x, int y){
        ArrayList<Pair<Integer, Integer>> positions = new ArrayList<>();
        ArrayList<Integer> values = new ArrayList<>();
        Set<Pair<Integer, Integer>> set = new HashSet<>();
        Stack<Pair<Integer, Integer>> stack = new Stack<>();
        stack.push(new Pair<>(x, y));

        while(!stack.empty()){
            Pair<Integer, Integer> position = stack.pop();
            int currX = position.getKey(), currY = position.getValue();
            if(areCoordinatorsValid(currX, currY) || set.contains(position)){
                continue;
            }
            set.add(position);
            Field field = getField(currX, currY);
            if(field.getState() == Field.State.MARKED || field.getState() == Field.State.REVERSED){
                continue;
            }
            field.reverse();
            ++numberOfReversedFields;
            int value = field.getValue();
            positions.add(position);
            values.add(value);
            if(value == 0){
                stack.push(new Pair<>(currX-1, currY-1));
                stack.push(new Pair<>(currX, currY-1));
                stack.push(new Pair<>(currX+1, currY-1));
                stack.push(new Pair<>(currX+1, currY));
                stack.push(new Pair<>(currX+1, currY+1));
                stack.push(new Pair<>(currX, currY+1));
                stack.push(new Pair<>(currX-1, currY+1));
                stack.push(new Pair<>(currX-1, currY));
            }
        }

        if(numberOfReversedFields == width*height - bombs){
            gameState = GameState.SOLVED;
            return new ReverseChange(gameState, positions, values);
        }
        return new ReverseChange(positions, values);
    }

    /**
     * Initializes all variables related to a board and given difficulty
     * and generates a board.
     * @param difficulty Difficulty of a game to generate.
     */
    private void initBoard(Difficulty difficulty){
        switch(difficulty){
            case EASY:
                height = 9;
                width = 9;
                bombs = 10;
                break;
            case MEDIUM:
                height = 16;
                width = 16;
                bombs = 40;
                break;
            case HARD:
                height = 16;
                width = 30;
                bombs = 99;
                break;
            default:
                throw new IndexOutOfBoundsException("Enumerated out of difficulty enum");
        }

        bombIndicator = bombs;
        board = createBoard(height, width, bombIndicator);
    }

    /**
     * Generates a board.
     * @param height Height of a board.
     * @param width Width of a board.
     * @param bombs Bombs in a board.
     * @return Two-dimensional list of fields representation of a board.
     */
    private ArrayList<ArrayList<Field>> createBoard(int height, int width, int bombs){
        if(width < 1 || height < 1 || bombs > width*height){
            throw new WrongBoardParametersException("Cannot create a board with given parameters.");
        }

        ArrayList<ArrayList<Field>> board = new ArrayList<>();
        Set<Integer> bombsLinearPositions = new HashSet<>(generateRandomNumbers(height*width, bombs));
        for(int i = 0; i<height; ++i){
            board.add(new ArrayList<>());
            for(int j = 0; j<width; ++j){
                boolean containsBomb = bombsLinearPositions.contains(i*width + j);
                int numberOfAdjacentBombs = getNumberOfAdjacent(bombsLinearPositions, width, height, i * width + j);
                board.get(i).add(new Field(containsBomb, numberOfAdjacentBombs));
            }
        }

        return board;
    }

    /**
     * Generates n random integers from the range (1,n).
     * @param range Right boundary of the range.
     * @param n Number to generate.
     * @return Array of generated integers.
     */
    private ArrayList<Integer> generateRandomNumbers(int range, int n){
        Integer[] arr = new Integer[range];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = i;
        }
        Collections.shuffle(Arrays.asList(arr));

        return new ArrayList<>(Arrays.asList(Arrays.copyOf(arr, n)));
    }

    /**
     * Returns number of fields which are adjacent to field of given linear
     * position and holds a bomb.
     * @param linearPositions Current set of visited fields.
     * @param width Width of a board.
     * @param height Height of a board.
     * @param position Linear position of current field.
     * @return Number of adjacent fields holding a bomb.
     */
    private int getNumberOfAdjacent(Set<Integer> linearPositions, int width, int height, int position){
        int numberOfAdjacent = 0;
        int y = position/width;
        int x = position%width;

        boolean isLeft = x > 0;
        boolean isRight = x < width - 1;
        boolean isTop = y > 0;
        boolean isBottom = y < height;

        if(isLeft && isTop && linearPositions.contains(position-width-1)) ++numberOfAdjacent;
        if(isTop && linearPositions.contains(position-width)) ++numberOfAdjacent;
        if(isTop && isRight && linearPositions.contains(position-width+1)) ++numberOfAdjacent;
        if(isRight && linearPositions.contains(position+1)) ++numberOfAdjacent;
        if(isBottom && isRight && linearPositions.contains(position+width+1)) ++numberOfAdjacent;
        if(isBottom && linearPositions.contains(position+width)) ++numberOfAdjacent;
        if(isBottom && isLeft && linearPositions.contains(position+width-1)) ++numberOfAdjacent;
        if(isLeft && linearPositions.contains(position-1)) ++numberOfAdjacent;

        return numberOfAdjacent;
    }

    /**
     * Returns a Field object.
     * @param x X-coordinate.
     * @param y Y-coordinate.
     * @return A field object.
     */
    private Field getField(int x, int y){
        if(areCoordinatorsValid(x, y)){
            throw new WrongBoardParametersException("Cannot access the field at the given position: " + x + ", " + y + ".");
        }

        return board.get(y).get(x);
    }

    /**
     * Tests if given coordinates are valid in terms
     * of the board.
     * @param x X-coordinate.
     * @param y Y-coordinate.
     * @return True if coordinates are valid.
     */
    private boolean areCoordinatorsValid(int x, int y){
        return x < 0 || x >= width || y < 0 || y >= height;
    }

    /**
     * A board object.
     */
    private ArrayList<ArrayList<Field>> board;
    /**
     * Current game state.
     */
    private GameState gameState;
    /**
     * Variables describing properties of a board.
     */
    private int height, width, bombs;
    /**
     * Current bomb indicator value.
     */
    private int bombIndicator;
    /**
     * Number of already reversed fields.
     */
    private int numberOfReversedFields;
    /**
     * Timer object responsible for counting time.
     */
    private Timer timer;
    /**
     * An observable object watching changes in timer.
     */
    private TimerObservable timerObservable;
    /**
     * Difficulty of this game.
     */
    private Difficulty difficulty;

    /**
     * Observable which tracks timer changes.
     */
    public class TimerObservable extends Observable{
        /**
         * Creates an object.
         */
        TimerObservable(){
            seconds = 0;
        }

        /**
         * Returns current number of seconds.
         * @return Number of passed seconds.
         */
        public int getSeconds() {
            return seconds;
        }

        /**
         * Increments timer counter.
         */
        void increment(){
            ++seconds;
            setChanged();
            notifyObservers();
        }

        /**
         * Number of seconds which already passed.
         */
        private int seconds;
    }

    /**
     * Possible difficulties of a game.
     */
    public enum Difficulty{
        EASY,
        MEDIUM,
        HARD
    }
}
