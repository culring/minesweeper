package minesweeper.controller;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Pair;
import minesweeper.logger.Logger;
import minesweeper.model.GameState;
import minesweeper.model.MarkChange;
import minesweeper.model.Model;
import minesweeper.model.ReverseChange;
import minesweeper.view.Field;
import minesweeper.view.View;

import java.util.Observable;
import java.util.Observer;

/**
 * The controller class responsible for the game window.
 */
public class GameController {
    /**
     * The game controller constructor.
     * @param stage A stage to display scenes.
     */
    public GameController(Stage stage) {
        this.stage = stage;
        logger = new Logger("GameController");
        startGame(Model.Difficulty.EASY, true);
    }

    /**
     * Creates a model and a view for a level specified with
     * a given difficulty. Both created objects are assigned
     * to internal variables.
     * @param difficulty Indicates difficulty of creating level.
     */
    private void createModelView(Model.Difficulty difficulty){
        model = new Model(difficulty);
        stage.setOnCloseRequest(event -> model.dispose());
        try {
            view = new View(stage, model.getHeight(), model.getWidth(), model.getBombIndicator());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts new game.
     * @param difficulty Difficulty of starting game.
     * @param center Specifies if the stage should be centered on the screen.
     */
    private void startGame(Model.Difficulty difficulty, boolean center){
        stage.toBack();
        createModelView(difficulty);
        setup();
        stage.sizeToScene();
        if(center) stage.centerOnScreen();
        stage.toFront();
    }

    /**
     * Starts new game.
     * @param difficulty Difficulty of starting game.
     */
    private void startNewGame(Model.Difficulty difficulty){
        model.dispose();
        startGame(difficulty, true);
    }

    /**
     * Launched when pressed reset button in the game.
     * Responsible for cleaning up all running threads
     * and starting a new game.
     */
    private void resetGame(){
        model.dispose();
        startGame(model.getDifficulty(), false);
    }

    /**
     * Setups all handlers and required variables before
     * starting a game.
     */
    private void setup(){
        view.addButtonObserver(new ButtonObserver());
        view.setEasyMenuItemHandler(event -> startNewGame(Model.Difficulty.EASY));
        view.setMediumItemHandler(event -> startNewGame(Model.Difficulty.MEDIUM));
        view.setHardItemHandler(event -> startNewGame(Model.Difficulty.HARD));
        model.addTimerObserver(new TimerObserver());
        gameStarted = false;
        view.addResetButtonHandler(event -> resetGame());
    }

    /**
     * Model of the game.
     */
    private Model model;
    /**
     * View of the game.
     */
    private View view;
    /**
     * Specifies if the game has already started.
     */
    private boolean gameStarted;
    /**
     * Logger object used to debug.
     */
    private Logger logger;
    /**
     * Main stage.
     */
    private Stage stage;

    /**
     * An observer added to the model timer.
     * Gets changed time and updates the view.
     */
    class TimerObserver implements Observer{
        /**
         * Doing updates after getting a notification
         * from the timer.
         * @param o Observed object.
         * @param arg Passed argument.
         */
        @Override
        public void update(Observable o, Object arg) {
            Model.TimerObservable timer = (Model.TimerObservable)o;
            int seconds = timer.getSeconds();
            view.setTimer(seconds);
        }
    }

    /**
     * The observer class added to the fields of the view.
     */
    class ButtonObserver implements Observer{
        /**
         * Does all needed updates after getting notification.
         * @param o Observed object.
         * @param arg Passed argument.
         */
        @Override
        public void update(Observable o, Object arg) {
            MouseEvent event = (MouseEvent)arg;
            if(!gameStarted){
                model.start();
                gameStarted = true;
            }

            Field field = ((Field)event.getSource());
            String fieldId = field.getId();
            Pair<Integer, Integer> buttonPosition = parseId(fieldId);
            int x = buttonPosition.getKey(), y = buttonPosition.getValue();

            MouseButton mouseButton = event.getButton();
            if(mouseButton == MouseButton.PRIMARY){
                logger.log("left button clicked");
                handleLeftButton(x, y);
            }
            else if(mouseButton == MouseButton.SECONDARY){
                logger.log("right button clicked");
                handleRightButton(x, y);
            }
        }

        /**
         * Handles actions related to pressing the left mouse button.
         * @param x X-coordinate of the pressed button.
         * @param y Y-coordinate of the pressed button.
         */
        private void handleLeftButton(int x, int y){
            ReverseChange reverseChange = model.reverse(x, y);

            if(reverseChange.gameStateChanged() && reverseChange.getGameState() == GameState.BOMB_DETONATED){
                logger.log("Bomb detonated.");
                view.showBombDetonated(x, y);
            }
            else{
                for(int i = 0; i < reverseChange.getPositions().size(); ++i){
                    Pair<Integer, Integer> position = reverseChange.getPositions().get(i);
                    int currX = position.getKey(), currY = position.getValue();
                    int value = reverseChange.getValues().get(i);
                    view.setFieldValue(currX, currY, value);
                }

                if(reverseChange.gameStateChanged() && reverseChange.getGameState() == GameState.SOLVED){
                    view.victory();
                }
            }
        }

        /**
         * Handles actions related to pressing the right mouse button.
         * @param x X-coordinate of the pressed button.
         * @param y Y-coordinate of the pressed button.
         */
        private void handleRightButton(int x, int y){
            MarkChange markChange = model.mark(x, y);
            if(markChange.isStateChanged()){
                logger.log("state changed");
                if(markChange.getState() == MarkChange.State.MARKED){
                    view.mark(x, y);
                }
                else{
                    view.unmark(x, y);
                }
                view.setCounter(markChange.getBombCounter());
            }
        }

        /**
         * Translates string in format 'x, y' to pair
         * of integers.
         * @param id String in format 'x. y'.
         * @return Pair of parsed integers.
         */
        private Pair<Integer, Integer> parseId(String id){
            String[] parts = id.split(", ");
            return new Pair<>(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        }
    }
}
