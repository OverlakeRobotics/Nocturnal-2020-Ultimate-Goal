package org.firstinspires.ftc.teamcode.components;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.helpers.Constants;

import java.util.concurrent.TimeUnit;

public class YeetSystem {

    // Systems
    private final DcMotor motor; //one motor that we need
    private final Servo leftServo;
    private final Servo rightServo;

    // Tracker fields
    private int targetPosition;

    public YeetSystem(DcMotor motor, Servo leftServo, Servo rightServo) { //constructor
        this.motor = motor; //setting ArmSystem motor to whatever motor that is
        this.leftServo = leftServo;
        this.rightServo = rightServo;
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    /**
     * Places the wobble goal down and releases it
     */
    public boolean placed() {
        if (isComplete()) {
            release();
            return true;
        } else if (!motor.isBusy()) {
            targetPosition = Constants.ARM_MOTOR_DOWN_POSITION;
            moveArm();
        }
        return false;
    }

    /**
     * Picks up the wobble goal
     */
    public boolean pickUp() {
        if (isComplete()) {
            powerDown();
            return true;
        } else if (!motor.isBusy()) {
            targetPosition = Constants.ARM_MOTOR_UP_POSITION;
            grab();
            massTimesGravity();
            moveArm();
        }
        return false;
    }

    /**
     * Yeets the wobble goal over the fence
     */
    public boolean yeet() {
        if (pickUp()) {
            release();
            return true;
        }
        return false;
        // [TODO, AC] figure this out because if you release it it'll just fall rather than yeet.
    }

    /**
     * Waits a certain amount of time in milliseconds
     */
    private void massTimesGravity() {
        try {
            TimeUnit.MILLISECONDS.sleep(Constants.SERVO_WAIT_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the system still needs to run
     * @return if the system still needs to run
     */
    private boolean isComplete() {
        return (Math.abs(targetPosition - motor.getCurrentPosition()) < 50);
    }

    /**
     * Shuts down the motor
     */
    private void powerDown() {
        motor.setPower(0.0);
    }

    /**
     * Moves arm either up or down
     */
    private void moveArm() {
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setTargetPosition(targetPosition);
        if (targetPosition == Constants.ARM_MOTOR_DOWN_POSITION) {
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
    private void grab() {
        leftServo.setPosition(Constants.LEFT_ARM_SERVO_CLOSED_POSITION);
        rightServo.setPosition(Constants.RIGHT_ARM_SERVO_CLOSED_POSITION);
    }

    /**
     * Opens the servos to release the wobble goal
     */
    private void release() {
        leftServo.setPosition(Constants.LEFT_ARM_SERVO_OPEN_POSITION);
        rightServo.setPosition(Constants.RIGHT_ARM_SERVO_OPEN_POSITION);
    }
}
