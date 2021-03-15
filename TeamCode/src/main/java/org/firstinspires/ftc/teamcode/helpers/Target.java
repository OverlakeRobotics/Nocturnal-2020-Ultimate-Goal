package org.firstinspires.ftc.teamcode.helpers;

public enum Target {
    //TODO Find out powers for each goal
    POWER_SHOT (0.75),
    TOWER_GOAL (0.81);

    private final double power;
    Target(double power) {
        this.power = power;
    }

    public double getPower() {
        return power;
    }
}
