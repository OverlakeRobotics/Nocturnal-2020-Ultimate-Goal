package org.firstinspires.ftc.teamcode.tests;

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
//        vuforiaData();
        telemetry.addData("GameState", currentGameState);
        telemetry.update();
        switch (currentGameState) {
            case INITIAL:
                // Initialize
                elapsedTime.reset();
                shootingSystem.warmUp(Target.POWER_SHOT);
                newGameState(GameState.DELIVER_WOBBLE);
                break;

            case DELIVER_WOBBLE:
                if (elapsedTime.seconds() > 1) {
                    newGameState(GameState.CALIBRATE_LOCATION);
                }

                break;

            case CALIBRATE_LOCATION:
                if (shootingSystem.shoot()) {
                    newGameState(GameState.POWERSHOT);
                }
                break;


            case POWERSHOT:
                //TODO do the powershot routine
                if (shootingSystem.shoot()) {
                    newGameState(GameState.PICK_UP_SECOND_WOBBLE);
                }
                break;

            case PICK_UP_SECOND_WOBBLE:
                //TODO drive to the second wobble goal
                if (shootingSystem.shoot()) {
                    newGameState(GameState.COMPLETE);
                }
                break;


            case RETURN_TO_NEST:
                //TODO drive back to nest
                newGameState(GameState.COMPLETE);
                break;

            case COMPLETE:
                //TODO park the robot, shut down system, and release used resources
                stop();
                shootingSystem.shutDown();
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
    private void calibrateLocation() {
        double xUpdate = Coordinates.CALIBRATION.getX() - (vuforia.getYOffset() / Constants.mmPerInch - Constants.tileWidth);
        double yUpdate = Coordinates.CALIBRATION.getY() + vuforia.getXOffset() / Constants.mmPerInch;
        roadRunnerDriveSystem.setPoseEstimate(new Pose2d(xUpdate, yUpdate));
    }
}