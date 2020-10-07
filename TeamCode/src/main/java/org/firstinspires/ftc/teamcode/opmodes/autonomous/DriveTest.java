package org.firstinspires.ftc.teamcode.opmodes.autonomous;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.components.RoadRunnerDriveSystem;

@Autonomous(name = "Road Runner Test", group="Autonomous")
public class DriveTest extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        RoadRunnerDriveSystem drive = new RoadRunnerDriveSystem(hardwareMap);

        Trajectory traj1 = drive.trajectoryBuilder(new Pose2d())
                .splineToConstantHeading(new Vector2d(40, 40), Math.toRadians(0))
                .build();

        waitForStart();

        if (isStopRequested()) return;

        drive.followTrajectory(traj1);
    }
}
