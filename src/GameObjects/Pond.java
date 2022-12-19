package GameObjects;

import game.Position;
import game.Rainmaker;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

class Pond extends GameObject implements Updatable {
    private GameText pondText;
    private Ellipse pondShape;
    //private Color pondColor;
    private int pondFill;
    private int pondFillClock;
    private boolean isFull = false;

    public Pond(Position startPosition) {
        initializePondPosition(startPosition);
        buildPond();
        this.pondFill = (int) (Math.random() * 50);
    }

    private void buildPond() {
        this.pondFill = 0;
        this.pondFillClock = 0;
        this.pondShape = new Ellipse(0, 0, 20, 20);
        this.pondText = new GameText(this.pondFill + "%", Color.WHITE);
        this.pondShape.setFill(Color.BLUE);
        add(this.pondShape);
        add(this.pondText);

        this.pondText.setTranslateX(-10);
        this.pondText.setTranslateY(10);
    }

    private void initializePondPosition(Position startPosition) {
        this.setTranslateX(startPosition.xPos());
        this.setTranslateY(startPosition.yPos());
    }

    @Override
    public void update() {
        pondText.updateText(pondFill + "%");

    }

    public void fillPond(int cloudSeedValue) {
        if (this.pondFill < 100) {
            pondFillClock += cloudSeedValue / 10;
            if (pondFillClock % 100 < pondFillClock) {
                this.pondFill += 1;
                pondFillClock = pondFillClock % 100;
            }
        } else {
            pondFill = 100;
            isFull = true;
        }
    }

    public void reset() {
        this.pondFill = 0;
        this.isFull = false;
        pondText.updateText(pondFill + "%");
        Position pondStartPosition =
                new Position(Math.random() * Rainmaker.WINDOW_WIDTH,
                        Math.random() * Rainmaker.WINDOW_HEIGHT * (2.0 / 3.0)
                                + Rainmaker.WINDOW_HEIGHT / 3.0);
        initializePondPosition(pondStartPosition);
    }

    public boolean full() {
        return this.isFull;
    }
}
