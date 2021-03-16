package org.firstinspires.ftc.teamcode.helpers;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder;

import org.firstinspires.ftc.teamcode.components.RoadRunnerDriveSystem;
import org.firstinspires.ftc.teamcode.opmodes.autonomous.AutonomousOpMode;

public class Trajectories {

    /**
     * Gets the trajectory for a given state
     * @param currentState to draw trajectory for
     * @param posEstimate current position estimate
     * @return Trajectory for currentState
     */
    public static Trajectory getTrajectory(GameState currentState, Pose2d posEstimate) {
        //TODO Figure out the trajectories for each state, CODE REVIEW NEEDED
        //TODO figure out the initial movements of the robot from start
        TrajectoryBuilder trajectoryBuilder = RoadRunnerDriveSystem.trajectoryBuilder(posEstimate);
        switch (currentState) {
            case AVOID_RINGS:
                trajectoryBuilder.lineToConstantHeading(Coordinates.DETOUR_POSITION.getCoordinates());
                break;

            case DELIVER_WOBBLE:
                switch (AutonomousOpMode.targetRegion) {
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
                        throw new IllegalStateException("Unexpected value: " + AutonomousOpMode.targetRegion);
                }
                break;

            case CALIBRATE_LOCATION:
                //TODO drive to shooting line in front of the tower goal and update RoadRunner position
                trajectoryBuilder.lineToConstantHeading(Coordinates.CALIBRATION.getCoordinates());
                break;

            case DRIVE_TO_SHOOTING_LOCATION:
                //TODO get coordinate and orientation of robot to fire first powershot
                trajectoryBuilder.lineToConstantHeading(Coordinates.POWERSHOT_1.getCoordinates());
                break;

            case SHOOT2:
                //TODO roadrunner adjust to optimal shooting position
                trajectoryBuilder.lineToConstantHeading(Coordinates.POWERSHOT_2.getCoordinates());
                break;

            case SHOOT3:
                //TODO roadrunner adjust to optimal shooting position
                trajectoryBuilder.lineToConstantHeading(Coordinates.POWERSHOT_3.getCoordinates());
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
}
