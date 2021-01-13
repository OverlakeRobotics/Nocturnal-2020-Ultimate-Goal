package org.firstinspires.ftc.teamcode.components;

import com.acmerobotics.roadrunner.drive.Drive;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.EnumMap;

public class ArmSystem {

    private static final double LEFT_CLOSED_POSITION = 0.715;
    private static final double RIGHT_CLOSED_POSITION = 0.189;
    private static final double LEFT_OPEN_POSITION = 0.446;
    private static final double RIGHT_OPEN_POSITION = 0.456;

    public enum Servoes {

        LEFT (LEFT_CLOSED_POSITION, LEFT_OPEN_POSITION, false), //values need to be changed
        RIGHT (RIGHT_CLOSED_POSITION, RIGHT_OPEN_POSITION, false); //values need to be changed

        private final double openPosition;
        private final double closedPosition;
        private boolean closed;

        Servoes(double closedPosition, double openPosition, boolean closed) {
            this.closedPosition = closedPosition;
            this.openPosition = openPosition;
            this.closed = closed;
        }

        private double getOpenPosition() {
            return openPosition;
        }

        private double getClosedPosition() {
            return closedPosition;
        }

        private boolean getClosed() {
            return closed;
        }

        private void setLatched(boolean isClosed) {
            closed = isClosed;
        }
    }

    public EnumMap<Servoes, Servo> servoMap;

    DcMotor motor; //one motor that we need

    private static final double TICKS_PER_REVOLUTION = DriveConstants.TICKS_PER_REV; //number of ticks per revolution
    private static final double NUM_REVOLUTIONS = 0.8; // this needs to be changed - the number is num of revolutions


    private static final int DEFAULT = 0; // this needs to be changed


    public ArmSystem(DcMotor motor1, EnumMap<Servoes, Servo> servoMap) { //constructor
        this.motor = motor1; //setting ArmSystem motor to whatever motor that is
        init();
    }

    private void init() {
        down();
        release();
    }

    private void up(){
        grab();
        while (motor.getCurrentPosition() != NUM_REVOLUTIONS * TICKS_PER_REVOLUTION){
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor.setPower(0.75);
        }
    }

    private void down(){
        while (motor.getCurrentPosition() != DEFAULT) {
            motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            motor.setTargetPosition(DEFAULT);
        }
        motor.setPower(0.0);
    }

    private void grab(){
        servoMap.forEach((name, servo) -> {
            servo.setPosition(name.getClosedPosition());
        });
    }

    private void release(){
        servoMap.forEach((name, servo) -> {
            servo.setPosition(name.getOpenPosition());
        });
    }
}




