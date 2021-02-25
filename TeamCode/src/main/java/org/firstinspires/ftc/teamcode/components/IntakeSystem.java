package org.firstinspires.ftc.teamcode.components;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.opmodes.base.BaseOpMode;

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
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //TODO Figure out what direction to set to
        motor.setDirection(DcMotorSimple.Direction.REVERSE);
        motor.setPower(0);
    }

    /**
     * Intakes rings
     */
    public void suck() {
        if (BaseOpMode.getRingCount() < 3) {
            //TODO implement motor
            BaseOpMode.addRingCount();
        }
    }

    /**
     * Shuts down the motor
     */
    public void stop() {
        motor.setPower(0);
    }
}
