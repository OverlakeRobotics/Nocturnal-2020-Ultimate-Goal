package org.firstinspires.ftc.teamcode.opmodes.autonomous;

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

        Trajectory trajectory = drive.trajectoryBuilder(new Pose2d())
                .forward(30)
                .build();

        Trajectory trajectory1 = drive.trajectoryBuilder(trajectory.end())
                .strafeLeft(30)
                .build();

        Trajectory trajectory2 = drive.trajectoryBuilder(trajectory1.end())
                .back(15)
                .build();

        Trajectory trajectory3 = drive.trajectoryBuilder(trajectory2.end())
                .splineTo(new Vector2d(30, -30), Math.toRadians(90))
                .build();

        Trajectory trajectory4 = drive.trajectoryBuilder(trajectory3.end())
                .splineTo(new Vector2d(0, 0), Math.toRadians(0))
                .build();

        waitForStart();

        if (isStopRequested()) return;

        drive.followTrajectory(trajectory);
        drive.followTrajectory(trajectory1);
        drive.followTrajectory(trajectory2);
        drive.followTrajectory(trajectory3);
        drive.followTrajectory(trajectory4);
    }
}
