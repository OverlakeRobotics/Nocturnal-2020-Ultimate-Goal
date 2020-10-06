package org.firstinspires.ftc.teamcode.opmodes.autonomous;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.components.RoadRunnerDriveSystem;

public class DriveTest extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        RoadRunnerDriveSystem drive = new RoadRunnerDriveSystem(hardwareMap);

        Trajectory trajectory = drive.trajectoryBuilder(new Pose2d())
                .forward(100)
                .build();

        Trajectory trajectory1 = drive.trajectoryBuilder(new Pose2d())
                .forward(20)
                .build();

        Trajectory trajectory2 = drive.trajectoryBuilder(new Pose2d())
                .forward(60)
                .strafeRight(350)
                .strafeLeft(260)
                .back(-250)
                .build();

        waitForStart();

        if (isStopRequested()) return;

        drive.turn(Math.toRadians(-45));
        drive.followTrajectory(trajectory);
        drive.turn(Math.toRadians(45));
        drive.followTrajectory(trajectory1);
        drive.turn(Math.toRadians(360));
        drive.turn(Math.toRadians(-360));
        drive.followTrajectory(trajectory1);
        drive.turn(Math.toRadians(40));
        drive.followTrajectory(trajectory2);
    }
}
