package org.firstinspires.ftc.teamcode.tests;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.teamcode.components.RoadRunnerDriveSystem;
import org.firstinspires.ftc.teamcode.components.Tensorflow;
import org.firstinspires.ftc.teamcode.components.VuforiaSystem;
import org.firstinspires.ftc.teamcode.helpers.Constants;
import org.firstinspires.ftc.teamcode.helpers.Coordinates;
import org.firstinspires.ftc.teamcode.helpers.GameState;

@Autonomous(name = "AutonomousDriveTest", group = "Autonomous")
public class AutonomousDriveTest extends OpMode {

    // Variables
    protected GameState currentGameState;
    public static Tensorflow.SquareState targetRegion;
    protected boolean deliveredFirstWobble;

    // Systems
    protected Tensorflow tensorflow;

    // Variables
    private static final float mmPerInch = 25.4f;
    protected Pose2d currentPosition;

    // Systems
    protected RoadRunnerDriveSystem roadRunnerDriveSystem;
    protected VuforiaSystem vuforia;

    @Override
    public void init() {
        this.msStuckDetectInit = 20000;
        this.msStuckDetectInitLoop = 20000;

        currentPosition = new Pose2d(Coordinates.STARTING_POSITION.getX(), Coordinates.STARTING_POSITION.getY(), Math.PI);
        vuforia = VuforiaSystem.getInstance();

        //Initialize RoadRunner
        try {
            roadRunnerDriveSystem = new RoadRunnerDriveSystem(hardwareMap);
            roadRunnerDriveSystem.setPoseEstimate(currentPosition);
        } catch (Exception e) {
            telemetry.addData(Constants.ROBOT_SYSTEM_ERROR, e.getStackTrace());
        }

        tensorflow = new Tensorflow(hardwareMap.appContext.getResources().getIdentifier("tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName()));
        tensorflow.activate();
        newGameState(GameState.INITIAL);
    }

    @Override
    public void init_loop() {
        targetRegion = tensorflow.getTargetRegion();
    }

    @Override
    public void start() {
        tensorflow.shutdown();
        super.start();
    }

    @Override
    public void loop() {
        vuforiaData();
        telemetry.addData("GameState", currentGameState);


        // Makes sure the trajectory is finished before doing anything else
        boolean trajectoryFinished = roadRunnerDriveSystem.update();
        Pose2d poseEstimate = roadRunnerDriveSystem.getPoseEstimate();
        telemetry.addData("x", poseEstimate.getX());
        telemetry.addData("y", poseEstimate.getY());
        telemetry.addData("heading", poseEstimate.getHeading());
        telemetry.update();

        switch (currentGameState) {
            case INITIAL:
                // Initialize
                newGameState(GameState.DELIVER_WOBBLE);
                break;

            case DELIVER_WOBBLE:
                if (trajectoryFinished) {
                    newGameState(GameState.CALIBRATE_LOCATION);
                }
                break;

            case CALIBRATE_LOCATION:
                if (trajectoryFinished) {
                    newGameState(GameState.COMPLETE);
                }
                break;

            case DRIVE_TO_SHOOTING_LOCATION:
                //TODO drive to shooting location, start up shooter motor
                newGameState(GameState.POWERSHOT);
                break;

            case POWERSHOT:
                //TODO do the powershot routine
                newGameState(GameState.DRIVE_TO_SECOND_WOBBLE);
                break;

            case DRIVE_TO_SECOND_WOBBLE:
                //TODO drive to the second wobble goal
                newGameState(GameState.COLLECT_SECOND_WOBBLE);
                break;

            case COLLECT_SECOND_WOBBLE:
                //TODO position the robot and collect the second wobble goal
                newGameState(GameState.DELIVER_WOBBLE);
                break;

            case RETURN_TO_NEST:
                //TODO drive back to nest
                newGameState(GameState.COMPLETE);
                break;

            case COMPLETE:
                //TODO park the robot, shut down system, and release used resources
                stop();
                break;
        }
    }

    @Override
    public void stop() {
        if (vuforia != null) {
            vuforia.disable();
        }

        if (tensorflow != null) {
            tensorflow.shutdown();
        }
    }

    /**
     * Updates the state of the system and updates RoadRunner trajectory
     * @param newGameState to switch to
     */
    protected void newGameState(GameState newGameState) {
        currentGameState = newGameState;
        currentPosition = roadRunnerDriveSystem.getPositionEstimate();
        Trajectory trajectory = TestTrajectories.getTrajectory(currentGameState, currentPosition);
        if (trajectory != null) {
            roadRunnerDriveSystem.followTrajectoryAsync(trajectory);
        }
    }

    /**
     * Calibrates RoadRunner using Vuforia data
     * Because camera is sideways, the x offset corresponds to y coordinates and visa versa
     * Vuforia is in millimeters and everything else is in inches
     */
    private void calibrateLocation() {
        double xUpdate = Coordinates.CALIBRATION.getX() - (vuforia.getYOffset() / Constants.mmPerInch - Constants.fieldBoxWidth);
        double yUpdate = Coordinates.CALIBRATION.getY() + vuforia.getXOffset() / Constants.mmPerInch;
        roadRunnerDriveSystem.setPoseEstimate(new Pose2d(xUpdate, yUpdate));
    }

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
}