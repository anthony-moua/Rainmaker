import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
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
import java.util.ArrayList;

/**
 * Rainmaker game
 */

public class Rainmaker extends Application {
    // Constants
    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 800;
    //GameApp gameApp = new GameApp();

    public void start(Stage stage) {
        GameApp.getGameApp().start(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
class GameApp extends Application {
    private static final GameApp gameApp = new GameApp();
    private Game game;
    private Scene scene;
    private Helicopter helicopter;
    private HeliPad heliPad;

    private GameApp () {
        initializeGameAndScene();
        InitializeGameObjects();
    }
    private void initializeGameAndScene() {
        game = Game.getGame();
        scene = new Scene(game, Rainmaker.WINDOW_WIDTH,
                Rainmaker.WINDOW_HEIGHT);
    }
    private void InitializeGameObjects() {
        Position heliStartPosition =
                new Position((double)Rainmaker.WINDOW_WIDTH/2, (double)Rainmaker.WINDOW_WIDTH/5);
        helicopter = new Helicopter(heliStartPosition);
        heliPad = new HeliPad(heliStartPosition);
    }
    public static GameApp getGameApp() {
        return gameApp;
    }
    public void reset() {
    helicopter.reset();
    Ponds.reset();
    Clouds.reset();
    game.reset();
    }
    @Override
    public void start(Stage stage) {
        setUpStage(stage);
        createInputHandlers();
        addObjectsToGame();
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
    private void addObjectsToGame() {
        game.setBackground(BackgroundObject.getBackground());
        game.getChildren().add(Ponds.getPonds());
        game.getChildren().add(Clouds.getClouds());
        for (Cloud cloud : Clouds.getCloudList()) {
            game.getChildren().add(cloud.getBoundingBox().getVisibleBoundingBox());
        }
        game.getChildren().add(heliPad);
        game.getChildren().add(helicopter);
        game.getChildren().add(helicopter.getBoundingBox().getVisibleBoundingBox());
    }
    private void setUpStage(Stage stage) {
        game.setScaleY(-1);
        stage.setScene(scene);
        stage.setTitle("Rainmaker");
        scene.setFill(Color.BLACK);
    }
    private void createInputHandlers() {
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (helicopter.getIgnition()) {
                    if (event.getCode() == KeyCode.UP) {
                        helicopter.speedUp();
                    }
                    if (event.getCode() == KeyCode.LEFT) {
                        helicopter.turnHelicopter(-10);
                    }
                    if (event.getCode() == KeyCode.DOWN) {
                        helicopter.slowDown();
                    }
                    if (event.getCode() == KeyCode.RIGHT) {
                        helicopter.turnHelicopter(10);
                    }
                    if (event.getCode() == KeyCode.SPACE) {
                        helicopter.seedCloud();
                    }
                }
                if (event.getCode() == KeyCode.I) {
                    if (Math.abs(helicopter.getSpeed()) <= 0.1) {
                        helicopter.toggleIgnition();
                    }
                }
                if (event.getCode() == KeyCode.B) {
                    helicopter.toggleBoundingBoxDisplay();
                    Clouds.toggleBoundingBoxDisplay();
                }
                if (event.getCode() == KeyCode.R) {
                    reset();
                }
            }
        });
    }
}
class Game extends Pane implements Updatable {
    private static Game game = new Game();
    private Game () {

    }

    public static Game getGame() {
        return game;
    }

    @Override
    public void update() {
        for (Node n : getChildren()) {
            if (n instanceof Updatable) {
                ((Updatable) n).update();
            }
        }
    }
    public void reset() {
        System.out.println("reset game");
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
    void update();
}
interface Observer {
    void updateObserve();
    // public void update(Observable obsu)
}
class GameBoundingBox implements Updatable{
    private Rectangle visibleBoundingBox;
    private BoundingBox invisibleBoundingBox;
    private boolean displayBoundingBox = false;

