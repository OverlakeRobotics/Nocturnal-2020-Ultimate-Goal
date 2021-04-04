package org.firstinspires.ftc.teamcode.tests;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder;

import org.firstinspires.ftc.teamcode.components.RoadRunnerDriveSystem;
import org.firstinspires.ftc.teamcode.helpers.Coordinates;
import org.firstinspires.ftc.teamcode.helpers.GameState;
import org.firstinspires.ftc.teamcode.helpers.PowerShotState;

public class TestTrajectories {

    /**
     * Gets the trajectory for a given state
     * @param currentState to draw trajectory for
     * @param posEstimate current position estimate
     * @return Trajectory for currentState
     */
    public static Trajectory getTrajectory(GameState currentState, Pose2d posEstimate) {
        TrajectoryBuilder trajectoryBuilder = RoadRunnerDriveSystem.trajectoryBuilder(posEstimate);
        switch (currentState) {
            case DELIVER_WOBBLE:
                trajectoryBuilder.lineTo(new Vector2d(60, 5));
                break;

            case CALIBRATE_LOCATION:
                //TODO drive to shooting line in front of the tower goal and update RoadRunner position
                trajectoryBuilder.lineTo(new Vector2d(60, -5));
                break;

            case DRIVE_TO_SECOND_WOBBLE:
                trajectoryBuilder.lineToConstantHeading(Coordinates.SECOND_WOBBLE.getCoordinates());
                break;

            case RETURN_TO_NEST:
                trajectoryBuilder.lineToConstantHeading(Coordinates.PARKING_POSITION.getCoordinates());
                break;
            default:
                return null;
        }

        return trajectoryBuilder.build();
    }

    /**
     * Test for shots
     * @param shot to test
     * @param posEstimate of robot
     * @return Trajectory to perform
     */
    public static Trajectory getTrajectory(PowerShotState shot, Pose2d posEstimate) {
        TrajectoryBuilder trajectoryBuilder = RoadRunnerDriveSystem.trajectoryBuilder(posEstimate);
        switch (shot) {
            case ONE:
                //TODO get coordinate and orientation of robot to fire first powershot
                trajectoryBuilder.lineToConstantHeading(Coordinates.POWERSHOT_1.getCoordinates());
                break;

            case TWO:
                //TODO roadrunner adjust to optimal shooting position
                trajectoryBuilder.lineToConstantHeading(Coordinates.POWERSHOT_2.getCoordinates());
                break;

            case THREE:
                //TODO roadrunner adjust to optimal shooting position
                trajectoryBuilder.lineToConstantHeading(Coordinates.POWERSHOT_3.getCoordinates());
                break;
        }

        return trajectoryBuilder.build();
    }
}
