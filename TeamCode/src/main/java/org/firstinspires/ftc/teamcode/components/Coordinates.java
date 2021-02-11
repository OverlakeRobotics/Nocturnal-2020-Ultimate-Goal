package org.firstinspires.ftc.teamcode.components;

import com.acmerobotics.roadrunner.geometry.Vector2d;

import org.firstinspires.ftc.teamcode.Constants;

public enum Coordinates {
    //TODO fill in these coordinates
    BOX_A(2.5 * Constants.fieldBoxWidth, 3.5 * Constants.fieldBoxWidth),
    BOX_B(1.5 * Constants.fieldBoxWidth, 4.5 * Constants.fieldBoxWidth),
    BOX_C(2.5 * Constants.fieldBoxWidth, 5.5 * Constants.fieldBoxWidth),
    POWERSHOT_1(Constants.firstPowerShotCoordinates.getX(), Constants.firstPowerShotCoordinates.getY()),
    POWERSHOT_2(Constants.secondPowerShotCoordinates.getX(), Constants.secondPowerShotCoordinates.getY()),
    POWERSHOT_3(Constants.thirdPowerShotCoordinates.getX(), Constants.thirdPowerShotCoordinates.getY()),
    SECOND_WOBBLE(),
    PARKING_POSITION();

    private final double x;
    private final double y;
    private Vector2d z;

    Coordinates(double x, double y) {
        this.x = x;
        this.y = y;
        this.z = new Vector2d(x, y);
    }

    public double getX() {
        return x;
    }

    public Vector2d getVector2d(){
        return z;
    }

    public double getY() {
        return y;
    }
}
