package org.firstinspires.ftc.teamcode.components;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.helpers.Constants;

public class YeetSystem {

    // Systems
    private final DcMotor motor; //one motor that we need
    private Servo leftServo;
    private Servo rightServo;

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
        armDown();
        releaseDown();
    }

    /**
     * Yeets the wobble goal over the fence
     */
    public void yeet() {
        pickup();
        releaseUp();
        // [TODO, AC] figure this out because if you release it it'll just fall rather than yeet.
    }

    public void releaseDown(){
        if (!updateDown()){
            releaseDown();
        }
        else{
            release();
        }
    }

    public void releaseUp(){
        if (!updateUp()){
            updateUp();
        }
        else{
            release();
        }
    }


    /**
     * Grabs the wobble goal and raises it to the up position
     */
    public void pickup() {
        grab();
        armUp();
    }

    public boolean updateDown () {
        return (motor.getCurrentPosition() >= Constants.ARM_MOTOR_DOWN_POSITION);
    }

    public boolean updateUp () {
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
