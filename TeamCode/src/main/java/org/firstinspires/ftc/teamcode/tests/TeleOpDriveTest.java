package org.firstinspires.ftc.teamcode.tests;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.components.RoadRunnerDriveSystem;
import org.firstinspires.ftc.teamcode.opmodes.base.BaseOpMode;

import java.util.ArrayList;

@TeleOp
public class TeleOpDriveTest extends BaseOpMode {

    private boolean homing = false;
    private Trajectory home = RoadRunnerDriveSystem.trajectoryBuilder(new Pose2d()).splineTo(new Vector2d(0, 0), Math.toRadians(0)).build();

    public void loop(){
        roadRunnerDriveSystem.update();
        float rx = (float) Math.pow(gamepad1.right_stick_x, 3);
        float lx = (float) Math.pow(gamepad1.left_stick_x, 3);
        float ly = (float) Math.pow(gamepad1.left_stick_y, 3);
        roadRunnerDriveSystem.slowDrive(gamepad1.left_trigger > 0.3f);
        roadRunnerDriveSystem.drive(rx, lx, ly);

        ArrayList<Double> encoderValues = roadRunnerDriveSystem.getEncoders();
        telemetry.addData("Left Encoder", encoderValues.get(0));
        telemetry.addData("Right Encoder", encoderValues.get(1));
        telemetry.addData("Front Encoder", encoderValues.get(2));

        if (gamepad1.a && !homing) {
            homing = true;
        }

        if (homing) {
            roadRunnerDriveSystem.followTrajectoryAsync(home);
        }

        if (roadRunnerDriveSystem.mode == RoadRunnerDriveSystem.Mode.IDLE) {
            homing = false;
        }
    }
}
