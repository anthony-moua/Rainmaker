package HelicopterStates;

import GameObjects.Helicopter;

public class Ready extends HelicopterState {
    private Helicopter helicopter;

    public Ready(Helicopter helicopter) {
        super(helicopter);
        this.helicopter = helicopter;
    }

    @Override
    public void ignition() {
        this.helicopter.changeState(new Stopping(helicopter));
    }

    @Override
    public void burnFuel() {
        helicopter.burnFuel(5);
    }

    @Override
    public void spinBlades() {
        this.helicopter.rotateBlades();
    }

    @Override
    public void moveHelicopter() {
        this.helicopter.moveHelicopter();
    }

    @Override
    public void upPressed() {
        this.helicopter.adjustSpeed(.1);
    }

    @Override
    public void leftPressed() {
        this.helicopter.turnHelicopter(-10);
    }

    @Override
    public void downPressed() {
        this.helicopter.adjustSpeed(-.1);
    }

    @Override
    public void rightPressed() {
        this.helicopter.turnHelicopter(10);
    }

    @Override
    public void spacePressed() {
        this.helicopter.seedCloud();
    }
}
