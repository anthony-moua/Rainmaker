package HelicopterStates;

import GameObjects.Helicopter;

public abstract class HelicopterState {
    private Helicopter helicopter;
    // all helicopter behavior can be found here, depending on the state,
    // different things would happen with these calls
    public HelicopterState(Helicopter helicopter) {
        this.helicopter = helicopter;
    }

    public abstract void ignition();

    public abstract void burnFuel();

    public abstract void spinBlades();

    public abstract void moveHelicopter();

    public abstract void upPressed();

    public abstract void leftPressed();

    public abstract void downPressed();

    public abstract void rightPressed();

    public abstract void spacePressed();
}
