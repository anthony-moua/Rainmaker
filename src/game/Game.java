package game;

import GameObjects.Cloud;
import GameObjects.Clouds;
import GameObjects.Updatable;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

public class Game extends Pane implements Updatable {
    private static Game game = new Game();

    private Game() {
        // no need
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
        // Win state and conditions would be reset here
    }

    public void secondPassed() {
        for (Cloud cloud : Clouds.getCloudList()) {
            if (cloud.getSeedValue() > 0) {
                cloud.decrementSeedValue();
                // every second, the clouds decrement their seed value in sync
            }
        }
    }
}
