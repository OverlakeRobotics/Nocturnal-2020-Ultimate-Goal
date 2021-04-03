package org.firstinspires.ftc.teamcode.components;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

/**
 * IntakeSystem.java is a component which
 * comprises of two enums in order to convey the state
 * of the system and be manipulated by state machines used in OpModes.
 * It has one motor, which can either be moving (ingesting) - or not.
 * Realize the motor's polarity is determined by set velocity's signum.
 * Understanding this should be pretty straight forward.
 */
public class IntakeSystem {
    // IntakeState
    private enum IntakeState {
        IDLE,
        SUCK,
        UNSUCK
    }
    private IntakeState currentState;

    // Hardware
    private final DcMotor motor;

    /**
     * Creates the IntakeSystem Object
     * @param motor to represent the motor which rotates in order to s'word the object in.
     */
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
     * Outtakes rings
     */
    public void unsuck() {
        if (currentState != IntakeState.UNSUCK) {
            currentState = IntakeState.UNSUCK;
            motor.setPower(-1);
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
