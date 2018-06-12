import javafx.application.Application;
import javafx.stage.Stage;
import minesweeper.controller.GameController;

/**
 * Main class for the minesweeper application.
 */
public class Main extends Application {

    /**
     * This is the first method run in the application.
     * @param primaryStage The primary stage on which all GUI is displayed.
     */
    @Override
    public void start(Stage primaryStage) {
        GameController controller = new GameController(primaryStage);
    }

    /**
     * The main function.
     * @param args Arguments passed to the application.
     */
    public static void main(String[] args) { launch(args); }
}
