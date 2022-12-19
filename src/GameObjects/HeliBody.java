package GameObjects;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
// gameobject that is an image of the helicopter body
class HeliBody extends GameObject {
    private ImageView heliImage;

    public HeliBody(double scaleX, double scaleY) {
        heliImage = new ImageView(new Image("HeliBodyImage.png"));
        heliImage.setScaleX(scaleX);
        heliImage.setScaleY(-scaleY);
        this.setTranslateX(this.getTranslateX()
                - heliImage.getBoundsInLocal().getWidth() / 2);
        this.setTranslateY(this.getTranslateY()
                - heliImage.getBoundsInLocal().getHeight() / 2);

        this.getChildren().add(heliImage);
    }

    public ImageView getImage() {
        return heliImage;
    }
}
