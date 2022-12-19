package GameObjects;

import HelicopterStates.HelicopterState;
import HelicopterStates.Off;
import HelicopterStates.Ready;
import HelicopterStates.Stopping;
import game.Position;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

public class Helicopter extends GameObject implements Updatable {
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
                10, 25, 3, Color.LIGHTGRAY);
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
        this.feulText.setTranslateY(this.feulText.getTranslateY() - 10);


        this.feulText.setTranslateX(this.feulText.getTranslateX() -
                feulText.getBoundsInLocal().getWidth() / 2);
        this.feulText.setTranslateY(this.feulText.getTranslateY() -
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
        if (speedChange > 0 && rotorSpeed > 15) {
            rotorSpeed = 15;
            this.changeState(new Ready(this));
        }
        if (speedChange < 0 && rotorSpeed < .1) {
            this.changeState(new Off(this));
        }
    }

    public void rotateBlades() {
        this.heliBlade.setRotate(heliBlade.getRotate() + rotorSpeed);
    }

    public void ignition() {
        this.currentState.ignition();
    }

    public void upPressed() {
        this.currentState.upPressed();
    }

    public void adjustSpeed(double speedChange) {
        speed += speedChange;
        if (speed > 10) {
            speed = 10;
        } else if (speed < -2) {
            speed = -2;
        }
    }

    public void downPressed() {
        this.currentState.downPressed();
    }

    public void burnFuel(int amount) {
        if (feul > 0) {
            int fuelBurned = amount + (int) (speed * 10);
            feul -= fuelBurned;
        }
        if (feul <= 0) {
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
        for (Cloud cloud : Clouds.getCloudList()) {
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
                .intersects(GameObjects.HeliPad.getHelipad().getBoundingBox())) {
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
        if (this.onCloud) {

            for (Cloud cloud : Clouds.getCloudList()) {
                if (cloud.getCloudNumber() == this.currentCloudNumber) {
                    cloud.activateSeeding();
                    break;
                }
            }
        } else {
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
