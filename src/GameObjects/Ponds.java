package GameObjects;

import game.Position;
import game.Rainmaker;

import java.util.ArrayList;

public class Ponds extends GameObject implements Updatable {
    private static Ponds ponds = new Ponds(3);
    private static ArrayList<Pond> pondList;

    private Ponds(int numberOfPonds) {
        pondList = new ArrayList<>(numberOfPonds);
        for (int i = 0; i < numberOfPonds; i++) {
            Position pondStartPosition =
                    new Position(Math.random() * Rainmaker.WINDOW_WIDTH,
                            Math.random() * Rainmaker.WINDOW_HEIGHT * (2.0 / 3.0)
                                    + Rainmaker.WINDOW_HEIGHT / 3.0);
            Pond p = new Pond(pondStartPosition);
            pondList.add(p);
            this.getChildren().add(p);
        }
    }

    public static ArrayList<Pond> getPondList() {
        return pondList;
    }

    public static void reset() {
        for (Pond pond : pondList) {
            pond.reset();
        }
    }

    public static Ponds getPonds() {
        return ponds;
    }

    public void update() {
        for (Pond pond : this.pondList)
            pond.update();
    }
}
