package org.firstinspires.ftc.teamcode.opmodes.autonomous;

import android.widget.Switch;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.components.Coordinates;
import org.firstinspires.ftc.teamcode.GameState;
import org.firstinspires.ftc.teamcode.components.ShootingSystem;
import org.firstinspires.ftc.teamcode.components.Tensorflow;
import org.firstinspires.ftc.teamcode.components.Trajectories;
import org.firstinspires.ftc.teamcode.opmodes.base.BaseOpMode;

import static org.firstinspires.ftc.teamcode.Constants.fieldBoxWidth;
import static org.firstinspires.ftc.teamcode.Constants.powerShotY;

@Autonomous(name = "AutonomousOpMode", group = "Autonomous")
public class AutonomousSelfCheck extends AutonomousOpMode {

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
                shootingSystem.init(ShootingSystem.Target.POWER_SHOT);
                powershotRoutine();
                newGameState(GameState.TEST_INTAKE);
            case TEST_INTAKE:
                intakeSystem.suck();
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
                Pose2d posEstimate = roadRunnerDriveSystem.getPositionEstimate();
                Vector2d negativeFirstPowerShotCoordinates = new Vector2d(-4 * fieldBoxWidth - (23.5f * 2 + 4.25f - ((44 * 10) / Constants.mmPerInch / 2)), powerShotY);
                trajectory = Trajectories.getTrajectory(currentGameState, posEstimate);
                if (trajectory != null) {
                    roadRunnerDriveSystem.followTrajectoryAsync(trajectory);
                }
                break;
        }
        stop();
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
}


