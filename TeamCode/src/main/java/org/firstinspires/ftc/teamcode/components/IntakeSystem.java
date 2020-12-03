package org.firstinspires.ftc.teamcode.components;

import com.qualcomm.robotcore.hardware.DcMotor;

public class IntakeSystem {
    private DcMotor motor1;
    private int rings;

    public IntakeSystem(DcMotor motor) {
        motor1 = motor;

        // Sets the rings to 3 since we start with 3 rings
        rings = 3;
    }

    /**
     * Gets the number of rings currently in the robot
     * @return the number of rings currently in the robot
     */
    public int getRingCount() {
        return rings;
    }

    /**
     * Updates the number of rings in the robot
     * @param rings to set the value to
     */
    public void setRings(int rings) {
        this.rings = rings;
    }

    /**
     * Intakes rings
     */
    public void intake() {
        //TODO implement
    }
}
