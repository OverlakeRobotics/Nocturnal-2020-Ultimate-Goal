package org.firstinspires.ftc.teamcode.opmodes.autonomous;

import android.util.Log;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

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
    private ElapsedTime elapsedTime;
    private int shotsLeft = 3;

    // Systems
    private Tensorflow tensorflow;

    @Override
    public void init() {
        super.init();
        deliveredFirstWobble = false;
        tensorflow = new Tensorflow();
        tensorflow.activate();
        elapsedTime = new ElapsedTime();
        newGameState(GameState.INITIAL);
    }

    @Override
    public void init_loop() {
        targetRegion = tensorflow.getTargetRegion();
        Log.d("POSITION", "Target region: " + targetRegion.name());
    }

    @Override
    public void start() {
        tensorflow.shutdown();
        super.start();
    }

    @Override
    public void loop() {
//        vuforiaData();
        telemetry.addData("GameState", currentGameState);
        telemetry.update();

        Pose2d poseEstimate = roadRunnerDriveSystem.getPoseEstimate();
        Log.d("POSITION", "STATE: " + currentGameState.name());
        Log.d("POSITION", "x: " + poseEstimate.getX());
        Log.d("POSITION","y: " + poseEstimate.getY());
        Log.d("POSITION", "heading: " + poseEstimate.getHeading());

        trajectoryFinished = currentGameState == GameState.INITIAL || roadRunnerDriveSystem.update() || trajectory == null;

        // Makes sure the trajectory is finished before doing anything else
        if (trajectoryFinished) {
            switch (currentGameState) {
                case INITIAL:
                    newGameState(GameState.AVOID_RINGS);
                    break;

                case AVOID_RINGS:
                    newGameState(GameState.SHOOT_UPPER);
                    shootingSystem.warmUp(Target.TOWER_GOAL);
                    break;

                case SHOOT_UPPER:
                    if (shootingSystem.shoot()) {
                        if (shotsLeft <= 0) {
                            newGameState(GameState.DELIVER_WOBBLE);
                            shootingSystem.shutDown();
                        }
                        shotsLeft--;
                    }
                    break;

                case DELIVER_WOBBLE:
                    if (yeetSystem.placed()) {
                        newGameState(GameState.WAIT_FOR_ARM_SERVO);
                        elapsedTime.reset();
                    }
                    break;
                case WAIT_FOR_ARM_SERVO:
                    if (elapsedTime.milliseconds() > 500) {
                        newGameState(GameState.RESET_ARM);
                    }
                    break;
                case RESET_ARM:
                    if (yeetSystem.pickedUp(false)) {
                        newGameState(GameState.RETURN_TO_NEST);
                    }
                    break;
                case RETURN_TO_NEST:
                    newGameState(GameState.COMPLETE);
                    break;
//                case START_SHOOTER:
//                    shootingSystem.warmUp(Target.POWER_SHOT);
//                    newGameState(GameState.POWERSHOT_1);
//                    break;

                case POWERSHOT_1:
                    if (shootingSystem.shoot()) {
                        newGameState(GameState.POWERSHOT_2);
                    }
                    break;

                case POWERSHOT_2:
                    if (shootingSystem.shoot()) {
                        newGameState(GameState.POWERSHOT_3);
                    }
                    break;

                case POWERSHOT_3:
                    if (shootingSystem.shoot()) {
                        shootingSystem.shutDown();
                        newGameState(GameState.PARK_ON_LINE);
                    }
                    break;
                case PARK_ON_LINE:
                    newGameState(GameState.COMPLETE);
                    break;

                case PICK_UP_SECOND_WOBBLE:
                    if (yeetSystem.pickedUp(false)) {
                        newGameState(GameState.DELIVER_WOBBLE);
                    }
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