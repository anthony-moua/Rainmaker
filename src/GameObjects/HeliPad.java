package GameObjects;

import game.Position;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;

public class HeliPad extends GameObject {
    //private static GameObjects.HeliPad heliPad = new GameObjects.HeliPad()
    private Rectangle helipadOutline = new Rectangle(200, 200);
    private Ellipse helipadCircle = new Ellipse(75, 75);

    public HeliPad(Position startPosition) {
        initializeHelipadPosition(startPosition);
        buildHelipad();
    }
/*
    public static GameObjects.HeliPad getHelipad() {
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
