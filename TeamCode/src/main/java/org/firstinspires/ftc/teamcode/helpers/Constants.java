package org.firstinspires.ftc.teamcode.helpers;

import com.acmerobotics.roadrunner.control.PIDCoefficients;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.constraints.DriveConstraints;

/**
 *
 */

public class Constants {

    // Debugging captions
    public static final String ROBOT_SYSTEM_ERROR = "ROBOTSYSTEMERROR";

    private final static int CPS_STEP = 0x10000;

    public static int TICKS_PER_REV = 2400;
    public static int TICKS_PER_REV_SHOOTER = 103;
    public static double WHEEL_RADIUS = 1; // in
    public static double GEAR_RATIO = 1; // output (wheel) speed / input (encoder) speed

    public static double LATERAL_DISTANCE = 4.212; // in; distance between the left and right wheels
    public static double FORWARD_OFFSET = -9.97; // in; offset of the lateral wheel

    public static double X_MULTIPLIER = 1.0;
    public static double Y_MULTIPLIER = 1.0;
    /*
     * These are motor constants that should be listed online for your motors.
     */
    public static final double MAX_RPM = 160;

    /*
     * Set the first flag appropriately. If using the built-in motor velocity PID, update
     * MOTOR_VELO_PID with the tuned coefficients from DriveVelocityPIDTuner.
     */
    public static final boolean RUN_USING_ENCODER = false;
    public static final PIDCoefficients MOTOR_VELO_PID = null;

    /*
     * These are physical constants that can be determined from your robot (including the track
     * width; it will be tune empirically later although a rough estimate is important). Users are
     * free to chose whichever linear distance unit they would like so long as it is consistently
     * used. The default values were selected with inches in mind. Road runner uses radians for
     * angular distances although most angular parameters are wrapped in Math.toRadians() for
     * convenience. Make sure to exclude any gear ratio included in MOTOR_CONFIG from GEAR_RATIO.
     */

    // TODO
    public static double TRACK_WIDTH = 3; // in

    /*
     * These are the feedforward parameters used to model the drive motor behavior. If you are using
     * the built-in velocity PID, *these values are fine as is*. However, if you do not have drive
     * motor encoders or have elected not to use them for velocity control, these values should be
     * empirically tuned.
     */
    public static double kV = 1.0 / rpmToVelocity(MAX_RPM);
    public static double kA = 0;
    public static double kStatic = 0;

    /*
     * These values are used to generate the trajectories for you robot. To ensure proper operation,
     * the constraints should never exceed ~80% of the robot's actual capabilities. While Road
     * Runner is designed to enable faster autonomous motion, it is a good idea for testing to start
     * small and gradually increase them later after everything is working. The velocity and
     * acceleration values are required, and the jerk values are optional (setting a jerk of 0.0
     * forces acceleration-limited profiling). All distance units are inches.
     */
    public static DriveConstraints BASE_CONSTRAINTS = new DriveConstraints(
            15, 15, 0.0,
            Math.toRadians(180.0), Math.toRadians(180.0), 0.0
    );


    public static double encoderTicksToInches(double ticks) {
        return WHEEL_RADIUS * 2 * Math.PI * GEAR_RATIO * ticks / TICKS_PER_REV;
    }

    public static double rpmToVelocity(double rpm) {
        return rpm * GEAR_RATIO * 2 * Math.PI * WHEEL_RADIUS / 60.0;
    }

    public static double getMotorVelocityF() {
        // see https://docs.google.com/document/d/1tyWrXDfMidwYyP_5H4mZyVgaEswhOC35gvdmP-V-5hA/edit#heading=h.61g9ixenznbx
        return 32767 * 60.0 / (MAX_RPM * TICKS_PER_REV);
    }

    //Field
    public static final int tileWidth = 24;

    //Vuforia
    public static final float mmPerInch = 25.4f;                    // constant for converting measurements from inches to millimeters
    public static final float mmTargetHeight = (6) * mmPerInch;          // the height of the center of the target image above the floor
    public static final float halfField = 72 * mmPerInch;                  // constants for perimeter targets
    public static final float quadField = 36 * mmPerInch;

    //Shooter Constants
    public static final float powerShotY = 78;
    // TODO, Find position values.
    public static final double SHOOTING_SERVO_IDLE_POSITION = 0.5;
    public static final double SHOOTING_SERVO_SHOOT_POSITION = 0.7;

    Vector2d negativeFirstPowerShotCoordinates = new Vector2d(-4 * tileWidth - (23.5f * 2 + 4.25f - ((44 * 10) / Constants.mmPerInch / 2)), powerShotY);
    public static final Vector2d firstPowerShotCoordinates = new Vector2d(4 * tileWidth - (23.5f * 2 + 4.25f - ((44 * 10) / mmPerInch / 2)), powerShotY);
    public static final Vector2d secondPowerShotCoordinates = new Vector2d(firstPowerShotCoordinates.getX() - 6.75, 78);
    public static final Vector2d thirdPowerShotCoordinates = new Vector2d(secondPowerShotCoordinates.getX() - 8.5, powerShotY);

    //YeetSystem
    //TODO Change these numbers
    public static final double ARM_MOTOR_NUM_REVOLUTIONS = 0.6; // THIS NEEDS TO BE CHANGED - the number is num of revolutions
    public static final int ARM_MOTOR_DOWN_POSITION = (int)(Constants.TICKS_PER_REV * ARM_MOTOR_NUM_REVOLUTIONS);
    public static final int ARM_MOTOR_UP_POSITION = 0; // this needs to be changed
    public static final double ARM_MOTOR_RAW_POWER = 0.6;
    public static final double LEFT_ARM_SERVO_CLOSED_POSITION = 0.8;
    public static final double RIGHT_ARM_SERVO_CLOSED_POSITION = 0.0;
    public static final double LEFT_ARM_SERVO_OPEN_POSITION = 0.16;
    public static final double RIGHT_ARM_SERVO_OPEN_POSITION = 0.4;
    public static final int SERVO_WAIT_TIME = 500;
}
