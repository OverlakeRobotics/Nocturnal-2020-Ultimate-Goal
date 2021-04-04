package org.firstinspires.ftc.teamcode.helpers;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder;

import org.firstinspires.ftc.teamcode.components.RoadRunnerDriveSystem;
import org.firstinspires.ftc.teamcode.components.Tensorflow;

import static org.firstinspires.ftc.teamcode.helpers.Constants.tileWidth;


/**
 * Trajectories.java is a file in which we use a state machine
 * for organization regarding 'pre-set' but adaptable
 * paths that the robot should take based on both its stage
 * in the overall autonomous routine, as well as its stage in the
 * specific PowerShot routine, and Tensorflow input at the
 * beginning of the match. This was created to better organize other classes.
 *
 * Refer to:
 * @components Roadrunner.java to understand methods
 * @components Tensorflow.java to understand 'TargetDropBox' (2nd Method Specifically)
 * @helpers Coordinates.java to find numerical values for pre-set positions in the trajectories.
 *
 * Used in:
 * @autonomous AutonomousOpMode
 * @base BaseOpmode
 */

public class Trajectories {

    /**
     * Gets the trajectory for a given state
     * @param currentState to draw trajectory for
     * @param posEstimate current position estimate
     * @return Trajectory for currentState
     */
    public static Trajectory getTrajectory(GameState currentState, Pose2d posEstimate) {
        //TODO Add turns to the trajectories as needed
        //TODO figure out the initial movements of the robot from start
        TrajectoryBuilder trajectoryBuilder = RoadRunnerDriveSystem.trajectoryBuilder(posEstimate);
        switch (currentState) {
            case AVOID_RINGS:
                trajectoryBuilder.lineToSplineHeading(new Pose2d(Coordinates.DETOUR_POSITION.getCoordinates().getX(), Coordinates.DETOUR_POSITION.getCoordinates().getY(), -Math.PI / 1.87));
                break;

            case CALIBRATE_LOCATION:
                trajectoryBuilder.lineToConstantHeading(Coordinates.CALIBRATION.getCoordinates());
                break;

            case PICK_UP_SECOND_WOBBLE:
                trajectoryBuilder.lineToConstantHeading(Coordinates.SECOND_WOBBLE.getCoordinates());
                break;

            case RETURN_TO_NEST:
                trajectoryBuilder.lineToConstantHeading(new Vector2d(posEstimate.getX(), Coordinates.PARKING_POSITION.getY()));
                break;
            case MOVE_TO_POWERSHOT:
                trajectoryBuilder.lineToConstantHeading(new Vector2d(Coordinates.STARTING_POSITION.getCoordinates().getX(), Coordinates.POWERSHOT_1.getCoordinates().getY()));
                break;
            case SHOOT_UPPER:
                trajectoryBuilder.lineToConstantHeading(new Vector2d(2.75 * tileWidth, tileWidth * 2.85));
                break;
            case POWERSHOT_1:
                trajectoryBuilder.lineToSplineHeading(new Pose2d(Coordinates.POWERSHOT_1.getCoordinates().getX(), Coordinates.POWERSHOT_1.getCoordinates().getY(), -Math.PI / 2.14));
                break;

            case POWERSHOT_2:
                trajectoryBuilder.lineToConstantHeading(Coordinates.POWERSHOT_2.getCoordinates());
                break;

            case POWERSHOT_3:
                trajectoryBuilder.lineToConstantHeading(Coordinates.POWERSHOT_3.getCoordinates());
                break;
//            case COMPLETE:
//                trajectoryBuilder.lineToSplineHeading(new Pose2d(Coordinates.STARTING_POSITION.getCoordinates().getX(), Coordinates.STARTING_POSITION.getCoordinates().getY(), 0));
//                break;
            default:
                return null;
        }

        return trajectoryBuilder.build();
    }

    /**
     * Returns a trajectory to the selected TargetRegion
     * @param targetRegion to drive to
     * @param posEstimate of the robot
     * @return Trajectory to the target region
     */
    public static Trajectory getTrajectory(TargetDropBox targetRegion, Pose2d posEstimate) {
        TrajectoryBuilder trajectoryBuilder = RoadRunnerDriveSystem.trajectoryBuilder(posEstimate);
        switch (targetRegion) {
            case BOX_A:
                trajectoryBuilder.lineToConstantHeading(Coordinates.BOX_A.getCoordinates());
                break;

            case BOX_B:
                trajectoryBuilder.lineToConstantHeading(Coordinates.BOX_B.getCoordinates());
                break;

            case BOX_C:
                trajectoryBuilder.lineToConstantHeading(Coordinates.BOX_C.getCoordinates());
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + targetRegion);
        }

        return trajectoryBuilder.build();
    }

    /**
     * Returns trajectory for PowerShots
     * @param shot to drive to
     * @param posEstimate of the robot
     * @return Trajectory to the target region
     */
    public static Trajectory getTrajectory(PowerShotState shot, Pose2d posEstimate) {
        TrajectoryBuilder trajectoryBuilder = RoadRunnerDriveSystem.trajectoryBuilder(posEstimate);
        switch (shot) {
            case ONE:
                trajectoryBuilder.lineToConstantHeading(Coordinates.POWERSHOT_1.getCoordinates());
                break;

            case TWO:
                trajectoryBuilder.lineToConstantHeading(Coordinates.POWERSHOT_2.getCoordinates());
                break;

            case THREE:
                trajectoryBuilder.lineToConstantHeading(Coordinates.POWERSHOT_3.getCoordinates());
                break;
        }

        return trajectoryBuilder.build();
    }
}
