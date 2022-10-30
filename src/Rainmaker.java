import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.Node;
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
    GameApp gameApp = new GameApp();

    public void start(Stage stage) {
        gameApp.start(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
class GameApp extends Application {
    static Group root = new Group();
    static Scene scene = new Scene(root, Rainmaker.WINDOW_WIDTH,
            Rainmaker.WINDOW_HEIGHT);
    public void reset() {

    }

    @Override
    public void start(Stage stage) {
        // Add to root's children to make objects visible

        // set up the scene
        stage.setScene(scene);
        stage.setTitle("Rainmaker");
        scene.setFill(Color.BLACK);
        GameApp.CheckInput();


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

    private static void CheckInput() {
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.UP){
                    System.out.println("UP");
                }
                if(event.getCode() == KeyCode.LEFT){
                    System.out.println("LEFT");
                }
                if(event.getCode() == KeyCode.DOWN){
                    System.out.println("DOWN");
                }
                if(event.getCode() == KeyCode.RIGHT){
                    System.out.println("RIGHT");
                }
                if(event.getCode() == KeyCode.I){

                }
                if(event.getCode() == KeyCode.B){

                }
                if(event.getCode() == KeyCode.R){

                }
            }
        });
    }
}
abstract class GameObject extends Group implements Updatable {
    void add (Node node) {this.getChildren().add(node);}
}
interface Updatable {
    public void update();
}
class Helicopter extends GameObject {
    private Ellipse helicopterBody = new Ellipse(0,0, 20,20);
    public Helicopter() {
        this.helicopterBody.setFill(Color.YELLOW);
    }
    @Override
    public void update() {

    }
}
class HeliPad extends GameObject {
    private Rectangle helipadShape = new Rectangle(0,0,40,40);
    public HeliPad() {
        this.helipadShape.setFill(Color.GRAY);
    }
    @Override
    public void update() {

    }
}
class Cloud extends GameObject {
    private Ellipse cloudShape = new Ellipse(0,0, 20,20);
    public Cloud() {
        this.cloudShape.setFill(Color.WHITE);
    }
    @Override
    public void update() {

    }
}
class Pond extends GameObject {
    private Ellipse pondShape = new Ellipse(0,0, 20,20);
    public Pond() {
        this.pondShape.setFill(Color.BLUE);
    }
    @Override
    public void update() {

    }
}

