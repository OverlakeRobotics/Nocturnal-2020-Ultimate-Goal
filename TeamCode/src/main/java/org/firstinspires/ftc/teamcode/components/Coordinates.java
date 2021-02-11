package org.firstinspires.ftc.teamcode.components;

public enum Coordinates {
    //TODO fill in these coordinates
    BOX_A(),
    BOX_B(),
    BOX_C(),
    POWERSHOT_1(),
    POWERSHOT_2(),
    POWERSHOT_3(),
    SECOND_WOBBLE(),
    PARKING_POSITION();

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
