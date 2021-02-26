package org.firstinspires.ftc.teamcode.tests;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.components.IntakeSystem;
import org.firstinspires.ftc.teamcode.components.RoadRunnerDriveSystem;
import org.firstinspires.ftc.teamcode.components.ShootingSystem;
import org.firstinspires.ftc.teamcode.components.Trajectories;
import org.firstinspires.ftc.teamcode.helpers.Constants;
import org.firstinspires.ftc.teamcode.helpers.GameState;
import org.firstinspires.ftc.teamcode.opmodes.autonomous.AutonomousOpMode;

@Autonomous(name = "AutonomousOpMode", group = "Autonomous")
public class AutonomousSelfCheck extends AutonomousOpMode {

    private Pose2d oldPosEstimate;
    private IntakeSystem intake = new IntakeSystem(hardwareMap.get(DcMotor.class, "IntakeSystem"));

    @Override
    public void loop() {
        double x = time;
        int i = 0;
        switch (currentGameState) { // TODO: This monstrosity.
            //TODO Do we need a trajectory as a field?
            case TEST_YEET:
                yeetSystem.pickup();
                yeetSystem.yeet();
                yeetSystem.place();
                newGameState(GameState.TEST_SHOOTING);
            case TEST_SHOOTING:
                shootingSystem.warmUp(ShootingSystem.Target.POWER_SHOT);
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
                TrajectoryBuilder trajectoryBuilder = RoadRunnerDriveSystem.trajectoryBuilder(currentPosition);
                trajectoryBuilder.strafeLeft(5);
                if (trajectory != null) {
                    roadRunnerDriveSystem.followTrajectory(trajectory);
                    break;
                }
                trajectoryBuilder = RoadRunnerDriveSystem.trajectoryBuilder(currentPosition);
                trajectoryBuilder.strafeRight(5);
                if (trajectory != null) {
                    roadRunnerDriveSystem.followTrajectory(trajectory);
                    break;
                }
                newGameState(GameState.COMPLETE);
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


