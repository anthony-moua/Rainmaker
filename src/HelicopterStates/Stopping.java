package HelicopterStates;

import GameObjects.Helicopter;

public class Stopping extends HelicopterState {
    private Helicopter helicopter;

    public Stopping(Helicopter helicopter) {
        super(helicopter);
        this.helicopter = helicopter;
    }

    @Override
    public void ignition() {
        this.helicopter.changeState(new Starting(helicopter));
    }

    @Override
    public void burnFuel() {
        // don't burn fuel while stopping
    }

    @Override
    public void spinBlades() {
        this.helicopter.spinUpBlades(-.05);
    }

    @Override
    public void moveHelicopter() {
        // helicopter doesn't move
    }

    @Override
    public void upPressed() {
        // do nothing
    }

    @Override
    public void leftPressed() {
        // do nothing
    }

    @Override
    public void downPressed() {
        // do nothing
    }

    @Override
    public void rightPressed() {
        // do nothing
    }

    @Override
    public void spacePressed() {
        // do nothing
    }
}
