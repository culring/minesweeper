package minesweeper.view;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
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

public class View {
    public View(Stage stage, int height, int width, int bombs) throws Exception{
        this.stage = stage;
        gridObservable = new GridObservable();
        logger = new Logger("View");

        setScene();
        setupTopBar(bombs);
        setupGameGrid(height, width);
        setupStage();
    }

    public void addButtonObserver(Observer observer){
        gridObservable.addObserver(observer);
    }

    public void addResetButtonHandler(EventHandler<MouseEvent> handler){
        resetButton.setOnMouseClicked(handler);
    }

    public void mark(int x, int y){
        getField(x, y).set(Field.Type.MARKED);
    }

    public void unmark(int x, int y){
        getField(x, y).set(Field.Type.UNMARKED);
    }

    public void showBomb(int x, int y){
        getField(x, y).set(Field.Type.BOMB);
    }

    public void showBombDetonated(int x, int y){
        getField(x, y).set(Field.Type.BOMB_DETONATED);
        resetButton.setImage(new Image("/minesweeper/view/resources/game_board/bomb_detonated.jpg",
                50, 0, true, true));
    }

    public void setFieldValue(int x, int y, int value){
        if(value == 0){
            getField(x, y).set(Field.Type.REVERSED);
            return;
        }
        getField(x, y).set(value);
    }

    public void setTimer(int seconds){
        Platform.runLater(() -> timerCounter.setValue(seconds));
    }

    public void setCounter(int value){
        bombCounter.setValue(value);
    }

    public void victory(){
        resetButton.setImage(new Image("/minesweeper/view/resources/game_board/bomb_green.jpg",
                50, 0, true, true));
    }

    public void setEasyMenuItemHandler(EventHandler<ActionEvent> handler){
        MenuBar menuBar = (MenuBar) scene.lookup("#menuBar");
        MenuItem menuItem = menuBar.getMenus().get(0).getItems().get(0);
        menuItem.setOnAction(handler);
    }

    public void setMediumItemHandler(EventHandler<ActionEvent> handler){
        MenuBar menuBar = (MenuBar) scene.lookup("#menuBar");
        MenuItem menuItem = menuBar.getMenus().get(0).getItems().get(1);
        menuItem.setOnAction(handler);
    }

    public void setHardItemHandler(EventHandler<ActionEvent> handler){
        MenuBar menuBar = (MenuBar) scene.lookup("#menuBar");
        MenuItem menuItem = menuBar.getMenus().get(0).getItems().get(2);
        menuItem.setOnAction(handler);
    }

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

    private Field getField(int x, int y){
        return fields.get(y).get(x);
    }

    private void setupStage() {
        stage.show();
        stage.setMinWidth(scene.getWidth());
        stage.setMinHeight(scene.getHeight());
        stage.getIcons().add(new Image("/minesweeper/view/resources/game_board/bomb.jpg"));
        stage.setResizable(false);
    }

    private void setScene() throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../view/game.fxml"));
        stage.setTitle("Minesweeper");
        scene = new Scene(root);
        stage.setScene(scene);
    }

    private String translateCoordinatesToId(int x, int y){
        return Integer.toString(x) + ", " + Integer.toString(y);
    }

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

    private Stage stage;
    private Scene scene;
    private Observable gridObservable;
    private Logger logger;
    private ArrayList<ArrayList<Field>> fields;
    private Counter timerCounter, bombCounter;
    private ImageView resetButton;

    class ButtonPressedHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent event) {
            logger.log("button clicked.");
            gridObservable.notifyObservers(event);
        }
    }

    class GridObservable extends Observable{
        public void notifyObservers(Object args){
            setChanged();
            super.notifyObservers(args);
        }
    }
}