    public GameBoundingBox (Rectangle bounds) {
        this.visibleBoundingBox = bounds;
        System.out.println(bounds.getBoundsInLocal());
        this.invisibleBoundingBox =
                new BoundingBox(bounds.getBoundsInLocal().getMinX(),
                        bounds.getBoundsInLocal().getMinY(),
                        bounds.getBoundsInLocal().getWidth(),
                        bounds.getBoundsInLocal().getHeight());
        this.visibleBoundingBox.setFill(Color.TRANSPARENT);
        this.visibleBoundingBox.setStroke(Color.TRANSPARENT);
        //this.visibleBoundingBox.setStroke(Color.WHITE);
    }
    public Rectangle getVisibleBoundingBox() {
        return visibleBoundingBox;
    }

    public BoundingBox getInvisibleBoundingBox() {
        return invisibleBoundingBox;
    }

    public boolean isDisplayBoundingBox() {
        return displayBoundingBox;
    }

    @Override
    public void update() {

    }

    public void toggle() {
        //System.out.println("toggle called for " + this);
        displayBoundingBox = !displayBoundingBox;
        if(displayBoundingBox) {
            visibleBoundingBox.setStroke(Color.RED);
        }
        else {
            visibleBoundingBox.setStroke(Color.TRANSPARENT);
        }
    }

    public void setPosition(Position newPosition) {
        this.visibleBoundingBox.setTranslateX(newPosition.xPos());
        this.visibleBoundingBox.setTranslateY(newPosition.yPos());
        //System.out.println(this + " " + this.getVisibleBoundingBox()
        // .getBoundsInLocal());

        this.invisibleBoundingBox =
                new BoundingBox(visibleBoundingBox.getTranslateX()-(visibleBoundingBox.getWidth()/2),
                        visibleBoundingBox.getTranslateY()-(visibleBoundingBox.getHeight()/2),
                        visibleBoundingBox.getBoundsInLocal().getWidth(),
                        visibleBoundingBox.getBoundsInLocal().getHeight());

        /*
        this.visibleBoundingBox.setWidth(200);
        this.visibleBoundingBox.setHeight(200);
        this.invisibleBoundingBox =
                new BoundingBox(visibleBoundingBox.getTranslateX()-(visibleBoundingBox.getWidth()),
                        visibleBoundingBox.getTranslateY()-(visibleBoundingBox.getHeight()),
                        200, 200);

         */
        //System.out.println("visibleBoundingBox.getTranslateX() = " +
        // visibleBoundingBox.getTranslateX());
        //System.out.println("visibleBoundingBox.getWidth() = " +
        // visibleBoundingBox.getWidth());
    }
}
class Helicopter extends GameObject {
    private Ellipse helicopterBody;
    private Rotate pivotPoint = new Rotate();
    private GameBoundingBox boundingBox;
    private Line pointerLine;
    private GameText feulText;
    private Position startPosition;
    private Position currentPosition;
    private int feul = 250000;
    private double direction = 0;
    private double speed = 0;
    private boolean onHelipad = true;
    private boolean ignitionOn = false;
    private boolean onCloud;
    private int currentCloudNumber = -1;

    public Helicopter(Position startPosition) {
        this.startPosition = startPosition;
        this.currentPosition = startPosition;
        buildHelicopter();
        initializeHelicopterPosition(startPosition);
        this.boundingBox.setPosition(currentPosition);

        //this.setRotate(this.getRotate());

        /*
        this.pivotPoint.setPivotX(0);
        this.pivotPoint.setPivotY(10);
        this.getTransforms().addAll(pivotPoint);
        //this.pivotPoint.setAngle(0);
        */
    }

    private void buildHelicopter() {
        this.helicopterBody = new Ellipse(0,0,20,20);
        this.helicopterBody.setFill(Color.YELLOW);
        this.pointerLine = new Line(0,0,0,40);
        this.pointerLine.setStroke(Color.YELLOW);
        this.pointerLine.setStrokeWidth(2);
        this.feulText = new GameText("F:" + feul, Color.YELLOW);


        add(this.helicopterBody);
        add(this.pointerLine);


        Rectangle heliBounds = new Rectangle(this.getBoundsInLocal().getMinX(),
                this.getBoundsInLocal().getMinY(),
                this.getBoundsInLocal().getWidth(),
                this.getBoundsInLocal().getHeight());
        this.boundingBox = new GameBoundingBox(heliBounds);
        //add(this.heliBoundingBox.getVisibleBoundingBox());

        add(this.feulText);
        this.feulText.setTranslateY(this.feulText.getTranslateY() - 30);
        this.feulText.setTranslateX(this.feulText.getTranslateX() - 30);
    }

