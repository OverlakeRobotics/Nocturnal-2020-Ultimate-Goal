package org.firstinspires.ftc.teamcode.opmodes.base;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.teamcode.helpers.Constants;
import org.firstinspires.ftc.teamcode.helpers.GameState;
import org.firstinspires.ftc.teamcode.helpers.Coordinates;
import org.firstinspires.ftc.teamcode.components.RoadRunnerDriveSystem;
import org.firstinspires.ftc.teamcode.components.ShootingSystem;
import org.firstinspires.ftc.teamcode.components.Trajectories;
import org.firstinspires.ftc.teamcode.components.VuforiaSystem;
import org.firstinspires.ftc.teamcode.components.YeetSystem;

public abstract class BaseOpMode extends OpMode {

    // Variables
    protected boolean trajectoryFinished;
    protected Pose2d currentPosition;
    protected static int ringCount;
    private boolean hasFired;

    // Systems
    protected RoadRunnerDriveSystem roadRunnerDriveSystem;
    protected VuforiaSystem vuforia;
    protected Trajectory trajectory;
    protected ShootingSystem shootingSystem;
    protected YeetSystem yeetSystem;

    @Override
    public void init() {
        this.msStuckDetectInit = 20000;
        this.msStuckDetectInitLoop = 20000;
        ringCount = 3;
        hasFired = false;

        currentPosition = new Pose2d(Coordinates.STARTING_POSITION.getX(), Coordinates.STARTING_POSITION.getY(), Math.PI);
        vuforia = VuforiaSystem.getInstance();

        //TODO initialize RoadRunnerDriveSystem, ShootingSystem, and IntakeSystem once hardware online
        //Initialize RoadRunner
        try {
            roadRunnerDriveSystem = new RoadRunnerDriveSystem(hardwareMap);
            roadRunnerDriveSystem.setPoseEstimate(currentPosition);
        } catch (Exception e) {
            telemetry.addData(Constants.ROBOT_SYSTEM_ERROR, e.getStackTrace());
        }

        try {
            shootingSystem = new ShootingSystem(hardwareMap.get(DcMotor.class, "ShootingSystem"), hardwareMap.get(Servo.class, "ShootingSystemServo"));
        } catch (Exception e) {
            telemetry.addData(Constants.ROBOT_SYSTEM_ERROR, e.getStackTrace());
        }

        try {
            yeetSystem = new YeetSystem(hardwareMap.get(DcMotor.class, "YeetSystem"), hardwareMap.get(Servo.class, "LeftArmServo"), hardwareMap.get(Servo.class, "RightArmServo"));
        } catch (Exception e) {
            telemetry.addData(Constants.ROBOT_SYSTEM_ERROR, e.getStackTrace());
        }
    }

    @Override
    public void start() {
        vuforia.activate();
    }

    /**
     * Initializes Vuforia data
     */
    protected void vuforiaData() {
        VectorF translation = vuforia.vector();

        // only one of these two will be used
        if (translation != null) {
            telemetry.addData("Pos (in)", "{X, Y, Z} = %.1f, %.1f, %.1f",
                    translation.get(0) / Constants.mmPerInch, translation.get(1) / Constants.mmPerInch, translation.get(2) / Constants.mmPerInch);
        }
        telemetry.addData("Pos (in)", "{X, Y, Z} = %.1f, %.1f, %.1f",
                vuforia.getXOffset() / Constants.mmPerInch, vuforia.getYOffset() / Constants.mmPerInch, vuforia.getZOffset() / Constants.mmPerInch);

        if (translation != null) {
            telemetry.addLine("null");
        } else {
            telemetry.addLine("not null");
        }
    }

    /**
     * Powershot routine
     */
    protected void powershotRoutine() {
        // Shoot 1
        singlePowershot(GameState.SHOOT1);

        // Shoot 2
        singlePowershot(GameState.SHOOT2);

        // Shoot 3
        singlePowershot(GameState.SHOOT3);
    }

    /**
     * Assumes shooter is set to State Powershot
     * @param shot number to be performed
     */
    private void singlePowershot(GameState shot) {
        trajectory = Trajectories.getTrajectory(shot, currentPosition);
        trajectoryFinished = false;
        roadRunnerDriveSystem.followTrajectoryAsync(trajectory);
        trajectoryFinished = roadRunnerDriveSystem.update();
        if (trajectoryFinished) {
            shootingSystem.shoot();
            hasFired = true;
        }
    }

    /**
     * Gets the number of rings on the robot
     * @return the number of rings on the robot
     */
    public static int getRingCount() {
        return ringCount;
    }

    /**
     * Adds a ring to the ring count
     */
    public static void addRingCount() {
        ringCount++;
    }

    /**
     * Subtracts a ring to the ring count
     */
    public static void subtractRingCount() {
        ringCount--;
    }

    @Override
    public void stop() {
        if (vuforia != null) {
            vuforia.disable();
        }

        if (shootingSystem != null) {
            shootingSystem.shutDown();
        }
    }
}
