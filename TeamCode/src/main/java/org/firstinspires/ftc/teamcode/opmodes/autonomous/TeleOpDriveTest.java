package org.firstinspires.ftc.teamcode.opmodes.autonomous;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.components.RoadRunnerDriveSystem;
import org.firstinspires.ftc.teamcode.opmodes.base.BaseOpMode;

@Autonomous
public class TeleOpDriveTest extends BaseOpMode {

    private boolean homing = false;
    private Trajectory home = roadRunnerDriveSystem.trajectoryBuilder(new Pose2d()).splineTo(new Vector2d(0, 0), Math.toRadians(0)).build();

    public void loop(){
        roadRunnerDriveSystem.update();
        float rx = (float) Math.pow(gamepad1.right_stick_x, 3);
        float lx = (float) Math.pow(gamepad1.left_stick_x, 3);
        float ly = (float) Math.pow(gamepad1.left_stick_y, 3);
        roadRunnerDriveSystem.slowDrive(gamepad1.left_trigger > 0.3f);
        roadRunnerDriveSystem.drive(rx, lx, ly);

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
