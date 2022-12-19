package GameObjects;

import game.Rainmaker;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

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
