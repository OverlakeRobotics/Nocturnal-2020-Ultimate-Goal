package org.firstinspires.ftc.teamcode.tests;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.components.IntakeSystem;
import org.firstinspires.ftc.teamcode.components.RoadRunnerDriveSystem;
import org.firstinspires.ftc.teamcode.components.Tensorflow;
import org.firstinspires.ftc.teamcode.helpers.GameState;
import org.firstinspires.ftc.teamcode.helpers.Target;
import org.firstinspires.ftc.teamcode.helpers.TargetDropBox;
import org.firstinspires.ftc.teamcode.opmodes.autonomous.AutonomousOpMode;

@Autonomous(name = "AutonomousSelfCheck", group = "Autonomous")
public class AutonomousSelfCheck extends AutonomousOpMode {

    // Variables
    private GameState currentGameState;                         // Current GameState Machine GameState.
    private static TargetDropBox targetRegion;
    private boolean deliveredFirstWobble;
    private Pose2d oldPosEstimate;
    private boolean first = true;

    // Systems
    private Tensorflow tensorflow;
    private IntakeSystem intake = new IntakeSystem(hardwareMap.get(DcMotor.class, "IntakeSystem"));
    TrajectoryBuilder trajectoryBuilder = RoadRunnerDriveSystem.trajectoryBuilder(currentPosition);

    @Override
    public void init() {
        super.init();
        deliveredFirstWobble = false;
        tensorflow = new Tensorflow();
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
        double x = time;
        int i = 0;
        switch (currentGameState) { // TODO: This monstrosity.
            //TODO Do we need a trajectory as a field?
            case TEST_YEET_UP:
                if (yeetSystem.pickedUp(true)) {
                    newGameState(GameState.TEST_YEET_DOWN);
                }
            case TEST_YEET_DOWN:
                if (yeetSystem.placed()) {
                    newGameState(GameState.TEST_SHOOTING);
                }
            case TEST_SHOOTING:
                shootingSystem.warmUp(Target.POWER_SHOT);
                shootingSystem.shoot();
                shootingSystem.shutDown();
                newGameState(GameState.TEST_INTAKE);
            case TEST_INTAKE:
                intake.suck();
                intake.stop();
                newGameState(GameState.TEST_VUFORIA);
            case TEST_VUFORIA:
                vuforiaData();
                telemetry.addData("GameState", currentGameState);
                telemetry.update();
                newGameState(GameState.TEST_TENSORFLOW);
            case TEST_TENSORFLOW:
                targetRegion = tensorflow.getTargetRegion();
                telemetry.addData("TensorFlow Target", targetRegion);
                telemetry.update();
                newGameState(GameState.TEST_ROADRUNNER);
            case TEST_ROADRUNNER:
                if (first) {
                    trajectoryBuilder.strafeLeft(5);
                    if (trajectory != null) {
                        roadRunnerDriveSystem.followTrajectory(trajectory);
                    }
                    if (roadRunnerDriveSystem.update()){
                        first = false;
                    }
                }
                trajectoryBuilder = RoadRunnerDriveSystem.trajectoryBuilder(currentPosition);
                trajectoryBuilder.strafeRight(5);
                if (trajectory != null) {
                    roadRunnerDriveSystem.followTrajectory(trajectory);
                    break;
                }
                if (roadRunnerDriveSystem.update()) {
                    newGameState(GameState.COMPLETE);
                }
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
}


