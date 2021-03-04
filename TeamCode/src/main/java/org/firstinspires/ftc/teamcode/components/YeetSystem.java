package org.firstinspires.ftc.teamcode.components;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.helpers.Constants;

public class YeetSystem {

    // Systems
    private final DcMotor motor; //one motor that we need
    private Servo leftServo;
    private Servo rightServo;
    private boolean isRunning;

    public YeetSystem(DcMotor motor, Servo leftServo, Servo rightServo) { //constructor
        this.motor = motor; //setting ArmSystem motor to whatever motor that is
        this.leftServo = leftServo;
        this.rightServo = rightServo;
        this.isRunning = false;
        grab();
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    /**
     * Places the wobble goal down and releases it
     */
    public void place() {
        if (!isRunning) {
            armDown();
            isRunning = true;
        }
        if (!isDown()){
            release();
            powerDown();
            isRunning = false;
        }
    }

    /**
     * Yeets the wobble goal over the fence
     */
    public void yeet() {
        grab();
        if (!isRunning) {
            armUp();
            isRunning = true;
        }
        if (!isUp()){
            release();
            powerDown();
            isRunning = false;
        }
        // [TODO, AC] figure this out because if you release it it'll just fall rather than yeet.
    }


    /**
     * Grabs the wobble goal and raises it to the up position
     */
    public void pickup() {
        grab();
        armUp();
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
    private void armUp() {
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor.setPower(Constants.ARM_MOTOR_RAW_POWER);
    }

    /**
     * Lowers the arm to the down position
     */
    private void armDown() {
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setPower(-Constants.ARM_MOTOR_RAW_POWER);
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
