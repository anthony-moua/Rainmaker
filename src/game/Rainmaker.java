package game;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * game.Rainmaker game
 */

public class Rainmaker extends Application {
    // Constants
    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 800;
    //GameObjects.BackgroundObject.game.GameApp gameApp = new GameObjects.BackgroundObject.game.GameApp();

    public void start(Stage stage) {
        GameApp.getGameApp().start(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

