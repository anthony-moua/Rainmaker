package GameObjects;

import javafx.scene.Group;
import javafx.scene.Node;

abstract class GameObject extends Group implements Updatable {
    void add(Node node) {
        this.getChildren().add(node);
    }

    @Override
    public void update() {
    }
}
