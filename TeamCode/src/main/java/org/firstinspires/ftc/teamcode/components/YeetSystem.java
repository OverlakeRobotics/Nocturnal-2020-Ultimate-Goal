package org.firstinspires.ftc.teamcode.components;

import com.acmerobotics.roadrunner.drive.Drive;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Constants;

import java.util.EnumMap;

import static org.firstinspires.ftc.teamcode.Constants.LEFT_CLOSED_POSITION;
import static org.firstinspires.ftc.teamcode.Constants.LEFT_OPEN_POSITION;
import static org.firstinspires.ftc.teamcode.Constants.RIGHT_CLOSED_POSITION;
import static org.firstinspires.ftc.teamcode.Constants.RIGHT_OPEN_POSITION;

public class YeetSystem {

    public enum Servos {

        LEFT (LEFT_CLOSED_POSITION, LEFT_OPEN_POSITION, false), //values need to be changed
        RIGHT (RIGHT_CLOSED_POSITION, RIGHT_OPEN_POSITION, false); //values need to be changed

        private final double openPosition;
        private final double closedPosition;
        private boolean closed;

        Servos(double closedPosition, double openPosition, boolean closed) {
            this.closedPosition = closedPosition;
            this.openPosition = openPosition;
            this.closed = closed;
        }

        public double getOpenPosition() {
            return openPosition;
        }

        public double getClosedPosition() {
            return closedPosition;
        }

        public boolean isClosed() {
            return closed;
        }

        private void setLatched(boolean isClosed) {
            closed = isClosed;
        }
    }

    public EnumMap<Servos, Servo> servoMap;

    private final DcMotor motor; //one motor that we need

    private static final double TICKS_PER_REVOLUTION = Constants.TICKS_PER_REV; //number of ticks per revolution
    private static final double NUM_REVOLUTIONS = 0.8; // THIS NEEDS TO BE CHANGED - the number is num of revolutions
    private static final double UP_POSITION = TICKS_PER_REVOLUTION * NUM_REVOLUTIONS;

    private static final int DEFAULT = 0; // this needs to be changed

    public YeetSystem(DcMotor motor) { //constructor
        this.motor = motor; //setting ArmSystem motor to whatever motor that is
        init();
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    private void init() {
        down();
        release();
    }

    public void up() {
        grab();
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        while (motor.getCurrentPosition() != UP_POSITION){
            motor.setPower(0.75);
        }
    }

    public void down() {
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        while (motor.getCurrentPosition() != DEFAULT) {
            motor.setTargetPosition(DEFAULT);
        }
        motor.setPower(0.0);
    }

    public void grab() {
        servoMap.forEach((name, servo) -> {
            servo.setPosition(name.getClosedPosition());
        });
    }

    public void release() {
        servoMap.forEach((name, servo) -> {
            servo.setPosition(name.getOpenPosition());
        });
    }
}




