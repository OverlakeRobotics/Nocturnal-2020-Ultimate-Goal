package org.firstinspires.ftc.teamcode.components;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class IntakeSystem {

    private final DcMotor motor;

    public IntakeSystem(DcMotor motor) {
        this.motor = motor;
        initMotors();
    }

    /**
     * Initializes the motors
     */
    public void initMotors() {
        motor.setDirection(DcMotorSimple.Direction.REVERSE);
        motor.setPower(0);
    }

    /**
     * Intakes rings
     */
    public void suck() {
        motor.setPower(1);
    }

    /**
     * Shuts down the motor
     */
    public void stop() {
        motor.setPower(0);
    }
}
