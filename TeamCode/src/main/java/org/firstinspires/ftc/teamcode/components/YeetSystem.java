package org.firstinspires.ftc.teamcode.components;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.internal.system.Deadline;
import org.firstinspires.ftc.teamcode.helpers.Constants;

import java.util.concurrent.TimeUnit;

public class YeetSystem {

    // Arm State
    private enum ArmState {
        IDLE,
        GRAB,
        START_ARM,
        MOVING_ARM
    }

    // Systems
    public final DcMotor motor; //one motor that we need
    private final Servo leftServo;
    private final Servo rightServo;
    private final Deadline elapsedTime;

    // Tracker fields
    private ArmState currentState;
    private Integer targetPosition;

    public YeetSystem(DcMotor motor, Servo leftServo, Servo rightServo) { //constructor
        this.motor = motor; //setting ArmSystem motor to whatever motor that is
        this.leftServo = leftServo;
        this.rightServo = rightServo;
        elapsedTime = new Deadline(Constants.SERVO_WAIT_TIME, TimeUnit.MILLISECONDS);
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        currentState = ArmState.IDLE;
    }

    /**
     * Places the wobble goal down and releases it
     * @return if the wobble goal is placed on the ground
     */
    public boolean placed() {
        if (isComplete()) {
            release();
            targetPosition = null;
            currentState = ArmState.IDLE;
            return true;
        }

        if (currentState == ArmState.IDLE) {
            targetPosition = Constants.ARM_MOTOR_DOWN_POSITION;
            moveArm();
        }
        return false;
    }

    /**
     * Picks up the wobble goal
     * @return If the wobble goal is picked up
     */
    public boolean pickedUp() {
        if (isComplete()) {
            shutDown();
            targetPosition = null;
            currentState = ArmState.IDLE;
            return true;
        }

        switch (currentState) {
            case IDLE:
                elapsedTime.reset();
                currentState = ArmState.GRAB;
                grab();
                break;

            case GRAB:
                if (elapsedTime.hasExpired()) {
                    currentState = ArmState.START_ARM;
                }
                break;

            case START_ARM:
                targetPosition = Constants.ARM_MOTOR_UP_POSITION;
                moveArm();
                currentState = ArmState.MOVING_ARM;
                break;
        }
        return false;
    }

    /**
     * Yeets the wobble goal over the fence
     * @return if the wobble goal is yeeted over the fence
     */
    public boolean yeeted() {
        if (pickedUp()) {
            release();
            return true;
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

    public boolean isGrabbed(){
        if (leftServo.getPosition() == Constants.LEFT_ARM_SERVO_CLOSED_POSITION && rightServo.getPosition() == Constants.RIGHT_ARM_SERVO_CLOSED_POSITION){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean isOpen(){
        if (leftServo.getPosition() == Constants.LEFT_ARM_SERVO_OPEN_POSITION && rightServo.getPosition() == Constants.RIGHT_ARM_SERVO_OPEN_POSITION){
            return true;
        }
        else{
            return false;
        }
    }
    /**
     * Shuts down the motor
     */
    public void shutDown() {
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
    public void grab() {
        leftServo.setPosition(Constants.LEFT_ARM_SERVO_CLOSED_POSITION);
        rightServo.setPosition(Constants.RIGHT_ARM_SERVO_CLOSED_POSITION);
    }

    /**
     * Opens the servos to release the wobble goal
     */
    public void release() {
        leftServo.setPosition(Constants.LEFT_ARM_SERVO_OPEN_POSITION);
        rightServo.setPosition(Constants.RIGHT_ARM_SERVO_OPEN_POSITION);
    }
}

