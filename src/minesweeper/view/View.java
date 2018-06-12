package minesweeper.view;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import minesweeper.logger.Logger;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * View class of a game.
 */
public class View {
    /**
     * Initializes an object.
     * @param stage Current stage.
     * @param height Height of a board.
     * @param width Width of a board.
     * @param bombs Bombs in a board.
     * @throws Exception
     */
    public View(Stage stage, int height, int width, int bombs) throws Exception{
        this.stage = stage;
        gridObservable = new GridObservable();
        logger = new Logger("View");

        setScene();
        setupTopBar(bombs);
        setupGameGrid(height, width);
        setupStage();
    }

    /**
     * Adds observer to fields.
     * @param observer Observer to add.
     */
    public void addButtonObserver(Observer observer){
        gridObservable.addObserver(observer);
    }

    /**
     * Adds handler to reset button.
     * @param handler Handler to add.
     */
    public void addResetButtonHandler(EventHandler<MouseEvent> handler){
        resetButton.setOnMouseClicked(handler);
    }

    /**
     * Mark a field.
     * @param x X-coordinate.
     * @param y Y-coordinate.
     */
    public void mark(int x, int y){
        getField(x, y).set(Field.Type.MARKED);
    }

    /**
     * "Unmark" a field.
     * @param x X-coordinate.
     * @param y Y-coordinate.
     */
    public void unmark(int x, int y){
        getField(x, y).set(Field.Type.UNMARKED);
    }

    /**
     * Shows a bomb in this field.
     * @param x X-coordinate.
     * @param y Y-coordinate.
     */
    public void showBomb(int x, int y){
        getField(x, y).set(Field.Type.BOMB);
    }

    /**
     * Shows a detonated bomb in this field.
     * @param x X-coordinate.
     * @param y Y-coordinate.
     */
    public void showBombDetonated(int x, int y){
        getField(x, y).set(Field.Type.BOMB_DETONATED);
        resetButton.setImage(new Image("/minesweeper/view/resources/game_board/bomb_detonated.jpg",
                50, 0, true, true));
    }

    /**
     * Sets a number value in a field.
     * @param x X-coordinate.
     * @param y Y-coordinate.
     * @param value Value to be set.
     */
    public void setFieldValue(int x, int y, int value){
        if(value == 0){
            getField(x, y).set(Field.Type.REVERSED);
            return;
        }
        getField(x, y).set(value);
    }

    /**
     * Sets timer.
     * @param seconds Seconds to set on timer.
     */
    public void setTimer(int seconds){
        Platform.runLater(() -> timerCounter.setValue(seconds));
    }

    /**
     * Sets bomb counter.
     * @param value Value to be set.
     */
    public void setCounter(int value){
        bombCounter.setValue(value);
    }

    /**
     * Changes middle bomb image to indicate victory.
     */
    public void victory(){
        resetButton.setImage(new Image("/minesweeper/view/resources/game_board/bomb_green.jpg",
                50, 0, true, true));
    }

    /**
     * Sets handler to easy difficulty menu item.
     * @param handler Handler to set.
     */
    public void setEasyMenuItemHandler(EventHandler<ActionEvent> handler){
        MenuBar menuBar = (MenuBar) scene.lookup("#menuBar");
        MenuItem menuItem = menuBar.getMenus().get(0).getItems().get(0);
        menuItem.setOnAction(handler);
    }

    /**
     * Sets handler to medium difficulty menu item.
     * @param handler Handler to set.
     */
    public void setMediumItemHandler(EventHandler<ActionEvent> handler){
        MenuBar menuBar = (MenuBar) scene.lookup("#menuBar");
        MenuItem menuItem = menuBar.getMenus().get(0).getItems().get(1);
        menuItem.setOnAction(handler);
    }

    /**
     * Sets handler to hard difficulty menu item.
     * @param handler Handler to set.
     */
    public void setHardItemHandler(EventHandler<ActionEvent> handler){
        MenuBar menuBar = (MenuBar) scene.lookup("#menuBar");
        MenuItem menuItem = menuBar.getMenus().get(0).getItems().get(2);
        menuItem.setOnAction(handler);
    }

