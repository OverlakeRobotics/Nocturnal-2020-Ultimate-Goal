package org.firstinspires.ftc.teamcode.components;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.opmodes.base.BaseOpMode;

public class ShootingSystem {

    //TODO Find out powers for each goal
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

    private final DcMotor motor;
    private final Servo servo;
    private boolean servoClosed;

    private static final double CLOSED_POSITION = 0; // TODO, Find position values.
    private static final double OPEN_POSITION = 0;

    private Target currentTarget;

    public ShootingSystem(DcMotor motor, Servo servo) {
        this.motor = motor;
        this.servo = servo;
        initMotors();
    }

    /**
     * Initializes the motor and servo
     */
    private void initMotors() {
        // Motor
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //TODO Figure out what direction is forward and set it
        motor.setDirection(DcMotorSimple.Direction.REVERSE);
        setMotorPower(0);

        // Servo
        servo.setPosition(CLOSED_POSITION);
        servoClosed = true;
    }

    /**
     * Sets the target
     * @param target to shoot at
     */
    public void warmUp(Target target) {
        currentTarget = target;
        setMotorPower(currentTarget.getPower());
    }

    //TODO implement stop shooter
    /**
     * Shuts down the shooter
     */
    public void shutDown() {
        setMotorPower(0);
        if (!servoClosed) close();
    }

    //TODO implement the shooting method
    /**
     * Shoots a ring
     */
    public void shoot() {
        if (BaseOpMode.getRingCount() > 0) {
            open();
            close();
            BaseOpMode.subtractRingCount();
        }
    }

    /**
     * Method to set motor power manually rather than using given constants
     * @param power the motor will be set to
     */
    private void setMotorPower(double power) {
        motor.setPower(power);
    }

    /**
     * Opens servo
     */
    private void open() {
        servo.setPosition(OPEN_POSITION);
        servoClosed = false;
    }

    /**
     * Closes servo
     */
    private void close() {
        servo.setPosition(CLOSED_POSITION);
        servoClosed = true;
    }
}
