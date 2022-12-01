import javafx.scene.Node;
import javafx.scene.layout.Pane;

class Game extends Pane implements Updatable {

    @Override
    public void update() {
        for (Node n : getChildren()) {
            if (n instanceof Updatable) {
                ((Updatable) n).update();
            }
        }
    }

    public void reset() {
    }
}
