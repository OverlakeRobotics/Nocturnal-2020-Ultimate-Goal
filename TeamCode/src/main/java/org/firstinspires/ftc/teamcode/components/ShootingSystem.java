package org.firstinspires.ftc.teamcode.components;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.helpers.Target;
import org.firstinspires.ftc.teamcode.opmodes.base.BaseOpMode;

import static org.firstinspires.ftc.teamcode.helpers.Constants.SERVO_WAIT_TIME;
import static org.firstinspires.ftc.teamcode.helpers.Constants.SHOOTING_SERVO_CLOSED_POSITION;
import static org.firstinspires.ftc.teamcode.helpers.Constants.SHOOTING_SERVO_OPEN_POSITION;

public class ShootingSystem {

    // Systems
    private final DcMotor motor;
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

    public ShootingSystem(DcMotor motor, Servo servo) {
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
        setMotorPower(0);

        // Servos
        close();
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
                BaseOpMode.subtractRingCount();
                return true;

        }

        return false;
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
        servo.setPosition(SHOOTING_SERVO_OPEN_POSITION);
    }

    /**
     * Closes servo
     */
    private void close() {
        servo.setPosition(SHOOTING_SERVO_CLOSED_POSITION);
    }
}
