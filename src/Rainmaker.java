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
import javafx.scene.Group;
import javafx.animation.AnimationTimer;
import javafx.scene.transform.Rotate;
import java.util.*;

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
        for(Node n : getChildren()) {
            if (n instanceof Updatable)
                ((Updatable) n).update();
        }
    }
}
class GameApp extends Application {
    Game game = new Game();
    Scene scene = new Scene(game, Rainmaker.WINDOW_WIDTH,
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

        CheckInput(helicopter);
        game.getChildren().add(heliPad);
        game.getChildren().add(helicopter);

        helicopter.setTranslateX(Rainmaker.WINDOW_WIDTH/2);
        helicopter.setTranslateY(Rainmaker.WINDOW_WIDTH/5);

        heliPad.setTranslateX(Rainmaker.WINDOW_WIDTH/2);
        heliPad.setTranslateY(Rainmaker.WINDOW_WIDTH/5);
        // game.update();
        AnimationTimer loop = new AnimationTimer() {
            double old = -1;
            double elapsedTime = 0;
            public void handle(long nano) {
                if (old < 0) old = nano;
                double delta = (nano - old) / 1e9;
                old = nano;
                elapsedTime += delta;
                game.update();









            }
        };

        loop.start();
        stage.show();
    }

    private void CheckInput(Helicopter helicopter) {
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(helicopter.getIgnition()) {
                    if (event.getCode() == KeyCode.UP) {
                        System.out.println("UP");
                        helicopter.speedUp();
                    }
                    if (event.getCode() == KeyCode.LEFT) {
                        helicopter.turn(-10);
                        System.out.println("LEFT");
                    }
                    if (event.getCode() == KeyCode.DOWN) {
                        System.out.println("DOWN");
                        helicopter.slowDown();
                    }
                    if (event.getCode() == KeyCode.RIGHT) {
                        System.out.println("RIGHT");
                        helicopter.turn(10);
                    }
                }
                if(event.getCode() == KeyCode.I){
                    if(Math.abs(helicopter.getSpeed()) <= 0.1){
                        helicopter.toggleIgnition();
                    }

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
        //System.out.println("update test");
    }
}
class GameText extends GameObject implements Updatable{
    private Text text;
    public GameText(String text, Color color) {
        this.text = new Text(text);
        this.text.setScaleY(-1);
        this.text.setFill(color);
        this.text.setFont(Font.font(20));
        this.getChildren().add(this.text);
    }
    public void updateText(String s) {
        //System.out.println("update text");
        text.setText(s);
    }

}


interface Updatable {
    public void update();
}
class Helicopter extends GameObject {
    // private Ellipse helicopterBody = new Ellipse(20,20)
    private Ellipse helicopterBody = new Ellipse(20,20);
    private Rotate pivotPoint = new Rotate();
    private Line pointerLine = new Line(0,0,0,40);
    private BoundingBox heliBB = new BoundingBox(0,0,50,50);
    // private Rectangle heliVisibleBB = new Rectangle(50,50);
    private int feul = 25000;
    private double direction = 0;
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
        feulText.setUserData("FuelText");
        //pivotPoint.setPivotX(0);
        //pivotPoint.setPivotY(10);
        //this.getTransforms().addAll(pivotPoint);
        //pivotPoint.setAngle(0);

    }
    public void toggleIgnition(){
        if(feul > 0) {
            ignitionOn = !ignitionOn;
            speed = 0;
        }
    }

    public void speedUp() {
        speed += .1;
        if(speed > 10) {
            speed = 10;
        }

    }
    public void slowDown() {
        speed -= .1;
        if(speed < -2) {
            speed = -2;
        }

    }
    @Override
    public void update() {
        //System.out.println("helicopter update");
        move();
        if(ignitionOn){
            feulText.updateText("F:" + feul);
        }

    }

    private void burnFuel() {
        if(feul > 0) {
            int feulBurned = 5 + (int)(speed * 20);
            feul -= feulBurned;
        }
        if(feul <= 0) {
            feul = 0;
            feulText.updateText("F:" + feul);
            ignitionOn = false;
        }
    }

    private void move() {
        if(feul > 0) {
            setTranslateX(getTranslateX() + Math.sin(direction) * speed);
            setTranslateY(getTranslateY() + Math.cos(direction) * speed);
            burnFuel();
        }
    }
    public void turn(double turnAmount) {
        setRotate(getRotate() - turnAmount);
        turnAmount = Math.toRadians(turnAmount);
        direction = (direction + turnAmount) % 360;
        System.out.println(direction);

    }

    public boolean getIgnition() {
        return ignitionOn;
    }

    public double getSpeed() {
        return speed;
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

}
class Cloud extends GameObject {
    private Ellipse cloudShape = new Ellipse(0,0, 20,20);
    public Cloud() {
        cloudShape.setFill(Color.WHITE);
    }
}
class Pond extends GameObject {
    private Ellipse pondShape = new Ellipse(0,0, 20,20);
    public Pond() {
        pondShape.setFill(Color.BLUE);
    }

}

