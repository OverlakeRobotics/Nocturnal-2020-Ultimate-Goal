package org.firstinspires.ftc.teamcode.components;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class IntakeSystem {
    private final DcMotor motor;
    private int rings;

    public IntakeSystem(DcMotor motor) {
        this.motor = motor;
        initMotors();

        // Sets the rings to 3 since we start with 3 rings
        rings = 3;
    }

    /**
     * Initializes the motors
     */
    public void initMotors() {
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //TODO Figure out what direction to set to
        motor.setDirection(DcMotorSimple.Direction.REVERSE);
        motor.setPower(0);
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
    public void suck() {
        //TODO implement
    }

    /**
     * Shuts down the motor
     */
    public void stop() {
        motor.setPower(0);
    }
}
