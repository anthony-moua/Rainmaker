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
import javafx.geometry.BoundingBox;

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
            if (n instanceof Updatable) {
                ((Updatable) n).update();
            }
        }
    }

    public void reset() {
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
        Pond pond = new Pond(Math.random()*Rainmaker.WINDOW_WIDTH,
                Math.random()*Rainmaker.WINDOW_WIDTH);
        Cloud cloud = new Cloud(Math.random()*Rainmaker.WINDOW_WIDTH,
                Math.random()*Rainmaker.WINDOW_WIDTH);

        CheckInput();

        game.getChildren().add(pond);
        game.getChildren().add(cloud);
        game.getChildren().add(heliPad);
        game.getChildren().add(helicopter);

        helicopter.setTranslateX((double)Rainmaker.WINDOW_WIDTH/2);
        helicopter.setTranslateY((double)Rainmaker.WINDOW_WIDTH/5);

        heliPad.setTranslateX((double)Rainmaker.WINDOW_WIDTH/2);
        heliPad.setTranslateY((double)Rainmaker.WINDOW_WIDTH/5);
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

    private void CheckInput() {
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                for (Node n : game.getChildren()) {
                    if(n instanceof Helicopter) {
                        if (((Helicopter)n).getIgnition()) {
                            if (event.getCode() == KeyCode.UP) {
                                ((Helicopter)n).speedUp();
                            }
                            if (event.getCode() == KeyCode.LEFT) {
                                ((Helicopter)n).turnHelicopter(-10);
                            }
                            if (event.getCode() == KeyCode.DOWN) {
                                ((Helicopter)n).slowDown();
                            }
                            if (event.getCode() == KeyCode.RIGHT) {
                                ((Helicopter)n).turnHelicopter(10);
                            }
                            if (event.getCode() == KeyCode.SPACE) {
                                ((Helicopter)n).seedCloud();
                            }
                        }
                    }
                    if (event.getCode() == KeyCode.I) {
                        if (Math.abs(((Helicopter)n).getSpeed()) <= 0.1) {
                            ((Helicopter)n).toggleIgnition();
                        }

                    }
                    if (event.getCode() == KeyCode.B) {
                        if(n instanceof Helicopter) {
                            ((Helicopter) n).toggleBoundingBoxDisplay();
                        }
                        if(n instanceof Cloud) {
                            ((Cloud) n).toggleBoundingBoxDisplay();
                        }
                    }
                    if (event.getCode() == KeyCode.R) {
                        reset();
                    }
                }
            }
        });
    }
}
abstract class GameObject extends Group implements Updatable {
    void add (Node node) {
        this.getChildren().add(node);
    }

    @Override
    public void update() {
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
        text.setText(s);
    }

}


interface Updatable {
    public void update();
}
class Helicopter extends GameObject {
    private Ellipse helicopterBody;
    private Rotate pivotPoint = new Rotate();
    private Rectangle helicopterBoundingBox;
    private Line pointerLine;

    private GameText feulText;
    private int feul = 25000;
    private double direction = 0;
    private double speed = 0;
    private boolean onHelipad = true;
    private boolean displayBoundingBox = false;
    private boolean ignitionOn = false;
    private boolean onCloud;

    public Helicopter() {
        this.helicopterBody = new Ellipse(0,0,20,20);
        this.helicopterBody.setFill(Color.YELLOW);
        this.pointerLine = new Line(0,0,0,40);
        this.pointerLine.setStroke(Color.YELLOW);
        this.pointerLine.setStrokeWidth(2);
        this.feulText = new GameText("F:" + feul, Color.YELLOW);

        add(this.helicopterBody);
        add(this.pointerLine);
        add(this.feulText);

        this.feulText.setTranslateY(this.feulText.getTranslateY() - 30);
        this.feulText.setTranslateX(this.feulText.getTranslateX() - 30);

        this.setRotate(this.getRotate());

        this.helicopterBoundingBox =
                new Rectangle(this.getBoundsInLocal().getMinX(),
                        this.getBoundsInLocal().getMinY(),
                        this.getBoundsInLocal().getWidth(),
                        this.getBoundsInLocal().getHeight());

        //add(this.helicopterBoundingBox);
        add(this.helicopterBoundingBox);


        this.helicopterBoundingBox.setFill(Color.TRANSPARENT);
        this.helicopterBoundingBox.setStroke(Color.WHITE);


        this.pivotPoint.setPivotX(0);
        this.pivotPoint.setPivotY(10);
        this.getTransforms().addAll(pivotPoint);
        //this.pivotPoint.setAngle(0);

    }
    @Override
    public void update() {
        //System.out.println("helicopter update");
        if(feul > 0) {
            moveHelicopter();
            if (ignitionOn) {
                feulText.updateText("F:" + feul);
            }
        }

        checkCollision();

    }

