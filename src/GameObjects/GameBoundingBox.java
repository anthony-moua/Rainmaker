package GameObjects;

import game.Position;
import javafx.geometry.BoundingBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
// gameobject that holds a rectangle and a bounding box
public class GameBoundingBox implements Updatable {
    private Rectangle visibleBoundingBox;
    private BoundingBox invisibleBoundingBox;
    private boolean displayBoundingBox = false;

    public GameBoundingBox(Rectangle bounds) {
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
        if (displayBoundingBox) {
            visibleBoundingBox.setStroke(Color.RED);
        } else {
            visibleBoundingBox.setStroke(Color.TRANSPARENT);
        }
    }

    public void setPosition(Position newPosition) {
        this.visibleBoundingBox.setTranslateX(newPosition.xPos());
        this.visibleBoundingBox.setTranslateY(newPosition.yPos());
        double xPos = visibleBoundingBox.getTranslateX()
                - (visibleBoundingBox.getWidth() / 2);
        double yPos = visibleBoundingBox.getTranslateY()
                - (visibleBoundingBox.getHeight() / 2);
        this.invisibleBoundingBox = new BoundingBox(xPos, yPos,
                visibleBoundingBox.getWidth(), visibleBoundingBox.getHeight());
    }
}
