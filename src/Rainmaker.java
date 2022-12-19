import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
                new Position((double)Rainmaker.WINDOW_WIDTH/2,
                        (double)Rainmaker.WINDOW_WIDTH/5);
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
            double previousTime = -1;
            public void handle(long nano) {
                if (old < 0) old = nano;
                double delta = (nano - old) / 1e9;
                old = nano;
                elapsedTime += delta;
                game.update();
                if(elapsedTime % 1 < previousTime % 1)
                    game.secondPassed();
                previousTime = elapsedTime;
            }
        };
        loop.start();
        stage.show();
    }
    private void addObjectsToGame() {
        game.setBackground(BackgroundObject.getBackground());
        game.getChildren().add(heliPad);
        game.getChildren().add(Ponds.getPonds());
        game.getChildren().add(Clouds.getClouds());
        for (Cloud cloud : Clouds.getCloudList()) {
            game.getChildren().add(cloud.getBoundingBox().getVisibleBoundingBox());
        }

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
                if (event.getCode() == KeyCode.UP) {
                    helicopter.upPressed();
                }
                if (event.getCode() == KeyCode.LEFT) {
                    helicopter.leftPressed();
                }
                if (event.getCode() == KeyCode.DOWN) {
                    helicopter.downPressed();
                }
                if (event.getCode() == KeyCode.RIGHT) {
                    helicopter.rightPressed();
                }
                if (event.getCode() == KeyCode.SPACE) {
                    helicopter.seedCloud();
                }
                if (event.getCode() == KeyCode.I) {
                    if (Math.abs(helicopter.getSpeed()) <= 0.1
                            && helicopter.onHelipad()) {
                        helicopter.ignition();
                    }
                }
                if (event.getCode() == KeyCode.B) {
                    helicopter.toggleBoundingBoxDisplay();
                    Clouds.toggleBoundingBoxDisplay();
                }
                if (event.getCode() == KeyCode.D) {
                    Clouds.toggleDistanceLines();
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

    }

    public void secondPassed() {
        for (Cloud cloud : Clouds.getCloudList()) {
            if(cloud.getSeedValue() > 0) {
                cloud.decrementSeedValue();
            }
        }
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
    void updateObserver();
    // public void update(Observable obsu)
}
class GameBoundingBox implements Updatable{
    private Rectangle visibleBoundingBox;
    private BoundingBox invisibleBoundingBox;
    private boolean displayBoundingBox = false;

    public GameBoundingBox (Rectangle bounds) {
        this.visibleBoundingBox = bounds;
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
        double xPos = visibleBoundingBox.getTranslateX()
                - (visibleBoundingBox.getWidth()/2);
        double yPos = visibleBoundingBox.getTranslateY()
                - (visibleBoundingBox.getHeight()/2);
        this.invisibleBoundingBox = new BoundingBox(xPos, yPos,
                visibleBoundingBox.getWidth(), visibleBoundingBox.getHeight());
    }
}
abstract class HelicopterState {
    private Helicopter helicopter;
    public HelicopterState (Helicopter helicopter) {
        this.helicopter = helicopter;
    }
    abstract void ignition();

    abstract void burnFuel();

    abstract void spinBlades();

    abstract void moveHelicopter();
    abstract void upPressed();
    abstract void leftPressed();
    abstract void downPressed();
    abstract void rightPressed();
    abstract void spacePressed();
}
class Off extends HelicopterState {
    private Helicopter helicopter;
    public Off(Helicopter helicopter) {
        super(helicopter);
        this.helicopter = helicopter;
    }
    @Override
    public void ignition() {
        this.helicopter.changeState(new Starting(helicopter));
    }
    @Override
    void burnFuel() {
        // don't burn fuel while off
    }
    @Override
    void spinBlades() {
        //blades don't spin
    }
    @Override
    void moveHelicopter() {
        //helicopter doesn't move
    }
    @Override
    void upPressed() {
        // do nothing
    }
    @Override
    void leftPressed() {
        // do nothing
    }
    @Override
    void downPressed() {
        // do nothing
    }
    @Override
    void rightPressed() {
        // do nothing
    }
    @Override
    void spacePressed() {
        // do nothing
    }
}
class Starting extends HelicopterState {
    private Helicopter helicopter;
    public Starting(Helicopter helicopter) {
        super(helicopter);
        this.helicopter = helicopter;
    }
    @Override
    void ignition() {
        this.helicopter.changeState(new Stopping(helicopter));
    }
    @Override
    void burnFuel() {
        helicopter.burnFuel(5);
    }
    @Override
    void spinBlades() {
        this.helicopter.spinUpBlades(.05);
    }
    @Override
    void moveHelicopter() {
        // helicopter doesn't move
    }
    @Override
    void upPressed() {
        // do nothing
    }
    @Override
    void leftPressed() {
        // do nothing
    }
    @Override
    void downPressed() {
        // do nothing
    }
    @Override
    void rightPressed() {
        // do nothing
    }
    @Override
    void spacePressed() {
        // do nothing
    }
}
class Stopping extends HelicopterState {
    private Helicopter helicopter;
    public Stopping(Helicopter helicopter) {
        super(helicopter);
        this.helicopter = helicopter;
    }

    @Override
    void ignition() {
        this.helicopter.changeState(new Starting(helicopter));
    }
    @Override
    void burnFuel() {
        // don't burn fuel while stopping
    }

    @Override
    void spinBlades() {
        this.helicopter.spinUpBlades(-.05);
    }

    @Override
    void moveHelicopter() {
        // helicopter doesn't move
    }

    @Override
    void upPressed() {
        // do nothing
    }

    @Override
    void leftPressed() {
        // do nothing
    }

    @Override
    void downPressed() {
        // do nothing
    }

    @Override
    void rightPressed() {
        // do nothing
    }

    @Override
    void spacePressed() {
        // do nothing
    }
}
class Ready extends HelicopterState {
    private Helicopter helicopter;
    public Ready(Helicopter helicopter) {
        super(helicopter);
        this.helicopter = helicopter;
    }

    @Override
    void ignition() {
        this.helicopter.changeState(new Stopping(helicopter));
    }
    @Override
    void burnFuel() {
        helicopter.burnFuel(5);
    }

    @Override
    void spinBlades() {
        this.helicopter.rotateBlades();
    }

    @Override
    void moveHelicopter() {
        this.helicopter.moveHelicopter();
    }

    @Override
    void upPressed() {
        this.helicopter.adjustSpeed(.1);
    }

    @Override
    void leftPressed() {
        this.helicopter.turnHelicopter(-10);
    }

    @Override
    void downPressed() {
        this.helicopter.adjustSpeed(-.1);
    }

    @Override
    void rightPressed() {
        this.helicopter.turnHelicopter(10);
    }

    @Override
    void spacePressed() {
        this.helicopter.seedCloud();
    }
}
class Helicopter extends GameObject implements Updatable {
    private HelicopterState currentState;
    private HeliBody heliBody;
    private HeliBlade heliBlade;
    private Rotate pivotPoint = new Rotate();
    private GameBoundingBox boundingBox;
    private GameText feulText;
    private Position startPosition;
    private Position currentPosition;
    private int feul = 250000;
    private double direction = 0;
    private double speed = 0;
    private double rotorSpeed;
    private boolean onHelipad = true;
    private boolean onCloud;
    private int currentCloudNumber = -1;

    public Helicopter(Position startPosition) {
        this.startPosition = startPosition;
        this.currentPosition = startPosition;
        buildHelicopter();
        initializeHelicopterPosition(startPosition);
        this.boundingBox.setPosition(currentPosition);

        currentState = new Off(this);
    }

    private void buildHelicopter() {
        heliBody = new HeliBody(.75, .5);
        heliBlade = new HeliBlade(
                10,25, 3, Color.LIGHTGRAY);
        this.feulText = new GameText("F:" + feul, Color.YELLOW);
        this.add(heliBody);
        this.add(heliBlade);

        Rectangle heliBounds = new Rectangle(this.getBoundsInLocal().getMinX(),
                this.getBoundsInLocal().getMinY(),
                this.getBoundsInLocal().getWidth(),
                this.getBoundsInLocal().getHeight());
        this.boundingBox = new GameBoundingBox(heliBounds);
        //add(this.heliBoundingBox.getVisibleBoundingBox());

        add(this.feulText);
        this.feulText.setTranslateY(this.feulText.getTranslateY()-10);



        this.feulText.setTranslateX(this.feulText.getTranslateX()-
                feulText.getBoundsInLocal().getWidth()/2);
        this.feulText.setTranslateY(this.feulText.getTranslateY()-
                feulText.getBoundsInLocal().getHeight());
    }

    private void initializeHelicopterPosition(Position startPosition) {
        this.setTranslateX(startPosition.xPos());
        this.setTranslateY(startPosition.yPos());
    }

    @Override
    public void update() {
        // check for what state the helicopter is in

        feulText.updateText("F:" + feul);
        this.currentState.spinBlades();
        this.currentState.moveHelicopter();
        this.currentState.burnFuel();
        checkCollision();
    }

    public void spinUpBlades(double speedChange) {
        this.rotorSpeed += speedChange;
        rotateBlades();
        if(speedChange > 0 && rotorSpeed > 15) {
            rotorSpeed = 15;
            this.changeState(new Ready(this));
        }
        if (speedChange < 0 && rotorSpeed < .1) {
            this.changeState(new Off(this));
        }
    }
    public void rotateBlades() {
        this.heliBlade.setRotate(heliBlade.getRotate()+rotorSpeed);
    }

    public void ignition() {
        this.currentState.ignition();
    }
    public void upPressed() {
        this.currentState.upPressed();
    }
    public void adjustSpeed(double speedChange) {
        speed += speedChange;
        if(speed > 10) {
            speed = 10;
        }
        else if (speed < -2) {
            speed = -2;
        }
    }
    public void downPressed() {
        this.currentState.downPressed();
    }

    public void burnFuel(int amount) {
        if(feul > 0) {
            int fuelBurned = amount + (int)(speed * 10);
            feul -= fuelBurned;
        }
        if(feul <= 0) {
            feul = 0;
            feulText.updateText("F:" + feul);
            this.changeState(new Stopping(this));
        }
        feulText.updateText("F:" + feul);
    }

    public void moveHelicopter() {
        setTranslateX(this.getTranslateX() + Math.sin(direction) * speed);
        setTranslateY(this.getTranslateY() + Math.cos(direction) * speed);
        updateBoundingBoxPosition();
    }

    private void updateBoundingBoxPosition() {
        this.currentPosition = new Position(
                this.getTranslateX(),
                this.getTranslateY());
        this.boundingBox.setPosition(currentPosition);
    }
    public void leftPressed() {
        this.currentState.leftPressed();
    }
    public void rightPressed() {
        this.currentState.rightPressed();
    }
    public void turnHelicopter(double turnAmount) {
        this.setRotate(this.getRotate() - turnAmount);
        direction = (direction + Math.toRadians(turnAmount)) % 360;
        reshapeBoundingBox(turnAmount);

    }
    private void reshapeBoundingBox(double turnAmount) {

    }
    private void checkCollision() {
        for(Cloud cloud : Clouds.getCloudList()) {
            boolean collidingWithCloud = boundingBox.getInvisibleBoundingBox().
                    intersects(cloud.getBoundingBox().getInvisibleBoundingBox());
            if (collidingWithCloud) {
                this.onCloud = true;
                this.currentCloudNumber = cloud.getCloudNumber();
                return;
            }
        }
        this.onCloud = false;
        this.currentCloudNumber = -1;
        /*
        if(boundingBox.getInvisibleBoundingBox()
                .intersects(HeliPad.getHelipad().getBoundingBox())) {
            this.onHelipad = true;
        }
        else {
            this.onHelipad = false;
        }
        unfinished code for checking if on helipad
         */
    }


    public double getSpeed() {
        return speed;
    }

    public void toggleBoundingBoxDisplay() {
        boundingBox.toggle();
    }

    public void spacePressed() {
        this.currentState.spacePressed();
    }
    public void seedCloud() {
        if(this.onCloud) {

            for(Cloud cloud : Clouds.getCloudList()) {
                if(cloud.getCloudNumber() == this.currentCloudNumber) {
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

    }

    public GameBoundingBox getBoundingBox() {
        return boundingBox;
    }

    public boolean onHelipad() {
        return onHelipad;
    }

    public void changeState(HelicopterState state) {
        this.currentState = state;
    }
}
class HeliBody extends GameObject {
    private ImageView heliImage;
    public HeliBody (double scaleX, double scaleY) {
        heliImage = new ImageView(new Image("HeliBodyImage.png"));
        heliImage.setScaleX(scaleX);
        heliImage.setScaleY(-scaleY);
        this.setTranslateX(this.getTranslateX()
                -heliImage.getBoundsInLocal().getWidth()/2);
        this.setTranslateY(this.getTranslateY()
                -heliImage.getBoundsInLocal().getHeight()/2);

        this.getChildren().add(heliImage);
    }

    public ImageView getImage() {
        return heliImage;
    }
}
class HeliBlade extends GameObject {
    private Line blade1;
    private Line blade2;
    public HeliBlade (double yOffset, double bladeLength, int width, Color c) {
        blade1 = new Line(0,bladeLength,0,-bladeLength);
        blade2 = new Line(bladeLength,0,-bladeLength,0);

        blade1.setStroke(c);
        blade1.setStrokeWidth(width);
        blade2.setStroke(c);
        blade2.setStrokeWidth(width);
        this.add(blade1);
        this.add(blade2);
        this.setTranslateY(yOffset);
    }
}
class HeliPad extends GameObject {
    //private static HeliPad heliPad = new HeliPad()
    private Rectangle helipadOutline = new Rectangle(200,200);
    private Ellipse helipadCircle = new Ellipse(75,75);
    public HeliPad(Position startPosition) {
        initializeHelipadPosition(startPosition);
        buildHelipad();
    }
/*
    public static HeliPad getHelipad() {
        return helipad;
    }
    unfinished singleton code for helipad
 */

    private void buildHelipad() {
        this.helipadOutline.setFill(Color.GRAY);
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
            Position cloudStartPosition =
                    new Position(Math.random()*Rainmaker.WINDOW_WIDTH,
                    Math.random()*Rainmaker.WINDOW_HEIGHT*(2.0/3.0)
                            + Rainmaker.WINDOW_HEIGHT/3.0);
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

    public static void toggleDistanceLines() {
        for(Cloud cloud: cloudList) {
            cloud.toggleDistanceLines();
        }
    }

    public void update() {
        for (Cloud cloud : this.cloudList)
            cloud.update();
    }
}
class Cloud extends GameObject implements Updatable, Observer{
    private ArrayList<Line> distanceLines;
    private GameText cloudText;
    private Ellipse cloudShape;
    private GameBoundingBox boundingBox;
    private Color cloudColor;
    private Position currentPosition;
    private int seedValue;
    private int cloudNumber;
    private Pond closestPond;
    private boolean showDistanceLines;

    public Cloud(Position cloudStartPosition) {
        this.cloudNumber = Clouds.getCloudList().size();
        this.currentPosition = cloudStartPosition;
        this.seedValue = 0;
        this.showDistanceLines = false;
        initializeCloudPosition(cloudStartPosition);
        buildCloud();
        distanceLines = new ArrayList<>(Clouds.getCloudList().size());
        for (Pond pond : Ponds.getPondList()) {
            Line cloudToPond = new Line(0,
                    0,
                    pond.getTranslateX()-this.getTranslateX(),
                    pond.getTranslateY()-this.getTranslateY());
            cloudToPond.setStroke(Color.TRANSPARENT);
            cloudToPond.setStrokeWidth(1);
            distanceLines.add(cloudToPond);
            this.getChildren().add(cloudToPond);
        }
    }

    private void buildCloud() {
        this.cloudColor = Color.WHITE;
        this.cloudShape = new Ellipse(0,0, 60,40);
        this.cloudShape.setFill(this.cloudColor);
        this.cloudText = new GameText(this.seedValue + "%", Color.BLACK);
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
        if(seedValue > 30) {
            this.rain();
        }
        cloudText.updateText(seedValue + "%");
        this.boundingBox.setPosition(currentPosition);
        this.cloudColor = Color.rgb(255 - seedValue,255 - seedValue,
                255 - seedValue);
        this.cloudShape.setFill(cloudColor);

        int cloudIndex = 0;
        for(Line distanceLine : this.distanceLines) {
            distanceLine.setEndX(Ponds.getPondList().get(cloudIndex)
                    .getTranslateX()-this.getTranslateX());
            distanceLine.setEndY(Ponds.getPondList().get(cloudIndex)
                    .getTranslateY()-this.getTranslateY());
            cloudIndex++;
        }

        if(this.getTranslateX() > Rainmaker.WINDOW_WIDTH + 100) {
            this.reset();
        }

    }

    private void rain() {
        double closestPondDistance = Double.MAX_VALUE;
        for(Pond pond : Ponds.getPondList()) {
            if(getDistanceToPond(pond) < closestPondDistance && !pond.full()) {
                closestPondDistance = getDistanceToPond(pond);
                this.closestPond = pond;
            }
        }
        if(closestPond != null) {
            closestPond.fillPond(this.seedValue);
        }
        else {

        }
    }

    private double getDistanceToPond(Pond pond) {
        double xDistance = this.getTranslateX() -
                pond.getTranslateX();
        double yDistance = this.getTranslateY() -
                pond.getTranslateY();
        double hypotenuseSqrd = Math.pow(xDistance, 2) + Math.pow(yDistance, 2);
        double distance = Math.sqrt(hypotenuseSqrd);
        return distance;
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
        if (seedValue < 100) {
            seedValue++;
        }
    }
    @Override
    public void updateObserver() {

    }
    public void reset() {
        this.seedValue = 0;
        cloudText.updateText(seedValue + "%");
        this.cloudColor = Color.WHITE;
        Position cloudStartPosition;
        if(this.getTranslateX() > Rainmaker.WINDOW_WIDTH + 100){
            cloudStartPosition =
                    new Position(-100, Math.random()
                            * Rainmaker.WINDOW_HEIGHT * (2.0 / 3.0)
                            + Rainmaker.WINDOW_HEIGHT / 3.0);
        }
        else {
            cloudStartPosition =
                    new Position(Math.random() * Rainmaker.WINDOW_WIDTH,
                            Math.random() * Rainmaker.WINDOW_HEIGHT
                                 * (2.0 / 3.0) + Rainmaker.WINDOW_HEIGHT / 3.0);
        }
        currentPosition = cloudStartPosition;
                initializeCloudPosition(cloudStartPosition);
        this.boundingBox.setPosition(cloudStartPosition);
    }

    public GameBoundingBox getBoundingBox() {
        return boundingBox;
    }

    public int getSeedValue() {
        return seedValue;
    }
    public void decrementSeedValue() {
        this.seedValue--;
    }

    public ArrayList<Line> getDistanceLines() {
        return distanceLines;
    }

    public void toggleDistanceLines() {
        showDistanceLines = !showDistanceLines;
        for (Line distanceLine : distanceLines) {
            if(showDistanceLines) {
                distanceLine.setStroke(Color.RED);
            }
            else {
                distanceLine.setStroke(Color.TRANSPARENT);
            }

        }

    }
}
class Wind extends GameObject implements Updatable{
    private static WindParameters windParameters =
            new WindParameters(5, 1, 0);
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
record WindParameters(double speed, double directionX, double directionY) { }
class Ponds extends GameObject implements Updatable {
    private static Ponds ponds = new Ponds(3);
    private static ArrayList<Pond> pondList;

    private Ponds(int numberOfPonds) {
        pondList = new ArrayList<>(numberOfPonds);
        for (int i = 0; i < numberOfPonds; i++) {
            Position pondStartPosition =
                    new Position(Math.random()*Rainmaker.WINDOW_WIDTH,
                    Math.random()*Rainmaker.WINDOW_HEIGHT*(2.0/3.0)
                            + Rainmaker.WINDOW_HEIGHT/3.0);
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
    private int pondFill;
    private int pondFillClock;
    private boolean isFull = false;

    public Pond(Position startPosition) {
        initializePondPosition(startPosition);
        buildPond();
        this.pondFill = (int)(Math.random()*50);
    }
    private void buildPond() {
        this.pondFill = 0;
        this.pondFillClock = 0;
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
        pondText.updateText(pondFill + "%");

    }

    public void fillPond(int cloudSeedValue) {
        if(this.pondFill < 100) {
            pondFillClock += cloudSeedValue / 10;
            if(pondFillClock % 100 < pondFillClock) {
                this.pondFill += 1;
                pondFillClock = pondFillClock % 100;
            }
        }
        else {
            pondFill = 100;
            isFull = true;
        }
    }

    public void reset() {
        this.pondFill = 0;
        this.isFull = false;
        pondText.updateText(pondFill + "%");
        Position pondStartPosition =
                new Position(Math.random()*Rainmaker.WINDOW_WIDTH,
                Math.random()*Rainmaker.WINDOW_HEIGHT*(2.0/3.0)
                        + Rainmaker.WINDOW_HEIGHT/3.0);
        initializePondPosition(pondStartPosition);
    }

    public boolean full() {
        return this.isFull;
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