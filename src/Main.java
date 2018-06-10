import javafx.application.Application;
import javafx.stage.Stage;
import minesweeper.controller.GameController;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        GameController controller = new GameController(primaryStage);
    }

    public static void main(String[] args) { launch(args); }
}