    /**
     * Setups top bar of a view.
     * @param bombs Total bombs in a game.
     */
    private void setupTopBar(int bombs) {
        timerCounter = new Counter(3, 0);
        bombCounter = new Counter(3, bombs);
        resetButton = new ImageView(new Image("/minesweeper/view/resources/game_board/bomb.jpg",
                50, 0, true, true));

        BorderPane borderPane = (BorderPane) scene.lookup("#borderPane");
        borderPane.setRight(timerCounter.getNode());
        borderPane.setLeft(bombCounter.getNode());
        borderPane.setCenter(resetButton);
    }

    /**
     * Returns a field given its coordinates.
     * @param x X-coordinate.
     * @param y Y-coordinate.
     * @return A field of given coordinates.
     */
    private Field getField(int x, int y){
        return fields.get(y).get(x);
    }

    /**
     * Setups stage properties.
     */
    private void setupStage() {
        stage.show();
        stage.setMinWidth(scene.getWidth());
        stage.setMinHeight(scene.getHeight());
        stage.getIcons().add(new Image("/minesweeper/view/resources/game_board/bomb.jpg"));
        stage.setResizable(false);
    }

    /**
     * Loads FXML file and sets scene.
     * @throws Exception Raised by FXMLLoader.
     */
    private void setScene() throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../view/game.fxml"));
        stage.setTitle("Minesweeper");
        scene = new Scene(root);
        stage.setScene(scene);
    }

    /**
     * Translate coordinates to string - (x, y) format.
     * @param x X-coordinate.
     * @param y Y-coordinate.
     * @return String representation.
     */
    private String translateCoordinatesToId(int x, int y){
        return Integer.toString(x) + ", " + Integer.toString(y);
    }

    /**
     * Creates game board fields.
     * @param height Height of a board.
     * @param width Width of a board.
     * @return 2D array of created fields.
     */
    private ArrayList<ArrayList<Field>> createFields(int height, int width){
        ArrayList<ArrayList<Field>> fields = new ArrayList<>();

        int size = 30;
        for(int i = 0; i < height; ++i){
            ArrayList<Field> row = new ArrayList<>();
            fields.add(row);
            for(int j = 0; j < width; ++j){
                Field field = new Field();
                field.setFitWidth(size);
                field.setFitHeight(size);
                field.setId(translateCoordinatesToId(j, i));
                field.setOnMouseClicked(new ButtonPressedHandler());
                row.add(field);
            }
        }

        return fields;
    }

    /**
     * Setups game grid.
     * @param height Height of a board.
     * @param width Width of a board.
     */
    private void setupGameGrid(int height, int width){
        GridPane grid = new GridPane();
        fields = createFields(height, width);

        for(int i = 0; i < fields.size(); ++i){
            ArrayList<Field> row = fields.get(i);
            for(int j = 0; j < row.size(); ++j){
                Field field = row.get(j);
                grid.add(field, j, i);
            }
        }

        VBox mainVBox = (VBox)scene.lookup("#mainVBox");
        grid.setAlignment(Pos.CENTER);
        mainVBox.getChildren().add(grid);
    }

    /**
     * A current stage.
     */
    private Stage stage;
    /**
     * A current scene.
     */
    private Scene scene;
    /**
     * A grid observable.
     */
    private Observable gridObservable;
    /**
     * A logger object.
     */
    private Logger logger;
    /**
     * 2D array of fields.
     */
    private ArrayList<ArrayList<Field>> fields;
    /**
     * TimerCounter and BombCounter current values.
     */
    private Counter timerCounter, bombCounter;
    /**
     * Reset button image view.
     */
    private ImageView resetButton;

    /**
     * Field pressed handler.
     */
    class ButtonPressedHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent event) {
            logger.log("button clicked.");
            gridObservable.notifyObservers(event);
        }
    }

    /**
     * Grid observable.
     */
    class GridObservable extends Observable{
        public void notifyObservers(Object args){
            setChanged();
            super.notifyObservers(args);
        }
    }
}
