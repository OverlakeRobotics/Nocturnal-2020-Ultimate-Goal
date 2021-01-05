package org.firstinspires.ftc.teamcode.components;

import com.acmerobotics.roadrunner.drive.Drive;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

public class ArmSystem {

    DcMotor motor; //one motor that we need

    private static final double TICKS_PER_REVOLUTION = DriveConstants.TICKS_PER_REV; //number of ticks per revolution
    private static final double NUM_REVOLUTIONS = 0.8; // this needs to be changed - the number is num of revolutions

    private static final int DEFAULT = 0; // this needs to be changed


    public ArmSystem(DcMotor motor1) { //constructor
        this.motor = motor1; //setting ArmSystem motor to whatever motor that is
        init();
    }

    private void init() {
        while (motor.getCurrentPosition() != DEFAULT) {
            motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            motor.setTargetPosition(DEFAULT);
        }
        motor.setPower(0.0);
    }

    private void up(){
        while (motor.getCurrentPosition() != NUM_REVOLUTIONS * TICKS_PER_REVOLUTION){
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor.setPower(0.75);
        }
    }

    /*private void movetoPositionRevolutions(double revolutions){
        while (motor.getCurrentPosition() != revolutions * TICKS_PER_REVOLUTION){
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor.setPower(0.75);
        }
    }

    private void movetoPositionTicks(double ticks){
        while (motor.getCurrentPosition() != ticks * TICKS_PER_REVOLUTION){
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor.setPower(0.75);
        }
    }*/
}




