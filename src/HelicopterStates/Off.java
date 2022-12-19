package HelicopterStates;

import GameObjects.Helicopter;

public class Off extends HelicopterState {
    private Helicopter helicopter;

    public Off(Helicopter helicopter) {
        super(helicopter);
        this.helicopter = helicopter;
    }

    @Override
    public void ignition() {
        this.helicopter.changeState(new Starting(helicopter));
    }

    @Override
    public void burnFuel() {
        // don't burn fuel while off
    }

    @Override
    public void spinBlades() {
        //blades don't spin
    }

    @Override
    public void moveHelicopter() {
        //helicopter doesn't move
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
