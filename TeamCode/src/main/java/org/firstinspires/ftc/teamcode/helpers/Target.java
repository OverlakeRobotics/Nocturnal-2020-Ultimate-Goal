package org.firstinspires.ftc.teamcode.helpers;

/**
 *
 */
public enum Target {
    //TODO Find out powers for each goal
    // 1500

    // 29.5 inches
    // 63 inches
    POWER_SHOT (1200),
    TOWER_GOAL (1200);

    private final double rpm;
    Target(double rpm) {
        this.rpm = rpm;
    }

    public double getRpm() {
        return rpm;
    }
}
