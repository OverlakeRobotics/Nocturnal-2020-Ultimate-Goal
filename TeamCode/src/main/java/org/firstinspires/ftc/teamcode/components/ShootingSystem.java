package org.firstinspires.ftc.teamcode.components;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

public class ShootingSystem {

    //TODO Find out powers for each goal
    //TODO 1 servo and 1 motor total
    public enum Target {
        POWER_SHOT (1.0),
        TOWER_GOAL (1.0);

        private final double power;
        Target(double power) {
            this.power = power;
        }

        double getPower() {
            return power;
        }
    }

    public static final String TAG = "ShootingSystem";

    private DcMotor motor;
    private Servo servo;
    private boolean closed;

    private static final double CLOSED_POSITION = 0; // TODO, Find position values.
    private static final double OPEN_POSITION = 0;

    private Target currentTarget;

    public ShootingSystem(DcMotor motor, Servo servo) {
        this.motor = motor;
        this.servo = servo;
        servo.setPosition(CLOSED_POSITION);
        closed = true;
        initMotors();
    }

    private void open(){
        if (closed){
            servo.setPosition(OPEN_POSITION);
            closed = false;
        }
        else{

        }
    }

    private void close(){
        if (!closed){
            servo.setPosition(CLOSED_POSITION);
            closed = true;
        }
    }

    private void toggle(){
        if (closed){
            open();
        }
        else{
            close();
        }
    }

    /**
     * Initializes the motors
     */
    private void initMotors() {
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //TODO Figure out what direction is forward and set it
        motor.setDirection(DcMotorSimple.Direction.REVERSE);
        setMotorPower(0);
    }

    /**
     * Sets the target
     * @param target to shoot at
     */
    public void init(Target target) {
        currentTarget = target;
        start();
    }

    //TODO implement start motor
    /**
     * Starts the shooter
     */
    private void start() {
        motor.setPower(currentTarget.getPower());
    }

    //TODO implement stop shooter
    /**
     * Stops the shooter
     */
    public void stop() {
        setMotorPower(0);
    }

    //TODO implement the shooting method
    /**
     * Shoots a ring
     */
    public void shoot() {

    }

    /**
     * Method to set motor power manually rather than using given constants
     * @param power the motor will be set to
     */
    public void setMotorPower(double power) {
        motor.setPower(power);
    }
}
