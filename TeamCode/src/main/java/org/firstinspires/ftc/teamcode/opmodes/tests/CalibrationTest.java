package org.firstinspires.ftc.teamcode.opmodes.tests;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.helpers.GameState;
import org.firstinspires.ftc.teamcode.components.ShootingSystem;
import org.firstinspires.ftc.teamcode.components.Tensorflow;
import org.firstinspires.ftc.teamcode.components.Trajectories;
import org.firstinspires.ftc.teamcode.opmodes.base.BaseOpMode;

@Autonomous(name = "CalibrationOpMode", group = "Autonomous")

public class CalibrationTest extends BaseOpMode {

    // Variables
    private GameState currentGameState;                         // Current GameState Machine GameState.
    public static Tensorflow.SquareState targetRegion;
    private boolean deliveredFirstWobble;

    // Systems
    private Tensorflow tensorflow;
    private ShootingSystem shootingSystem;

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
            roadRunnerDriveSystem.followTrajectoryAsync(trajectory);
        }
        switch (currentGameState) { 
            
            case INIT:
                // Initialize
                newGameState(GameState.DELIVER_WOBBLE);
                break;

            case TEST_ROADRUNNER:
                Trajectory trajectory = roadRunnerDriveSystem.trajectoryBuilder(new Pose2d())
                        .forward(12)
                        .splineTo(new Vector2d(0, 0), Math.toRadians(0)) // TODO - these numbers should be changed
                        .strafeLeft(6)
                        .strafeRight(6)
                        .build();
                roadRunnerDriveSystem.followTrajectory(trajectory);
                roadRunnerDriveSystem.turn(-90);
                roadRunnerDriveSystem.turn(90);
                if (roadRunnerDriveSystem.getPositionEstimate() == new Pose2d(0, 0)){ // TODO - Pose2D needs coordinates to compare and calculate
                    telemetry.addLine("RoadRunner: ✔");
                } else{
                    telemetry.addLine("RoadRunner: ❌");
                }
                newGameState(GameState.TEST_IMU);
                break;

            case TEST_IMU:

                deliveredFirstWobble = true;
                if (trajectoryFinished) {
                    newGameState(GameState.POWERSHOT);
                }
                break;

            case TEST_SHOOTING:

                newGameState(GameState.DRIVE_TO_SECOND_WOBBLE);
                break;

            case TEST_INTAKE:
                intakeSystem.initMotors();
                intakeSystem.suck();
                intakeSystem.stop();
                newGameState(GameState.TEST_YEET);
                break;

            case TEST_YEET:
                yeetSystem.pickup();
                yeetSystem.place();
                yeetSystem.yeet();
                newGameState(GameState.TEST_VUFORIA);
                break;

            case TEST_VUFORIA:
                newGameState(GameState.COMPLETE);
                break;

            case TERMINATE:

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