package org.firstinspires.ftc.teamcode.tests;

import android.util.Log;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.components.ShootingSystem;
import org.firstinspires.ftc.teamcode.components.Tensorflow;
import org.firstinspires.ftc.teamcode.components.YeetSystem;
import org.firstinspires.ftc.teamcode.helpers.Constants;
import org.firstinspires.ftc.teamcode.helpers.Coordinates;
import org.firstinspires.ftc.teamcode.helpers.GameState;
import org.firstinspires.ftc.teamcode.helpers.PowerShotState;
import org.firstinspires.ftc.teamcode.helpers.Target;
import org.firstinspires.ftc.teamcode.helpers.TargetDropBox;
import org.firstinspires.ftc.teamcode.helpers.Trajectories;
import org.firstinspires.ftc.teamcode.opmodes.base.BaseOpMode;

@Autonomous(name = "AutonomousDriveTest", group = "Autonomous")
public class AutonomousDriveTest extends BaseOpMode {

    //TODO add method that keeps looping until see target
    //TODO make sure CALIBRATE_LOCATION is moving onto the next state

    // Variables
    private GameState currentGameState;                         // Current GameState Machine GameState.
    private static TargetDropBox targetRegion;
    private boolean deliveredFirstWobble;
    private boolean isTurning;
    private ElapsedTime elapsedTime;

    // Systems
    private Tensorflow tensorflow;

    @Override
    public void init() {
        super.init();
        elapsedTime = new ElapsedTime();
        deliveredFirstWobble = false;
        isTurning = false;
        tensorflow = new Tensorflow();
        elapsedTime = new ElapsedTime();
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
//        vuforiaData();
        telemetry.addData("GameState", currentGameState);
        telemetry.update();

        if (currentGameState == GameState.CALIBRATE_LOCATION) trajectoryFinished = true;

        switch (currentGameState) {
            case INITIAL:
                // Initialize
                newGameState(GameState.CALIBRATE_LOCATION);
                break;

            case AVOID_RINGS:
                if (trajectoryFinished && !isTurning) {
                    roadRunnerDriveSystem.turnAsync(-Math.PI / 2);
                    isTurning = true;
                } else if (roadRunnerDriveSystem.update()) {
                    isTurning = false;
                    newGameState(GameState.DELIVER_WOBBLE);
                }
                break;

            case DELIVER_WOBBLE:
                if (trajectoryFinished && yeetSystem.placed()) {
                    yeetSystem.pickedUp(!deliveredFirstWobble);
                    newGameState(deliveredFirstWobble ? GameState.RETURN_TO_NEST : GameState.CALIBRATE_LOCATION);
                }
                break;

            case CALIBRATE_LOCATION:
                if (trajectoryFinished) {
                    deliveredFirstWobble = true;
                    if (calibrateLocation()) {
                        shootingSystem.warmUp(Target.POWER_SHOT);
//                        newGameState(GameState.POWERSHOT);
                    }
                }
                break;

            case POWERSHOT:
                if (powerShotRoutine()) {
                    shootingSystem.shutDown();
                    newGameState(GameState.PICK_UP_SECOND_WOBBLE);
                }
                break;

            case PICK_UP_SECOND_WOBBLE:
                if (trajectoryFinished && yeetSystem.pickedUp(false)) {
                    newGameState(GameState.DELIVER_WOBBLE);
                }
                break;

            case RETURN_TO_NEST:
                if (trajectoryFinished) {
                    newGameState(GameState.COMPLETE);
                }
                break;

            case COMPLETE:
                stop();
                break;
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
        }

        //TODO test code
        else if (currentGameState == GameState.CALIBRATE_LOCATION) {
            trajectory = null;
        }

        else {
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
    private boolean calibrateLocation() {
        vuforia.updateLocation();

        //TODO x value here is not very reliable as it goes further and closer to the target
        double xUpdate = Coordinates.CALIBRATION.getX() - (vuforia.getYOffset() / Constants.mmPerInch - Constants.tileWidth) + 10;
        xUpdate /= 0.9722;
        xUpdate -= 0.5;
        double yUpdate = Coordinates.CALIBRATION.getY() + vuforia.getXOffset() / Constants.mmPerInch;
        yUpdate /= 1.062;
        yUpdate += 3.4;

        if (!Double.isNaN(xUpdate) && !Double.isNaN(yUpdate)) {
            Log.d("CALIBRATION", "xUpdate == " + xUpdate);
            Log.d("CALIBRATION", "yUpdate == " + yUpdate);
            return true;
        }
        return false;
    }
}