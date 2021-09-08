package org.firstinspires.ftc.teamcode.helpers;

/**
 *
 */
public enum Target {
    POWER_SHOT (2090),
    TOWER_GOAL (2140);

    private final double rpm;
    Target(double rpm) {
        this.rpm = rpm;
    }

    public double getRpm() {
        return rpm;
    }
}
