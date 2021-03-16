package org.firstinspires.ftc.teamcode.components;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.helpers.Target;

import static org.firstinspires.ftc.teamcode.helpers.Constants.SERVO_WAIT_TIME;
import static org.firstinspires.ftc.teamcode.helpers.Constants.SHOOTING_SERVO_CLOSED_POSITION;
import static org.firstinspires.ftc.teamcode.helpers.Constants.SHOOTING_SERVO_OPEN_POSITION;
import static org.firstinspires.ftc.teamcode.helpers.Constants.TICKS_PER_REV;

public class ShootingSystem {

    // Systems
    private final DcMotorEx motor;
    public final Servo servo;
    private ElapsedTime elapsedTime;

    private enum ShootingState {
        IDLE,
        OPEN,
        CLOSE
    }

    private ShootingState shootingState;

    // Target
    private Target currentTarget;

    public ShootingSystem(DcMotorEx motor, Servo servo) {
        elapsedTime = new ElapsedTime();
        shootingState = ShootingState.IDLE;

        this.motor = motor;
        this.servo = servo;
        initMotors();
    }

    /**
     * Initializes the motor and servo
     */
    private void initMotors() {
        // Motors
        motor.setDirection(DcMotorSimple.Direction.REVERSE);
        setMotorRpm(0);

        // Servos
        close();
    }

    /**
     * Sets the target
     * @param target to shoot at
     */
    public void warmUp(Target target) {
        currentTarget = target;
        setMotorRpm(currentTarget.getRpm());
    }

    //TODO implement stop shooter
    /**
     * Shuts down the shooter
     */
    public void shutDown() {
        setMotorRpm(0);
        close();
    }

    //TODO implement the shooting method
    /**
     * Shoots a ring
     */
    public boolean shoot() {
        switch (shootingState) {
            case IDLE:
                elapsedTime.reset();
                shootingState = ShootingState.OPEN;
                open();
                break;
            case OPEN:
                if (elapsedTime.milliseconds() > SERVO_WAIT_TIME) {
                    elapsedTime.reset();
                    close();
                    shootingState = ShootingState.CLOSE;
                }
                break;
            case CLOSE:

                if (elapsedTime.milliseconds() > SERVO_WAIT_TIME) {
                    elapsedTime.reset();
                    shootingState = ShootingState.IDLE;
                }
                return true;

        }

        return false;
    }

    /**
     * Method to set motor power manually rather than using given constants
     * @param rpm the motor will be set to
     */
    private void setMotorRpm(double rpm) {
        motor.setVelocity(rpm / 60.0 * TICKS_PER_REV);
    }

    /**
     * Opens servo
     */
    private void open() {
        servo.setPosition(SHOOTING_SERVO_OPEN_POSITION);
    }

    /**
     * Closes servo
     */
    private void close() {
        servo.setPosition(SHOOTING_SERVO_CLOSED_POSITION);
    }
}
