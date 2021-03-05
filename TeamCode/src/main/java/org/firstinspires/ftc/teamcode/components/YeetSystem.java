package org.firstinspires.ftc.teamcode.components;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.helpers.Constants;

public class YeetSystem {

    // Systems
    private final DcMotor motor; //one motor that we need
    private Servo leftServo;
    private Servo rightServo;
    private double targetPosition;

    public YeetSystem(DcMotor motor, Servo leftServo, Servo rightServo) { //constructor
        this.motor = motor; //setting ArmSystem motor to whatever motor that is
        this.leftServo = leftServo;
        this.rightServo = rightServo;
        grab();
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    /**
     * Places the wobble goal down and releases it
     */
    public void place() {
        if (!isRunning()) {
            moveArm(Constants.ARM_MOTOR_DOWN_POSITION);
        }
        if (!isDown()){
            release();
        }
    }

    /**
     * Yeets the wobble goal over the fence
     */
    public void yeet() {
        if (!isRunning()) {
            grab();
            moveArm(Constants.ARM_MOTOR_UP_POSITION);
        }
        if (!isUp()){
            release();
        }
        // [TODO, AC] figure this out because if you release it it'll just fall rather than yeet.
    }

    //Either Constants.ARM_MOTOR_UP_POSITION or Constants.ARM_MOTOR_DOWN_POSITION
    public void setTargetPosition(double targetPosition) {
        this.targetPosition = targetPosition;
    }

    public boolean isRunning () {
        return (Math.abs(targetPosition - motor.getCurrentPosition()) > 50);
    }

    public boolean isDown () {
        return (motor.getCurrentPosition() >= Constants.ARM_MOTOR_DOWN_POSITION);
    }

    public boolean isUp () {
        return (motor.getCurrentPosition() <= Constants.ARM_MOTOR_UP_POSITION);
    }

    public void powerDown() {
        motor.setPower(0.0);
    }

    /**
     * Moves arm either up or down
     */
    private void moveArm(double targetPosition) {
        this.targetPosition = targetPosition;
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        if (!isRunning()) {
            if (targetPosition == Constants.ARM_MOTOR_DOWN_POSITION) {
                motor.setPower(-Constants.ARM_MOTOR_RAW_POWER);
            } else {
                motor.setPower(Constants.ARM_MOTOR_RAW_POWER);
            }
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
