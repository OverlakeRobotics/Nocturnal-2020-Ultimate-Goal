package org.firstinspires.ftc.teamcode.opmodes.autonomous;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.teamcode.GameState;
import org.firstinspires.ftc.teamcode.components.Tensorflow;
import org.firstinspires.ftc.teamcode.components.Trajectories;
import org.firstinspires.ftc.teamcode.opmodes.base.BaseOpMode;

@Autonomous(name = "AutonomousOpMode", group = "Autonomous")
public class AutonomousOpMode extends BaseOpMode {

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
        trajectoryFinished = roadRunnerDriveSystem.update();

        switch (currentGameState) { // TODO: This monstrosity.
            //TODO Do we need a trajectory as a field?
            case INITIAL:
                // Initialize
                newGameState(GameState.DELIVER_WOBBLE);
                break;

            case DELIVER_WOBBLE:
                //TODO Search for goal? Drop off goal? (something).dropWobbleGoal() maybe pickup wobblegoal
                yeetSystem.place();
                newGameState(deliveredFirstWobble ? GameState.RETURN_TO_NEST : GameState.CALIBRATE_LOCATION);
                break;

            case CALIBRATE_LOCATION:
                //TODO calibrate location of robot using Vuforia and updates RoadRunner if Vuforia is more accurate
                if (trajectoryFinished) {
                    deliveredFirstWobble = true;
                    calibrateLocation();
                    newGameState(GameState.DRIVE_TO_SHOOTING_LOCATION);
                }
                break;

            case DRIVE_TO_SHOOTING_LOCATION:
                // [TODO, NOCTURNAL] CHECK IF WE NEED THIS UNIVERSALLY OR
                //  BELOW GIVEN ASYNC CAN BE PRETTY ANNOYING
                if (trajectoryFinished) newGameState(GameState.POWERSHOT);
                break;

            case POWERSHOT:
                powershotRoutine();
                newGameState(GameState.DRIVE_TO_SECOND_WOBBLE);
                break;

            case DRIVE_TO_SECOND_WOBBLE:
                //TODO drive to the second wobble goal
                newGameState(GameState.COLLECT_SECOND_WOBBLE);
                break;

            case COLLECT_SECOND_WOBBLE:
                //TODO position the robot and collect the second wobble goal
                yeetSystem.pickup();
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
        super.stop();
        if (tensorflow != null) {
            tensorflow.shutdown();
        }
    }

    private void newGameState(GameState newGameState) {
        currentGameState = newGameState;
        Pose2d posEstimate = roadRunnerDriveSystem.getPositionEstimate();
        trajectory = Trajectories.getTrajectory(currentGameState, posEstimate);
        if (trajectory != null) {
            roadRunnerDriveSystem.followTrajectoryAsync(trajectory);
        }
    }

    private void calibrateLocation() {
        Pose2d roadRunnerPos = roadRunnerDriveSystem.getPoseEstimate();
        Pose2d updatedPos = new Pose2d(roadRunnerPos.getX() + vuforia.getXOffset(), roadRunnerPos.getY() + vuforia.getYOffset());
        roadRunnerDriveSystem.setPoseEstimate(updatedPos);
    }
}