    public void toggleIgnition(){
        ignitionOn = !ignitionOn;
        speed = 0;
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
    private void burnFuel() {
        if(feul > 0) {
            int fuelBurned = 5 + (int)(speed * 10);
            feul -= fuelBurned;
        }
        if(feul <= 0) {
            feul = 0;
            feulText.updateText("F:" + feul);
            ignitionOn = false;
        }
    }

    private void moveHelicopter() {
        if(feul > 0) {
            setTranslateX(getTranslateX() + Math.sin(direction) * speed);
            setTranslateY(getTranslateY() + Math.cos(direction) * speed);
            burnFuel();
        }
    }
    public void turnHelicopter(double turnAmount) {
        setRotate(getRotate() - turnAmount);
        direction = (direction + Math.toRadians(turnAmount)) % 360;
        reshapeBoundingBox(turnAmount);

    }
    private void reshapeBoundingBox(double turnAmount) {
        getChildren().remove(helicopterBoundingBox);
        helicopterBoundingBox.setRotate
                (helicopterBoundingBox.getRotate()+turnAmount);

        helicopterBoundingBox.setX(getBoundsInLocal().getMinX());
        helicopterBoundingBox.setY(getBoundsInLocal().getMinY());
        helicopterBoundingBox.setWidth(getBoundsInLocal().getWidth());
        helicopterBoundingBox.setHeight(getBoundsInLocal().getHeight());
        getChildren().add(helicopterBoundingBox);
        //System.out.println(helicopterVisibleBoundingBox.getBoundsInLocal());
        //System.out.println(this.getTranslateX());
    }
    private void checkCollision() {
        boolean isIntersecting = false;
        for(Node n : this.getParent().getChildrenUnmodifiable()) {
            if(n instanceof Cloud) {
                if (this.getTranslateX() > n.getTranslateX() - ((Cloud)n).getCloudShape().getRadiusX() &&
                    this.getTranslateY() > n.getTranslateY() - ((Cloud)n).getCloudShape().getRadiusY() &&
                    this.getTranslateX() < n.getTranslateX() + ((Cloud)n).getCloudShape().getRadiusX() &&
                    this.getTranslateY() < n.getTranslateY() + ((Cloud)n).getCloudShape().getRadiusY()
                ) {
                    System.out.println("Helicopter and cloud have collided");
                    isIntersecting = true;
                }
                else{
                    System.out.println("No collision");
                }
                if (isIntersecting)
                {
                    this.onCloud = true;
                }
                else {
                    this.onCloud = false;
                }
            }


        }
    }
    public boolean getIgnition() {
        return ignitionOn;
    }

    public double getSpeed() {
        return speed;
    }

    public void toggleBoundingBoxDisplay() {
        displayBoundingBox = !displayBoundingBox;
        if(displayBoundingBox){
            helicopterBoundingBox.setStroke(Color.WHITE);
        }
        else {
            helicopterBoundingBox.setStroke(Color.TRANSPARENT);
        }
    }

    public void seedCloud() {

    }
}
class HeliPad extends GameObject {
    private Rectangle helipadOutline = new Rectangle(200,200);
    private Ellipse helipadCircle = new Ellipse(75,75);
    public HeliPad(int x, int y) {
        this.helipadOutline.setFill(Color.TRANSPARENT);
        this.helipadOutline.setStroke(Color.WHITE);
        this.helipadOutline.setStrokeWidth(2);
        this.helipadCircle.setFill(Color.TRANSPARENT);
        this.helipadCircle.setStroke(Color.WHITE);
        this.helipadCircle.setStrokeWidth(2);
        add(this.helipadOutline);
        add(this.helipadCircle);

        this.helipadOutline.setTranslateX(this.helipadOutline.getX() -
                this.helipadOutline.getWidth() / 2);
        this.helipadOutline.setTranslateY(this.helipadOutline.getY() -
                this.helipadOutline.getHeight() / 2);

        this.setTranslateX(x);
        this.setTranslateY(y);

    }

}
class Cloud extends GameObject {
    private GameText cloudText;
    private Ellipse cloudShape;
    private Rectangle cloudBoundingBox;
    private Color cloudColor;
    private int cloudSeedValue = 0;
    private boolean displayBoundingBox = false;
    public Cloud(double x, double y) {
        this.setTranslateX(x);
        this.setTranslateY(y);
        this.cloudColor = Color.WHITE;
        this.cloudShape = new Ellipse(0,0, 80,80);
        this.cloudShape.setFill(this.cloudColor);
        this.cloudText = new GameText(this.cloudSeedValue + "%",
                Color.BLACK);
        add(this.cloudShape);
        add(this.cloudText);

        this.cloudBoundingBox =
                new Rectangle(this.getBoundsInLocal().getMinX(),
                        this.getBoundsInLocal().getMinY(),
                        this.getBoundsInLocal().getWidth(),
                        this.getBoundsInLocal().getHeight());

        add(this.cloudBoundingBox);

        this.cloudBoundingBox.setFill(Color.TRANSPARENT);
        this.cloudBoundingBox.setStroke(Color.WHITE);


        this.cloudText.setTranslateX(-20);
        this.cloudText.setTranslateY(10);


    }
    @Override
    public void update() {
        if(false) {
            cloudText.updateText(cloudSeedValue + "%");
        }
    }
    public Rectangle getBoundingBox(){
        return this.cloudBoundingBox;
    }

    public Ellipse getCloudShape() {
        return cloudShape;
    }

    public void toggleBoundingBoxDisplay() {
        displayBoundingBox = !displayBoundingBox;
        if(displayBoundingBox){
            cloudBoundingBox.setStroke(Color.WHITE);
        }
        else {
            cloudBoundingBox.setStroke(Color.TRANSPARENT);
        }
    }
}
class Pond extends GameObject {
    private GameText pondText;
    private Ellipse pondShape;
    //private Color pondColor;
    private int pondFill = 0;

    public Pond(double x, double y) {
        this.setTranslateX(x);
        this.setTranslateY(y);
        this.pondShape = new Ellipse(0,0, 20,20);
        this.pondText = new GameText(this.pondFill + "%", Color.WHITE);
        this.pondShape.setFill(Color.BLUE);
        add(this.pondShape);
        add(this.pondText);

        this.pondText.setTranslateX(-10);
        this.pondText.setTranslateY(10);


    }
    @Override
    public void update() {
        if(false) {
            pondText.updateText(pondFill + "%");
        }
    }
}

