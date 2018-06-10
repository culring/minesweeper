package minesweeper.model;

import javafx.util.Pair;
import minesweeper.model.exceptions.WrongBoardParametersException;

import java.util.*;

public class Model {
    public Model(Difficulty difficulty){
        initBoard(difficulty);
        timer = new Timer();
        numberOfReversedFields = 0;
        gameState = GameState.NOT_STARTED;
        timerObservable = new TimerObservable();
        this.difficulty = difficulty;
    }

    public void dispose(){
        timer.cancel();
        timer.purge();
        timerObservable.deleteObservers();
    }

    // API
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

    public int getBombIndicator(){
        return bombIndicator;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    public void addTimerObserver(Observer observer){
        timerObservable.addObserver(observer);
    }
    // API

    public Difficulty getDifficulty(){
        return difficulty;
    }

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

    private ArrayList<Integer> generateRandomNumbers(int range, int n){
        Integer[] arr = new Integer[range];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = i;
        }
        Collections.shuffle(Arrays.asList(arr));

        return new ArrayList<>(Arrays.asList(Arrays.copyOf(arr, n)));
    }

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
        if(isBottom && isRight && linearPositions.contains(position-width+1)) ++numberOfAdjacent;
        if(isRight && linearPositions.contains(position+1)) ++numberOfAdjacent;
        if(isBottom && isRight && linearPositions.contains(position+width+1)) ++numberOfAdjacent;
        if(isBottom && linearPositions.contains(position+width)) ++numberOfAdjacent;
        if(isBottom && linearPositions.contains(position+width-1)) ++numberOfAdjacent;
        if(isLeft && linearPositions.contains(position-1)) ++numberOfAdjacent;

        return numberOfAdjacent;
    }

    private Field getField(int x, int y){
        if(areCoordinatorsValid(x, y)){
            throw new WrongBoardParametersException("Cannot access the field at the given position: " + x + ", " + y + ".");
        }

        return board.get(y).get(x);
    }

    private boolean areCoordinatorsValid(int x, int y){
        return x < 0 || x >= width || y < 0 || y >= height;
    }

    private ArrayList<ArrayList<Field>> board;
    private GameState gameState;
    private int height, width, bombs;
    private int bombIndicator;
    private int numberOfReversedFields;
    private Timer timer;
    private TimerObservable timerObservable;
    private Difficulty difficulty;

    public class TimerObservable extends Observable{
        TimerObservable(){
            seconds = 0;
        }

        public int getSeconds() {
            return seconds;
        }

        void increment(){
            ++seconds;
            setChanged();
            notifyObservers();
        }

        private int seconds;
    }

    public enum Difficulty{
        EASY,
        MEDIUM,
        HARD
    }
}
