package org.firstinspires.ftc.teamcode.helpers;

public enum Target {
    //TODO Find out powers for each goal
    POWER_SHOT (1000),
    TOWER_GOAL (1000);

    private final double rpm;
    Target(double rpm) {
        this.rpm = rpm;
    }

    public double getRpm() {
        return rpm;
    }
}
