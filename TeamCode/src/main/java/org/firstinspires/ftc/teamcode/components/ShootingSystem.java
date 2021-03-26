package org.firstinspires.ftc.teamcode.components;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.helpers.Target;

import static org.firstinspires.ftc.teamcode.helpers.Constants.SERVO_WAIT_TIME;
import static org.firstinspires.ftc.teamcode.helpers.Constants.SHOOTING_SERVO_IDLE_POSITION;
import static org.firstinspires.ftc.teamcode.helpers.Constants.SHOOTING_SERVO_SHOOT_POSITION;
import static org.firstinspires.ftc.teamcode.helpers.Constants.TICKS_PER_REV_SHOOTER;

public class ShootingSystem {

    // Systems
    private final DcMotorEx motor;
    public final Servo servo;
    private final ElapsedTime elapsedTime;

    // ShootingState
    private enum ShootingState {
        IDLE,
        SERVO_IDLE,
        SHOOT
    }
    private ShootingState currentShootingState;

    // Target
    private Target currentTarget;

    public ShootingSystem(DcMotorEx motor, Servo servo) {
        elapsedTime = new ElapsedTime();
        currentShootingState = ShootingState.IDLE;

        motor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
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
        servoIdle();
    }

    /**
     * Sets the target
     * @param target to shoot at
     */
    public void warmUp(Target target) {
        currentTarget = target;
        setMotorRpm(currentTarget.getRpm());
    }

    /**
     * Shuts down the shooter
     */
    public void shutDown() {
        setMotorRpm(0);
        servoIdle();
    }

    //TODO Code Review
    /**
     * Shoots a ring
     */
    public boolean shoot() {
        switch (currentShootingState) {
            case IDLE:
                servoIdle();
                currentShootingState = ShootingState.SHOOT;
                elapsedTime.reset();
                break;

            case SHOOT:
                if (elapsedTime.milliseconds() > SERVO_WAIT_TIME) {
                    servoShoot();
                    currentShootingState = ShootingState.IDLE;
                    elapsedTime.reset();
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
        motor.setVelocity(rpm / 60.0 * TICKS_PER_REV_SHOOTER);
    }

    /**
     * Moves servo to push ring into the motor
     */
    private void servoShoot() {
        servo.setPosition(SHOOTING_SERVO_SHOOT_POSITION);
    }

    /**
     * Resets servo to be ready for the next shot
     */
    private void servoIdle() {
        servo.setPosition(SHOOTING_SERVO_IDLE_POSITION);
    }
}
