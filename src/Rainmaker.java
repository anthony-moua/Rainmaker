import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
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
class Game extends Pane implements Updatable{

    @Override
    public void update() {

    }
}
class GameApp extends Application {
    static Game game = new Game();
    static Scene scene = new Scene(game, Rainmaker.WINDOW_WIDTH,
            Rainmaker.WINDOW_HEIGHT);

    public void reset() {

    }

    @Override
    public void start(Stage stage) {
        game.setScaleY(-1);
        // set up the scene
        stage.setScene(scene);
        stage.setTitle("Rainmaker");
        scene.setFill(Color.BLACK);
        

        Helicopter helicopter = new Helicopter();
        HeliPad heliPad = new HeliPad(500, 500);

        GameApp.CheckInput(helicopter);
        game.getChildren().add(heliPad);
        game.getChildren().add(helicopter);
        //System.out.println(game.getChildren());

        helicopter.setTranslateX(Rainmaker.WINDOW_WIDTH/2);
        helicopter.setTranslateY(Rainmaker.WINDOW_WIDTH/5);

        heliPad.setTranslateX(Rainmaker.WINDOW_WIDTH/2);
        heliPad.setTranslateY(Rainmaker.WINDOW_WIDTH/5);
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

    private static void CheckInput(Helicopter helicopter) {
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.UP){
                    helicopter.move();
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

    @Override
    public void update() {

    }
}

class GameText extends GameObject {
    private Text text;
    public GameText(String text, Color color) {
        this.text = new Text(text);
        this.text.setScaleY(-1);
        this.text.setFill(color);
        this.text.setFont(Font.font(20));
        this.getChildren().add(this.text);
    }
}


interface Updatable {
    public void update();
}
class Helicopter extends GameObject {
    private Ellipse helicopterBody = new Ellipse(20,20);
    private Line pointerLine = new Line(0,0,0,40);
    private BoundingBox heliBB = new BoundingBox(0,0,50,50);
    // private Rectangle heliVisibleBB = new Rectangle(50,50);
    private int feul = 25000;
    private GameText feulText = new GameText("F:" + feul, Color.YELLOW);
    private boolean onHelipad = true;
    private double speed = 0;
    private boolean ignitionOn = false;
    public Helicopter() {
        helicopterBody.setFill(Color.YELLOW);
        pointerLine.setStroke(Color.YELLOW);
        pointerLine.setStrokeWidth(2);
        add(helicopterBody);
        add(pointerLine);
        add(feulText);
        feulText.setTranslateX(feulText.getTranslateX() - 30);
        feulText.setTranslateY(feulText.getTranslateY() - 30);

    }
    public void toggleIgnition(){
        ignitionOn = !ignitionOn;
    }
    @Override
    public void update() {
        this.setTranslateY(this.getTranslateY() + speed);
    }

    public void move() {
        if(speed < 10) {
            speed += .1;
        }
    }
}
class HeliPad extends GameObject {
    private Rectangle helipadOutline = new Rectangle(200,200);
    private Ellipse helipadCircle = new Ellipse(75,75);
    public HeliPad(int x, int y) {
        helipadOutline.setFill(Color.TRANSPARENT);
        helipadOutline.setStroke(Color.WHITE);
        helipadOutline.setStrokeWidth(2);
        helipadCircle.setFill(Color.TRANSPARENT);
        helipadCircle.setStroke(Color.WHITE);
        helipadCircle.setStrokeWidth(2);
        add(helipadOutline);
        add(helipadCircle);

        helipadOutline.setTranslateX(helipadOutline.getX() -
                helipadOutline.getWidth() / 2);
        helipadOutline.setTranslateY(helipadOutline.getY() -
                helipadOutline.getHeight() / 2);

        this.setTranslateX(x);
        this.setTranslateY(y);

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

