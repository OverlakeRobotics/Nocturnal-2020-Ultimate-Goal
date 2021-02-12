package org.firstinspires.ftc.teamcode.components;

import org.firstinspires.ftc.teamcode.Constants;

public enum Coordinates {
    //TODO fill in these coordinates
    // Starting position
    STARTING_POSITION(),

    // Wobblegoal dropoff boxes
    BOX_A(3.5 * Constants.fieldBoxWidth, 3.5 * Constants.fieldBoxWidth),
    BOX_B(2.5 * Constants.fieldBoxWidth, 4.5 * Constants.fieldBoxWidth),
    BOX_C(3.5 * Constants.fieldBoxWidth, 5.5 * Constants.fieldBoxWidth),

    // Powershot
    POWERSHOT_1(Constants.firstPowerShotCoordinates.getX(), Constants.firstPowerShotCoordinates.getY()),
    POWERSHOT_2(Constants.secondPowerShotCoordinates.getX(), Constants.secondPowerShotCoordinates.getY()),
    POWERSHOT_3(Constants.thirdPowerShotCoordinates.getX(), Constants.thirdPowerShotCoordinates.getY()),

    // Second wobble
    SECOND_WOBBLE(),

    // Parking position
    PARKING_POSITION(2.5 * Constants.fieldBoxWidth, 3.5 * Constants.fieldBoxWidth);

    private final double x;
    private final double y;

    Coordinates(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
