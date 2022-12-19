package GameObjects;

import game.WindParameters;
//unfinished wind implementation
class Wind extends GameObject implements Updatable {
    private static WindParameters windParameters =
            new WindParameters(5, 1, 0);
    private static final Wind wind = new Wind(windParameters);

    private Wind(WindParameters windParameters) {

    }

    public static Wind getWind() {
        return wind;
    }

    @Override
    public void update() {

    }
}
