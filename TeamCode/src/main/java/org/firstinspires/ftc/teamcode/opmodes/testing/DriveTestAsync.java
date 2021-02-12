package org.firstinspires.ftc.teamcode.opmodes.testing;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.components.RoadRunnerDriveSystem;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name = "DriveTestAsync", group = "")
public class DriveTestAsync extends OpMode {
    RoadRunnerDriveSystem roadRunnerDriveSystem;
    Trajectory trajectory;

    @Override
    public void init() {
        roadRunnerDriveSystem = new RoadRunnerDriveSystem(hardwareMap);
        trajectory = RoadRunnerDriveSystem.trajectoryBuilder(new Pose2d())
                .lineToConstantHeading(new Vector2d(20, 20))
                .build();
        roadRunnerDriveSystem.followTrajectoryAsync(trajectory);
    }

    @Override
    public void loop() {
        roadRunnerDriveSystem.update();
    }
}
