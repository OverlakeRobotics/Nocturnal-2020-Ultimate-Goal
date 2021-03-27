package org.firstinspires.ftc.teamcode.helpers;

/**
 *
 */
public enum Target {
    //TODO Find out powers for each goal
    POWER_SHOT (60),
    TOWER_GOAL (60);

    private final double rpm;
    Target(double rpm) {
        this.rpm = rpm;
    }

    public double getRpm() {
        return rpm;
    }
}
