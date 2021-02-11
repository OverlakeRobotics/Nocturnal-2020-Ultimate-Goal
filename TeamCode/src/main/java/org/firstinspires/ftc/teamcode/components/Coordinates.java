package org.firstinspires.ftc.teamcode.components;

import com.acmerobotics.roadrunner.geometry.Vector2d;

public enum Coordinates {
    //TODO fill in these coordinates
    BOX_A(60, 84),
    BOX_B(36, 108),
    BOX_C(60, 132);//,
    /*POWERSHOT_1(20, 78), BRANDON
    POWERSHOT_2(),
    POWERSHOT_3(),
    SECOND_WOBBLE(),
    PARKING_POSITION();*/

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