    private void initializeHelicopterPosition(Position startPosition) {
        this.setTranslateX(startPosition.xPos());
        this.setTranslateY(startPosition.yPos());
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
        this.boundingBox.setPosition(currentPosition);
        //System.out.println("helicopter currentPosition = " +
        // currentPosition.xPos() +
                //", " + currentPosition.yPos());
    }

    public void toggleIgnition() {
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
            setTranslateX(this.getTranslateX() + Math.sin(direction) * speed);
            setTranslateY(this.getTranslateY() + Math.cos(direction) * speed);
            updateBoundingBoxPosition();
            burnFuel();
        }
    }

    private void updateBoundingBoxPosition() {
        this.currentPosition = new Position(
                this.getTranslateX(),
                this.getTranslateY());
        this.boundingBox.setPosition(currentPosition);
    }

    public void turnHelicopter(double turnAmount) {
        setRotate(getRotate() - turnAmount);
        direction = (direction + Math.toRadians(turnAmount)) % 360;
        reshapeBoundingBox(turnAmount);

    }
    private void reshapeBoundingBox(double turnAmount) {
        /*
        getChildren().remove(visibleBoundingBox);
        visibleBoundingBox.setRotate
                (visibleBoundingBox.getRotate()+turnAmount);

        visibleBoundingBox.setX(getBoundsInLocal().getMinX());
        visibleBoundingBox.setY(getBoundsInLocal().getMinY());
        visibleBoundingBox.setWidth(getBoundsInLocal().getWidth());
        visibleBoundingBox.setHeight(getBoundsInLocal().getHeight());
        getChildren().add(visibleBoundingBox);
        //System.out.println(helicopterVisibleBoundingBox.getBoundsInLocal());
        //System.out.println(this.getTranslateX());
         */
    }
    private void checkCollision() {
        for(Cloud cloud : Clouds.getCloudList()) {
            boolean collidingWithCloud = boundingBox.getInvisibleBoundingBox().
                    intersects(cloud.getBoundingBox().getInvisibleBoundingBox());
            if (collidingWithCloud) {
                //System.out.println("Helicopter and cloud number "+
                 //.getCloudNumber() +" have collided");
                this.onCloud = true;
                this.currentCloudNumber = cloud.getCloudNumber();
                return;
            }
        }
        //System.out.println("No collision");
        this.onCloud = false;
        this.currentCloudNumber = -1;
    }
    public boolean getIgnition() {
        return ignitionOn;
    }

    public double getSpeed() {
        return speed;
    }

    public void toggleBoundingBoxDisplay() {
        boundingBox.toggle();
    }

    public void seedCloud() {
        if(this.onCloud) {

            for(Cloud cloud : Clouds.getCloudList()) {
                System.out.println("test 1");
                if(cloud.getCloudNumber() == this.currentCloudNumber) {
                    System.out.println("test 2");
                    cloud.activateSeeding();
                    break;
                }
            }
        }
        else {
            // do nothing
        }
    }

    public void reset() {
        initializeHelicopterPosition(this.startPosition);
        this.setRotate(0);
        this.speed = 0;
        this.direction = 0;
        this.feul = 250000;
        feulText.updateText("F:" + feul);
        this.ignitionOn = false;

    }

    public GameBoundingBox getBoundingBox() {
        return boundingBox;
    }
}
class HeliPad extends GameObject {
    private Rectangle helipadOutline = new Rectangle(200,200);
    private Ellipse helipadCircle = new Ellipse(75,75);
    public HeliPad(Position startPosition) {
        initializeHelipadPosition(startPosition);

        this.helipadOutline.setFill(Color.TRANSPARENT);
        this.helipadOutline.setStroke(Color.WHITE);
        this.helipadOutline.setStrokeWidth(2);
        this.helipadCircle.setFill(Color.TRANSPARENT);
        this.helipadCircle.setStroke(Color.WHITE);
        this.helipadCircle.setStrokeWidth(2);


        this.helipadOutline.setTranslateX(this.helipadOutline.getX() -
                this.helipadOutline.getWidth() / 2);
        this.helipadOutline.setTranslateY(this.helipadOutline.getY() -
                this.helipadOutline.getHeight() / 2);

        add(this.helipadOutline);
        add(this.helipadCircle);
    }

