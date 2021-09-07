package org.firstinspires.ftc.teamcode.components;

import android.util.Log;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.internal.system.Deadline;
import org.firstinspires.ftc.teamcode.helpers.Constants;

import java.util.concurrent.TimeUnit;

/**
 * YeetSystem is a conponent which
 *
 * ArmState
 */
public class YeetSystem {

    // Arm State
    private enum ArmState {
        IDLE,
        GRAB,
        RELEASE,
        START_ARM,
        MOVING_ARM
    }

    // Systems
    private final DcMotorEx motor; //one motor that we need
    private final Servo leftServo;
    private final Servo rightServo;
    private final Deadline elapsedTime;

    // Tracker fields
    private ArmState currentState;
    private Integer targetPosition;

    public YeetSystem(DcMotorEx motor, Servo leftServo, Servo rightServo) { //constructor
        this.motor = motor; //setting ArmSystem motor to whatever motor that is
        this.leftServo = leftServo;
        this.rightServo = rightServo;
        elapsedTime = new Deadline(Constants.SERVO_WAIT_TIME, TimeUnit.MILLISECONDS);
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        grab();
        currentState = ArmState.IDLE;
    }



    /**
     * Places the wobble goal down and releases it
     * @return if the wobble goal is placed on the ground
     * RIGHT BUMPER
     */
    public boolean placed() {
        if (isComplete()) {
            if (elapsedTime.milliseconds() > 500) {
                release();
                targetPosition = null;
                currentState = ArmState.IDLE;
                return true;
            }
        }

        switch (currentState) {
            case IDLE:
                currentState = ArmState.START_ARM;
                break;

            case START_ARM:
                targetPosition = Constants.ARM_MOTOR_DOWN_POSITION;
                moveArm();
                elapsedTime.reset();
                break;
        }
        return false;
    }

    /**
     * Picks up the wobble goal
     * @return If the wobble goal is picked up
     */
    public boolean pickedUp(boolean closeServoOnPickup) {
        if (isComplete()) {
            shutDown();
            targetPosition = null;
            currentState = ArmState.IDLE;
            return true;
        }

        switch (currentState) {
            case IDLE:
                elapsedTime.reset();
                if (closeServoOnPickup) {
                    grab();
                } else {
                    currentState = ArmState.START_ARM;
                }
                break;

            case GRAB:
                if (elapsedTime.hasExpired()) {
                    currentState = ArmState.START_ARM;
                }
                break;

            case START_ARM:
                targetPosition = Constants.ARM_MOTOR_UP_POSITION;
                moveArm();
                break;
        }
        return false;
    }

    /**
     * Checks if the system still needs to run
     * @return if the system still needs to run
     */
    private boolean isComplete() {
        if (targetPosition == null) {
            return false;
        }
        return (Math.abs(targetPosition - motor.getCurrentPosition()) < 50);
    }

    /**
     * Shuts down the motor
     */
    public void shutDown() {
        motor.setPower(0.0);
    }

    public void setPower(double power) {
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor.setPower(power);
    }

    public void reset() {
        motor.setPower(0);
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    /**
     * Moves arm either up or down
     */
    private void moveArm() {
        currentState = ArmState.MOVING_ARM;
        motor.setTargetPosition(targetPosition);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        if (targetPosition == Constants.ARM_MOTOR_UP_POSITION) {
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motor.setPower(-Constants.ARM_MOTOR_RAW_POWER);
        } else {
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            motor.setPower(Constants.ARM_MOTOR_RAW_POWER);
        }
    }

    /**
     * Closes the servos to grab the wobble goal
     */
    public void grab() {
        currentState = ArmState.GRAB;
        leftServo.setPosition(Constants.LEFT_ARM_SERVO_CLOSED_POSITION);
        rightServo.setPosition(Constants.RIGHT_ARM_SERVO_CLOSED_POSITION);
    }

    /**
     * Opens the servos to release the wobble goal
     */
    public void release() {
        currentState = ArmState.RELEASE;
        leftServo.setPosition(Constants.LEFT_ARM_SERVO_OPEN_POSITION);
        rightServo.setPosition(Constants.RIGHT_ARM_SERVO_OPEN_POSITION);
    }
}

