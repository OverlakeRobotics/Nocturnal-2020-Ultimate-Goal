package org.firstinspires.ftc.teamcode.components;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.helpers.Constants;

public class YeetSystem {

    // Systems
    private final DcMotor motor; //one motor that we need
    private Servo leftServo;
    private Servo rightServo;

    // Tracker fields
    private double targetPosition;
    private boolean isRunning;

    public YeetSystem(DcMotor motor, Servo leftServo, Servo rightServo) { //constructor
        this.motor = motor; //setting ArmSystem motor to whatever motor that is
        this.leftServo = leftServo;
        this.rightServo = rightServo;
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    /**
     * Places the wobble goal down and releases it
     */
    public boolean place() {
        if (!isComplete()) {
            moveArm(Constants.ARM_MOTOR_DOWN_POSITION);
        } else {
            release();
            powerDown();
        }
        return isComplete();
    }

    /**
     * Picks up the wobble goal
     */
    public boolean pickUp() {
        if (!isComplete()) {
            grab();
            moveArm(Constants.ARM_MOTOR_UP_POSITION);
        } else {
            powerDown();
        }
        return isComplete();
    }

    /**
     * Yeets the wobble goal over the fence
     */
    public boolean yeet() {
        if (pickUp()) {
            release();
        }
        return isComplete();
        // [TODO, AC] figure this out because if you release it it'll just fall rather than yeet.
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
        isRunning = false;
    }

    /**
     * Moves arm either up or down
     */
    private void moveArm(double targetPosition) {
        this.targetPosition = targetPosition;
        if (!isRunning) {
            motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            if (targetPosition == Constants.ARM_MOTOR_DOWN_POSITION) {
                motor.setPower(-Constants.ARM_MOTOR_RAW_POWER);
            } else {
                motor.setPower(Constants.ARM_MOTOR_RAW_POWER);
            }
            isRunning = true;
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