    private void initializeHelipadPosition(Position startPosition) {
        this.setTranslateX(startPosition.xPos());
        this.setTranslateY(startPosition.yPos());
    }

}
class Clouds extends GameObject implements Updatable {
    private static Clouds clouds = new Clouds(5);
    private static ArrayList<Cloud> cloudList;

    private Clouds(int numberOfClouds) {
        cloudList = new ArrayList<>(numberOfClouds);
        for (int i = 0; i < numberOfClouds; i++) {
            Position cloudStartPosition = new Position(Math.random()*Rainmaker.WINDOW_WIDTH,
                    Math.random()*Rainmaker.WINDOW_WIDTH);
            Cloud c = new Cloud(cloudStartPosition);
            cloudList.add(c);
            this.getChildren().add(c);
        }
    }

    public static ArrayList<Cloud> getCloudList() {
        return cloudList;
    }

    public static void reset() {
        for(Cloud cloud : cloudList) {
            cloud.reset();
        }
    }

    public static void toggleBoundingBoxDisplay() {
        for (Cloud cloud:cloudList) {
            cloud.toggleBoundingBoxDisplay();
        }
    }

    public static Clouds getClouds() {
        return clouds;
    }

    public void update() {
        for (Cloud cloud : this.cloudList)
            cloud.update();
    }
}
class Cloud extends GameObject implements Updatable, Observer{
    private int cloudNumber;
    private GameText cloudText;
    private Ellipse cloudShape;
    private GameBoundingBox boundingBox;
    private Color cloudColor;
    private Position currentPosition;
    private int SeedValue = 0;
    public Cloud(Position cloudStartPosition) {
        this.cloudNumber = Clouds.getCloudList().size();
        this.currentPosition = cloudStartPosition;
        initializeCloudPosition(cloudStartPosition);
        buildCloud();
    }

    private void buildCloud() {
        this.cloudColor = Color.WHITE;
        this.cloudShape = new Ellipse(0,0, 60,40);
        this.cloudShape.setFill(this.cloudColor);
        this.cloudText = new GameText(this.SeedValue + "%", Color.BLACK);
        add(this.cloudShape);


        Rectangle cloudBounds = new Rectangle(this.getBoundsInLocal().getMinX(),
                this.getBoundsInLocal().getMinY(),
                this.getBoundsInLocal().getWidth(),
                this.getBoundsInLocal().getHeight());
        this.boundingBox = new GameBoundingBox(cloudBounds);
        add(this.cloudText);
        this.cloudText.setTranslateX(-20);
        this.cloudText.setTranslateY(10);
    }

    private void initializeCloudPosition(Position cloudStartPosition) {
        this.setTranslateX(cloudStartPosition.xPos());
        this.setTranslateY(cloudStartPosition.yPos());
    }

    @Override
    public void update() {
        if(SeedValue > 30) {
            this.rain();
        }
        cloudText.updateText(SeedValue + "%");
        this.boundingBox.setPosition(currentPosition);
        this.cloudColor = Color.rgb(255 - SeedValue,255 - SeedValue,
                255 - SeedValue);
        this.cloudShape.setFill(cloudColor);
        //System.out.println("cloud " + this.cloudNumber + "currentPosition =
        // " + currentPosition.xPos() + ", " + currentPosition.yPos());
    }

    private void rain() {
        //loop through list of ponds
            //find the closest pond unfilled pond
                // fill that pond

        /*
        for(Node n : this.getParent().getChildrenUnmodifiable()) {
            if(n instanceof Pond) {
                ((Pond)n).fillPond();
            }
        }
         */

    }

    public Ellipse getCloudShape() {
        return cloudShape;
    }
    public int getCloudNumber() {
        return this.cloudNumber;
    }

    public void toggleBoundingBoxDisplay() {
        this.boundingBox.toggle();
    }

