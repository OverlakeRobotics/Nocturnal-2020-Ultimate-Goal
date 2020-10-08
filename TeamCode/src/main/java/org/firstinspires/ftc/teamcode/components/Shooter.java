package org.firstinspires.ftc.teamcode.components;

import android.util.Log;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;
import java.util.EnumMap;

public class Shooter {

    public enum MotorNames {
        MOTOR_ONE, MOTOR_TWO;
    }

    public static final double REVERSE_FULL_POWER = -1.0;

    public static final double FORWARD_FULL_POWER = 1.0;

    public static final double MIN_SPEED = 0.37; //assuming you're the closest possible distance

    public static final double MAXIMUM_SPEED = 0.68; //assuming you're the farthest but reasonable distance away

    // some inches of the two wheels that will cause the thing to spin
    // some circumference of a wheel
    // 1120 ticks in a revolution
    // 1120 / circumference = answer
    private final double TICKS_IN_MM = 2.1; //needs to be updated

    public static final String TAG = "ShooterSystem";

    public EnumMap<MotorNames, DcMotor> shooterMotors;

    public static final double SLOW_DRIVE_COEFF = 0.4;
    // Gives the point at which to switch to less than full power


    public boolean mSlowDrive;

    public Shooter(EnumMap<MotorNames, DcMotor> motors) {
        this.shooterMotors = motors;
        initMotors();
    }


    public void setMotorPower(double power) {
        for (DcMotor motor : shooterMotors.values()) {
            motor.setPower(power);
        }
    }

    public void initMotors() {
        shooterMotors.forEach((name, motor) -> {
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            switch(name) {
                case MOTOR_ONE:
                    motor.setDirection(DcMotorSimple.Direction.REVERSE);
                    break;
                case MOTOR_TWO:
                    motor.setDirection(DcMotorSimple.Direction.FORWARD);
                    break;
            }
        });
        setMotorPower(0);
    }


    private void setSpinSpeed(DcMotor motor, double motorPower) {
        motor.setPower(Range.clip(mSlowDrive ?
                SLOW_DRIVE_COEFF * motorPower : motorPower, -1, 1));
    }


    public void stopAndReset() {
        setMotorPower(0.0);
        setRunMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void setRunMode(DcMotor.RunMode runMode) {
        for (DcMotor motor : shooterMotors.values()) {
            motor.setMode(runMode);
        }
    }

    /**
     * Converts millimeters to ticks
     * @param millimeters Millimeters to convert to ticks
     * @return number of ticks
     */
    private int millimetersToTicks(int millimeters) {
        return (int) Math.round(millimeters * TICKS_IN_MM);
    }

}