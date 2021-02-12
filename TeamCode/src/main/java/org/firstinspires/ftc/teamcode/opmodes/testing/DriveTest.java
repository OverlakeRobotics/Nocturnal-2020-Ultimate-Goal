package org.firstinspires.ftc.teamcode.opmodes.testing;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.components.RoadRunnerDriveSystem;

@Autonomous(group = "drive")
public class DriveTest extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        RoadRunnerDriveSystem drive = new RoadRunnerDriveSystem(hardwareMap);

        Trajectory trajectory = RoadRunnerDriveSystem.trajectoryBuilder(new Pose2d())
                .lineToConstantHeading(new Vector2d(20, 20))
                .build();

        waitForStart();

        if (isStopRequested()) return;

        drive.turn(Math.toRadians(180));
        drive.followTrajectory(trajectory);
    }
}