    public void activateSeeding() {
        //System.out.println("cloud # " + this.cloudNumber + "seeding");
        if (SeedValue < 100) {
            SeedValue++;
        }
    }
    @Override
    public void updateObserve() {

    }

    public void reset() {
        this.SeedValue = 0;
        cloudText.updateText(SeedValue + "%");
        this.cloudColor = Color.WHITE;
        Position cloudStartPosition = new Position(
                Math.random()*Rainmaker.WINDOW_WIDTH,
                Math.random()*Rainmaker.WINDOW_WIDTH);
        currentPosition = cloudStartPosition;
                initializeCloudPosition(cloudStartPosition);
        this.boundingBox.setPosition(cloudStartPosition);
    }

    public GameBoundingBox getBoundingBox() {
        return boundingBox;
    }
}
class Wind extends GameObject implements Updatable{
    private static WindParameters windParameters;
    private static final Wind wind = new Wind(windParameters);

    private Wind(WindParameters windParameters) {

    }
    public static Wind getWind() {
        return wind;
    }
    @Override
    public void update() {

    }
}
class WindParameters {
    private int windSpeed = 0;
    private int windDirectonX;
    private int windDirectonY;
}
class Ponds extends GameObject implements Updatable {
    private static Ponds ponds = new Ponds(3);
    private static ArrayList<Pond> pondList;

    private Ponds(int numberOfPonds) {
        pondList = new ArrayList<>(numberOfPonds);
        for (int i = 0; i < numberOfPonds; i++) {
            Position pondStartPosition =
                    new Position(Math.random()*Rainmaker.WINDOW_WIDTH,
                    Math.random()*Rainmaker.WINDOW_WIDTH);
            Pond p = new Pond(pondStartPosition);
            pondList.add(p);
            this.getChildren().add(p);
        }
    }

    public static ArrayList<Pond> getPondList() {
        return pondList;
    }

    public static void reset() {
        for (Pond pond : pondList) {
            pond.reset();
        }
    }
    public static Ponds getPonds() {
        return ponds;
    }

    public void update() {
        for (Pond pond : this.pondList)
            pond.update();
    }
}
class Pond extends GameObject implements Updatable{
    private GameText pondText;
    private Ellipse pondShape;
    //private Color pondColor;
    private int pondFill = 0;
    private boolean filled = false;

    public Pond(Position startPosition) {
        initializePondPosition(startPosition);
        this.pondShape = new Ellipse(0,0, 20,20);
        this.pondText = new GameText(this.pondFill + "%", Color.WHITE);
        this.pondShape.setFill(Color.BLUE);
        add(this.pondShape);
        add(this.pondText);

        this.pondText.setTranslateX(-10);
        this.pondText.setTranslateY(10);


    }

    private void initializePondPosition(Position startPosition) {
        this.setTranslateX(startPosition.xPos());
        this.setTranslateY(startPosition.yPos());
    }

    @Override
    public void update() {
        if(true) { // if being filled maybe?
            pondText.updateText(pondFill + "%");
        }
    }

    public void fillPond() {
        if(this.pondFill < 100) {
            this.pondFill += 1;
        }
        else {
            pondFill = 100;
            filled = true;
        }
    }

    public void reset() {
        this.pondFill = 0;
        pondText.updateText(pondFill + "%");
        Position pondStartPosition =
                new Position(Math.random()*Rainmaker.WINDOW_WIDTH,
                        Math.random()*Rainmaker.WINDOW_WIDTH);
        initializePondPosition(pondStartPosition);
    }
}
class BackgroundObject extends GameObject {
    private static BackgroundObject backgroundObject= new BackgroundObject();
    private static Background background;
    private static BackgroundImage backgroundImage;

    private BackgroundObject() {
        backgroundImage = new BackgroundImage(new Image("background.png",
                Rainmaker.WINDOW_WIDTH,Rainmaker.WINDOW_HEIGHT,false,true),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);

    }

    public static BackgroundObject getBackgroundObject() {
        return backgroundObject;
    }
    public static Background getBackground() {
        return new Background(backgroundImage);
    }
}

record Position(double xPos, double yPos) {}