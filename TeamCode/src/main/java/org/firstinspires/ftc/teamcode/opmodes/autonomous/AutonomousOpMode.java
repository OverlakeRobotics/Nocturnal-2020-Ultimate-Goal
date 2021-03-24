package org.firstinspires.ftc.teamcode.opmodes.autonomous;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.components.Tensorflow;
import org.firstinspires.ftc.teamcode.helpers.Constants;
import org.firstinspires.ftc.teamcode.helpers.Coordinates;
import org.firstinspires.ftc.teamcode.helpers.GameState;
import org.firstinspires.ftc.teamcode.helpers.Target;
import org.firstinspires.ftc.teamcode.helpers.TargetDropBox;
import org.firstinspires.ftc.teamcode.helpers.Trajectories;
import org.firstinspires.ftc.teamcode.opmodes.base.BaseOpMode;

@Autonomous(name = "AutonomousOpMode", group = "Autonomous")
public class AutonomousOpMode extends BaseOpMode {

    // Variables
    private GameState currentGameState;                         // Current GameState Machine GameState.
    private static TargetDropBox targetRegion;
    private boolean deliveredFirstWobble;
    private boolean isTurning;

    // Systems
    private Tensorflow tensorflow;

    @Override
    public void init() {
        super.init();
        deliveredFirstWobble = false;
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
        telemetry.update();

        trajectoryFinished = roadRunnerDriveSystem.update();

        // Makes sure the trajectory is finished before doing anything else
        if (trajectoryFinished) {
            switch (currentGameState) {
                case INITIAL:
                   // Initialize
                    newGameState(GameState.AVOID_RINGS);
                    break;

                case AVOID_RINGS:
                    if (!isTurning) {
                        roadRunnerDriveSystem.turnAsync(-Math.PI / 2);
                        isTurning = true;
                    } else if (roadRunnerDriveSystem.update()) {
                        isTurning = false;
                        newGameState(GameState.DELIVER_WOBBLE);
                    }
                    break;

                case DELIVER_WOBBLE:
                    if (yeetSystem.placed()) {
                        yeetSystem.pickedUp();
                        newGameState(deliveredFirstWobble ? GameState.RETURN_TO_NEST : GameState.CALIBRATE_LOCATION);
                    }
                    break;

                case CALIBRATE_LOCATION:
                    deliveredFirstWobble = true;
                    calibrateLocation();
                    shootingSystem.warmUp(Target.POWER_SHOT);
                    newGameState(GameState.POWERSHOT);
                    break;

                case POWERSHOT:
                    if (powerShotRoutine()) {
                        newGameState(GameState.PICK_UP_SECOND_WOBBLE);
                    }
                    break;

                case PICK_UP_SECOND_WOBBLE:
                    if (yeetSystem.pickedUp()) {
                        newGameState(GameState.DELIVER_WOBBLE);
                    }
                    break;

                case RETURN_TO_NEST:
                    newGameState(GameState.COMPLETE);
                    break;

                case COMPLETE:
                    stop();
                    break;
            }
        }
    }

    @Override
    public void stop() {
        super.stop();
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

        if (currentGameState == GameState.DELIVER_WOBBLE) {
            trajectory = Trajectories.getTrajectory(targetRegion, currentPosition);
        } else {
            trajectory = Trajectories.getTrajectory(currentGameState, currentPosition);
        }

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
        double xUpdate = Coordinates.CALIBRATION.getX() - (vuforia.getYOffset() / Constants.mmPerInch - Constants.tileWidth);
        double yUpdate = Coordinates.CALIBRATION.getY() + vuforia.getXOffset() / Constants.mmPerInch;
        roadRunnerDriveSystem.setPoseEstimate(new Pose2d(xUpdate, yUpdate));
    }
}