package org.firstinspires.ftc.teamcode.opmodes.tests;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.GameState;
import org.firstinspires.ftc.teamcode.components.Tensorflow;
import org.firstinspires.ftc.teamcode.components.Trajectories;
import org.firstinspires.ftc.teamcode.opmodes.base.BaseOpMode;

@Autonomous(name = "CallibrationOpMode", group = "Autonomous")

public class CallibrationTest extends BaseOpMode {

    // Variables
    private GameState currentGameState;                         // Current GameState Machine GameState.
    public static Tensorflow.SquareState targetRegion;
    private boolean deliveredFirstWobble;

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
        if (!trajectoryFinished && trajectory != null) {
            trajectoryFinished = roadRunnerDriveSystem.followTrajectoryAsync(trajectory);
        }
        switch (currentGameState) { 
            
            case INIT:
                // Initialize
                newGameState(GameState.DELIVER_WOBBLE);
                break;

            case TEST_ROADRUNNER:
                //TODO Search for goal? Drop off goal? (something).dropWobbleGoal() maybe pickup wobblegoal
                yeetSystem.place();
                newGameState(deliveredFirstWobble ? GameState.RETURN_TO_NEST : GameState.DRIVE_TO_SHOOTING_LOCATION);
                break;

            case TEST_IMU:
                // [TODO, NOCTURNAL] CHECK IF WE NEED THIS UNIVERSALLY OR
                //  BELOW GIVEN ASYNC CAN BE PRETTY ANNOYING
                deliveredFirstWobble = true;
                if (trajectoryFinished) {
                    newGameState(GameState.POWERSHOT);
                }
                break;

            case TEST_SHOOTING:
                powershotRoutine();
                newGameState(GameState.DRIVE_TO_SECOND_WOBBLE);
                break;

            case TEST_INTAKE:
                //TODO drive to the second wobble goal
                intakeSystem.initMotors();
                intakeSystem.suck();
                intakeSystem.stop();
                intakeSystem.getRingCount();
                newGameState(GameState.COLLECT_SECOND_WOBBLE);
                break;

            case TEST_YEET:
                //TODO position the robot and collect the second wobble goal
                yeetSystem.pickup();
                yeetSystem.place();
                yeetSystem.yeet();
                newGameState(GameState.DELIVER_WOBBLE);
                break;

            case TEST_VUFORIA:
                //TODO drive back to nest
                newGameState(GameState.COMPLETE);
                break;

            case TERMINATE:
                //TODO park the robot, shut down system, and release used resources
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

    private void newGameState(GameState newGameState) {
        currentGameState = newGameState;
        Pose2d posEstimate = roadRunnerDriveSystem.getPositionEstimate();
        trajectory = Trajectories.getTrajectory(currentGameState, posEstimate);
    }
}