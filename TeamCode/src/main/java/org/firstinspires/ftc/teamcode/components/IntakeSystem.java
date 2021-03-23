package org.firstinspires.ftc.teamcode.components;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class IntakeSystem {
    // IntakeState
    private enum IntakeState {
        IDLE,
        SUCK
    }
    private IntakeState currentState;

    // Hardware
    private final DcMotor motor;

    public IntakeSystem(DcMotor motor) {
        this.motor = motor;
        initMotors();
    }

    /**
     * Initializes the motors
     */
    public void initMotors() {
        currentState = IntakeState.IDLE;
        motor.setDirection(DcMotorSimple.Direction.REVERSE);
        motor.setPower(0);
    }

    /**
     * Intakes rings
     */
    public void suck() {
        if (currentState != IntakeState.SUCK) {
            currentState = IntakeState.SUCK;
            motor.setPower(1);
        }
    }

    /**
     * Shuts down the motor
     */
    public void stop() {
        if (currentState == IntakeState.SUCK) {
            currentState = IntakeState.IDLE;
            motor.setPower(0);
        }
    }
}
