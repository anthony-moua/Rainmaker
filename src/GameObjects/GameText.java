package GameObjects;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

class GameText extends GameObject implements Updatable {
    private Text text;

    public GameText(String text, Color color) {
        this.text = new Text(text);
        this.text.setScaleY(-1);
        this.text.setFill(color);
        this.text.setFont(Font.font(20));
        this.getChildren().add(this.text);
    }

    public void updateText(String s) {
        text.setText(s);
    }

}
