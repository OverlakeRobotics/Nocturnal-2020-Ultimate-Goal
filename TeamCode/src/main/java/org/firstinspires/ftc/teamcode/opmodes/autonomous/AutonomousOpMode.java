package org.firstinspires.ftc.teamcode.opmodes.autonomous;

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
    private int shotsLeft = 2;
    private int[] shotCount;

    // Systems
    private Tensorflow tensorflow;

    @Override
    public void init() {
        super.init();
        deliveredFirstWobble = false;
        tensorflow = new Tensorflow();
        tensorflow.activate();
        elapsedTime = new ElapsedTime();
        shotCount = new int[3];
        newGameState(GameState.INITIAL);
        elapsedTime.reset();
    }

    @Override
    public void init_loop() {
        if (elapsedTime.milliseconds() > 1500) {
            switch (tensorflow.getTargetRegion()) {
                case BOX_A:
                    shotCount[0]++;
                    break;

                case BOX_B:
                    shotCount[1]++;
                    break;

                case BOX_C:
                    shotCount[2]++;
                    break;
            }
        }
        updateTargetRegion();
    }

    private void updateTargetRegion() {
        int max = shotCount[0];
        max = Math.max(max, shotCount[1]);
        max = Math.max(max, shotCount[2]);
        if (shotCount[1] == max) {
            targetRegion = TargetDropBox.BOX_B;
        } else if (shotCount[2] == max) {
            targetRegion = TargetDropBox.BOX_C;
        } else {
            targetRegion = TargetDropBox.BOX_A;
        }
        telemetry.addData("Target Region", targetRegion.name());
        telemetry.update();
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
//        Log.d("POSITION", "STATE: " + currentGameState.name());
//        Log.d("POSITION", "x: " + poseEstimate.getX());
//        Log.d("POSITION","y: " + poseEstimate.getY());
//        Log.d("POSITION", "heading: " + poseEstimate.getHeading());

        trajectoryFinished = currentGameState == GameState.INITIAL || roadRunnerDriveSystem.update() || trajectory == null;

        // Makes sure the trajectory is finished before doing anything else
        if (trajectoryFinished) {
            switch (currentGameState) {
                case INITIAL:
                    shootingSystem.warmUp(Target.TOWER_GOAL);
                    newGameState(GameState.SHOOT_UPPER);
                    break;

//                case AVOID_RINGS:
//                    newGameState(GameState.SHOOT_UPPER);
//                    break;

                case SHOOT_UPPER:
                    if (shootingSystem.shoot()) {
                        if (shotsLeft < 1) {
                            newGameState(GameState.DELIVER_WOBBLE);
                            shootingSystem.shutDown();
                        }
                        shotsLeft--;
                    }
                    break;

                case DELIVER_WOBBLE:
                    if (yeetSystem.placed()) {
                        newGameState(GameState.RESET_ARM);
                        elapsedTime.reset();
                    }
                    break;
//                case WAIT_FOR_ARM_SERVO:
//                    if (elapsedTime.milliseconds() > 500) {
//                        newGameState(GameState.RESET_ARM);
//                    }
//                    break;
                case RESET_ARM:
                    if (yeetSystem.pickedUp(false)) {
                        if (deliveredFirstWobble) {
                            newGameState(GameState.RETURN_TO_NEST);
                        } else {
                            newGameState(GameState.DRIVE_TO_SECOND_WOBBLE_MIDWAY);
                        }
                        deliveredFirstWobble = true;
                    }
                    break;
                case DRIVE_TO_SECOND_WOBBLE_MIDWAY:
                    newGameState(GameState.DRIVE_TO_SECOND_WOBBLE);
                case DRIVE_TO_SECOND_WOBBLE:
                    if (yeetSystem.placed()) {
                        newGameState(GameState.STRAFE_FOR_SECOND_WOBBLE);
                    }
                    break;
                case STRAFE_FOR_SECOND_WOBBLE:
                    if (yeetSystem.pickedUp(true)) {
                        newGameState(GameState.DELIVER_WOBBLE);
                    }
                    break;
                case PICK_UP_SECOND_WOBBLE:
                    if (yeetSystem.pickedUp(false)) {
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
            trajectory = Trajectories.getTrajectory(targetRegion, currentPosition, deliveredFirstWobble);
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