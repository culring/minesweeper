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

public class GameController {
    public GameController(Stage stage) {
        this.stage = stage;
        logger = new Logger("GameController");
        startGame(Model.Difficulty.EASY);
    }

    private void createModelView(Model.Difficulty difficulty){
        model = new Model(difficulty);
        stage.setOnCloseRequest(event -> model.dispose());
        try {
            view = new View(stage, model.getHeight(), model.getWidth(), model.getBombIndicator());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startGame(Model.Difficulty difficulty){
        stage.toBack();
        createModelView(difficulty);
        setup();
        stage.sizeToScene();
        stage.centerOnScreen();
        stage.toFront();
    }

    private void startNewGame(Model.Difficulty difficulty){
        stage.toBack();
        model.dispose();
        createModelView(difficulty);
        setup();
        stage.sizeToScene();
        stage.centerOnScreen();
        stage.toFront();
    }

    private void setup(){
        view.addButtonObserver(new ButtonObserver());
        view.setEasyMenuItemHandler(event -> startNewGame(Model.Difficulty.EASY));
        view.setMediumItemHandler(event -> startNewGame(Model.Difficulty.MEDIUM));
        view.setHardItemHandler(event -> startNewGame(Model.Difficulty.HARD));
        model.addTimerObserver(new TimerObserver());
        gameStarted = false;
        view.addResetButtonHandler(event -> startNewGame(model.getDifficulty()));
    }

    private Model model;
    private View view;
    private boolean gameStarted;
    private Logger logger;
    private Stage stage;

    class TimerObserver implements Observer{
        @Override
        public void update(Observable o, Object arg) {
            Model.TimerObservable timer = (Model.TimerObservable)o;
            int seconds = timer.getSeconds();
            view.setTimer(seconds);
        }
    }

    class ButtonObserver implements Observer{
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

        private Pair<Integer, Integer> parseId(String id){
            String[] parts = id.split(", ");
            return new Pair<>(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        }
    }
}
