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
            moveArm();
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
            moveArm();
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
     * Raises the arm to the up position
     */
    private void moveArm() {
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        if (motor.getCurrentPosition() > Constants.ARM_MOTOR_UP_POSITION / 2){
            this.targetPosition = Constants.ARM_MOTOR_DOWN_POSITION;
            motor.setPower(-Constants.ARM_MOTOR_RAW_POWER);
        } else {
            this.targetPosition = Constants.ARM_MOTOR_UP_POSITION;
            motor.setPower(Constants.ARM_MOTOR_RAW_POWER);
        }
    }

    /**
     * Lowers the arm to the down position
     */
    private void armDown() {
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setPower(-Constants.ARM_MOTOR_RAW_POWER);
        if (isDown()){
            powerDown();
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
