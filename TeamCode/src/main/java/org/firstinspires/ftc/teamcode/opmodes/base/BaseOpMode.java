package org.firstinspires.ftc.teamcode.opmodes.base;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.teamcode.GameState;
import org.firstinspires.ftc.teamcode.components.Coordinates;
import org.firstinspires.ftc.teamcode.components.IntakeSystem;
import org.firstinspires.ftc.teamcode.components.RoadRunnerDriveSystem;
import org.firstinspires.ftc.teamcode.components.ShootingSystem;
import org.firstinspires.ftc.teamcode.components.Trajectories;
import org.firstinspires.ftc.teamcode.components.VuforiaSystem;
import org.firstinspires.ftc.teamcode.components.YeetSystem;

public abstract class BaseOpMode extends OpMode {

    // Variables
    private static final float mmPerInch = 25.4f;
    protected boolean trajectoryFinished;
    protected Pose2d currentPosition;
    protected static int ringCount;

    // Systems
    protected RoadRunnerDriveSystem roadRunnerDriveSystem;
    protected VuforiaSystem vuforia;
    protected Trajectory trajectory;
    protected ShootingSystem shootingSystem;
    protected IntakeSystem intakeSystem;
    protected YeetSystem yeetSystem;

    @Override
    public void init() {
        this.msStuckDetectInit = 20000;
        this.msStuckDetectInitLoop = 20000;
        ringCount = 3;

        currentPosition = new Pose2d(Coordinates.STARTING_POSITION.getX(), Coordinates.STARTING_POSITION.getY(), Math.PI);
        vuforia = VuforiaSystem.getInstance();

        //TODO initialize RoadRunnerDriveSystem, ShootingSystem, and IntakeSystem once hardware online
        //Initialize RoadRunner
        try {
            roadRunnerDriveSystem = new RoadRunnerDriveSystem(hardwareMap);
            roadRunnerDriveSystem.setPoseEstimate(currentPosition);
        } catch (Exception e) {

        }

        try {
            shootingSystem = new ShootingSystem(hardwareMap.get(DcMotor.class, "ShootingSystem"), hardwareMap.get(Servo.class, "ShootingSystemServo"));
        } catch (Exception e) {

        }

        try {
            intakeSystem = new IntakeSystem(hardwareMap.get(DcMotor.class, "IntakeSystem"));
        } catch (Exception e) {

        }

        try {
            yeetSystem = new YeetSystem(hardwareMap.get(DcMotor.class, "YeetSystem"));
        } catch (Exception e) {

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
                    translation.get(0) / mmPerInch, translation.get(1) / mmPerInch, translation.get(2) / mmPerInch);
        }
        telemetry.addData("Pos (in)", "{X, Y, Z} = %.1f, %.1f, %.1f",
                vuforia.getXOffset() / mmPerInch, vuforia.getYOffset() / mmPerInch, vuforia.getZOffset() / mmPerInch);

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
        while (!trajectoryFinished) trajectoryFinished = roadRunnerDriveSystem.update();
        shootingSystem.shoot();
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

        if (intakeSystem != null) {
            intakeSystem.stop();
        }
    }
}
