package GameObjects;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
// simple gameoject of blades
class HeliBlade extends GameObject {
    private Line blade1;
    private Line blade2;

    public HeliBlade(double yOffset, double bladeLength, int width, Color c) {
        blade1 = new Line(0, bladeLength, 0, -bladeLength);
        blade2 = new Line(bladeLength, 0, -bladeLength, 0);

        blade1.setStroke(c);
        blade1.setStrokeWidth(width);
        blade2.setStroke(c);
        blade2.setStrokeWidth(width);
        this.add(blade1);
        this.add(blade2);
        this.setTranslateY(yOffset);
    }
}
