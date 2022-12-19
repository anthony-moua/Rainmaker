package game;

import GameObjects.*;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class GameApp extends Application {
    private static final GameApp gameApp = new GameApp();
    private Game game;
    private Scene scene;
    private Helicopter helicopter;
    private HeliPad heliPad;

    private GameApp() {
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
                new Position((double) Rainmaker.WINDOW_WIDTH / 2,
                        (double) Rainmaker.WINDOW_WIDTH / 5);
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
                if (elapsedTime % 1 < previousTime % 1)
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
        stage.setTitle("game.Rainmaker");
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
