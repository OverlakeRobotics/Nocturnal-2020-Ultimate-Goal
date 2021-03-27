package org.firstinspires.ftc.teamcode.helpers;

import com.acmerobotics.roadrunner.geometry.Vector2d;

/**
 *
 */
public enum Coordinates {
    //TODO check and correct these coordinates
    //TODO See if the webcam will still work if the robot starts pressed against the left or right edge of the starting tile
    STARTING_POSITION(2.5 * Constants.tileWidth, 0.5 * Constants.LATERAL_DISTANCE),

    // Spline position to the left to avoid rings
    DETOUR_POSITION(0.5 * Constants.tileWidth, 2 * Constants.tileWidth),

    // Wobblegoal dropoff boxes
    BOX_A(3.5 * Constants.tileWidth, 3.5 * Constants.tileWidth),
    BOX_B(2.5 * Constants.tileWidth, 4.5 * Constants.tileWidth),
    BOX_C(3.5 * Constants.tileWidth, 5.5 * Constants.tileWidth),

    //Calibration
    CALIBRATION(2.5 * Constants.tileWidth, 3 * Constants.tileWidth),

    // Powershot
    POWERSHOT_1(Constants.firstPowerShotCoordinates.getX(), Constants.firstPowerShotCoordinates.getY()),
    POWERSHOT_2(Constants.secondPowerShotCoordinates.getX(), Constants.secondPowerShotCoordinates.getY()),
    POWERSHOT_3(Constants.thirdPowerShotCoordinates.getX(), Constants.thirdPowerShotCoordinates.getY()),

    // Second wobble
    SECOND_WOBBLE(2.5 * Constants.tileWidth, Constants.tileWidth),

    // Parking position
    PARKING_POSITION(2.5 * Constants.tileWidth, 3.5 * Constants.tileWidth);

    private final double x;
    private final double y;

    /**
     * Constructor
     * @param x (x-coordinate)
     * @param y (y-coordinate)
     *
     */

    Coordinates(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     *
     * @return
     */
    public double getX() {
        return x;
    }

    /**
     *
     * @return
     */
    public double getY() {
        return y;
    }

    /**
     *
     * @return
     */
    public Vector2d getCoordinates() {
        return new Vector2d(x, y);
    }
}
