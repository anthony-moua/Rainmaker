package GameObjects;
import game.Position;
import game.Rainmaker;

import java.util.ArrayList;

public class Clouds extends GameObject implements Updatable {
    private static Clouds clouds = new Clouds(5);
    private static ArrayList<Cloud> cloudList;

    private Clouds(int numberOfClouds) {
        cloudList = new ArrayList<>(numberOfClouds);
        for (int i = 0; i < numberOfClouds; i++) {
            Position cloudStartPosition =
                    new Position(Math.random() * Rainmaker.WINDOW_WIDTH,
                            Math.random() * Rainmaker.WINDOW_HEIGHT * (2.0 / 3.0)
                                    + Rainmaker.WINDOW_HEIGHT / 3.0);
            Cloud c = new Cloud(cloudStartPosition);
            cloudList.add(c);
            this.getChildren().add(c);
        }
    }

    public static ArrayList<Cloud> getCloudList() {
        return cloudList;
    }

    public static void reset() {
        for (Cloud cloud : cloudList) {
            cloud.reset();
        }
    }

    public static void toggleBoundingBoxDisplay() {
        for (Cloud cloud : cloudList) {
            cloud.toggleBoundingBoxDisplay();
        }
    }

    public static Clouds getClouds() {
        return clouds;
    }

    public static void toggleDistanceLines() {
        for (Cloud cloud : cloudList) {
            cloud.toggleDistanceLines();
        }
    }

    public void update() {
        for (Cloud cloud : this.cloudList)
            cloud.update();
    }
}
