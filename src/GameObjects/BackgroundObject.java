package GameObjects;

import game.Rainmaker;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
public class BackgroundObject extends GameObject {
    private static BackgroundObject backgroundObject = new BackgroundObject();
    private static Background background;
    private static BackgroundImage backgroundImage;
    private BackgroundObject() {
        backgroundImage = new BackgroundImage(new Image("background.png",
                Rainmaker.WINDOW_WIDTH, Rainmaker.WINDOW_HEIGHT, false, true),
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
