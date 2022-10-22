import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Label;
import javafx.scene.Group;
import javafx.animation.AnimationTimer;
import javax.sound.midi.*;

/**
 * Rainmaker game
 */

public class Rainmaker extends Application {
    // Constants
    public static final int WINDOW_WIDTH = 700;
    public static final int WINDOW_HEIGHT = 900;


    /* Called automatically when application is set up */
    public void start(Stage stage) {
        // Add to root's children to make objects visible
        Group root = new Group();
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        // set up the scene
        stage.setScene(scene);
        stage.setTitle("Rainmaker");
        scene.setFill(Color.WHITE);
        // GameHandler object manages the rules and conditions of the game
        GameHandler g = new GameHandler();


        // pressing I and S toggle the fps display and sound
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getText().equalsIgnoreCase("I")){

                }
            }
        });

        AnimationTimer loop = new AnimationTimer() {
            double old = -1;
            double elapsedTime = 0;
            public void handle(long nano) {
                if (old < 0) old = nano;
                double delta = (nano - old) / 1e9;
                old = nano;
                elapsedTime += delta;

            }

        };
        loop.start();
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
abstract class GameObject {

}

class Helicopter extends GameObject {

}
class HeliPad extends GameObject {

}
class Cloud extends GameObject {

}
class Pond extends GameObject {

}

class GameHandler {
    public GameHandler() {

    }
    public void reset() {

    }

}