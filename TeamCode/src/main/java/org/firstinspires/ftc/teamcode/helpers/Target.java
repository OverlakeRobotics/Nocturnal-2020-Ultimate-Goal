package org.firstinspires.ftc.teamcode.helpers;

/**
 *
 */
public enum Target {
    POWER_SHOT (1090),
    TOWER_GOAL (1140);

    private final double rpm;
    Target(double rpm) {
        this.rpm = rpm;
    }

    public double getRpm() {
        return rpm;
    }
}
