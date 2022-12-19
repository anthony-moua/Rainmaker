package GameObjects;

import game.Position;
import game.Rainmaker;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class Cloud extends GameObject implements Updatable, Observer {
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
        this.seedValue = 0;
        this.showDistanceLines = false;
        this.currentPosition = cloudStartPosition;
        initializeCloudPosition(cloudStartPosition);
        buildCloud();
        distanceLines = new ArrayList<>(Clouds.getCloudList().size());
        for (Pond pond : Ponds.getPondList()) {
            Line cloudToPond = new Line(0, 0,
                    pond.getTranslateX() - this.getTranslateX(),
                    pond.getTranslateY() - this.getTranslateY());
            cloudToPond.setStroke(Color.TRANSPARENT);
            cloudToPond.setStrokeWidth(1);
            distanceLines.add(cloudToPond);
            this.getChildren().add(cloudToPond);
        }
    }

    private void buildCloud() {
        this.cloudColor = Color.WHITE;
        this.cloudShape = new Ellipse(0, 0, 60, 40);
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
        if (seedValue > 30) {
            this.rain();
        }
        cloudText.updateText(seedValue + "%");
        this.boundingBox.setPosition(currentPosition);
        this.cloudColor = Color.rgb(255 - seedValue, 255 - seedValue,
                255 - seedValue);
        this.cloudShape.setFill(cloudColor);

        int cloudIndex = 0;
        for (Line distanceLine : this.distanceLines) {
            distanceLine.setEndX(Ponds.getPondList().get(cloudIndex)
                    .getTranslateX() - this.getTranslateX());
            distanceLine.setEndY(Ponds.getPondList().get(cloudIndex)
                    .getTranslateY() - this.getTranslateY());
            cloudIndex++;
        }

        if (this.getTranslateX() > Rainmaker.WINDOW_WIDTH + 100) {
            this.reset();
        }

    }

    private void rain() {
        double closestPondDistance = Double.MAX_VALUE;
        for (Pond pond : Ponds.getPondList()) {
            if (getDistanceToPond(pond) < closestPondDistance && !pond.full()) {
                closestPondDistance = getDistanceToPond(pond);
                this.closestPond = pond;
            }
        }
        // find the pond closest to this cloud and make it start filling
        if (closestPond != null) {
            closestPond.fillPond(this.seedValue);
        } else {

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
        if (this.getTranslateX() > Rainmaker.WINDOW_WIDTH + 100) {
            cloudStartPosition =
                    new Position(-100, Math.random()
                            * Rainmaker.WINDOW_HEIGHT * (2.0 / 3.0)
                            + Rainmaker.WINDOW_HEIGHT / 3.0);
        } else {
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
            if (showDistanceLines) {
                distanceLine.setStroke(Color.RED);
            } else {
                distanceLine.setStroke(Color.TRANSPARENT);
            }

        }

    }
}
