package org.firstinspires.ftc.teamcode.components;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder;
import org.firstinspires.ftc.teamcode.State;

import org.firstinspires.ftc.teamcode.opmodes.autonomous.AutonomousOpMode;

import static org.firstinspires.ftc.teamcode.Constants.firstPowerShotDisplacement;
import static org.firstinspires.ftc.teamcode.Constants.firstPowerShotX;
import static org.firstinspires.ftc.teamcode.Constants.secondPowerShotDisplacement;


public class Trajectories {

    /**
     * Gets the trajectory for a given state
     * @param currentState to draw trajectory for
     * @return Trajectory for currentState
     */
    public static Trajectory getTrajectory(State currentState, Pose2d posEstimate) {
        TrajectoryBuilder trajectoryBuilder = RoadRunnerDriveSystem.trajectoryBuilder(new Pose2d());
        //TODO Figure out the trajectories for each state
        switch (currentState) {
            case DELIVER_FIRST_WOBBLE:
                //TODO Determine the trajectories to each dropoff box
                switch (AutonomousOpMode.mTargetRegion) {
                    case BOX_A:
                        trajectoryBuilder.forward(48);
                        break;

                    case BOX_B:
                        trajectoryBuilder.strafeLeft(24).forward(24);
                        break;

                    case BOX_C:
                        trajectoryBuilder.strafeLeft(48).forward(48);
                        break;

                    default:
                        throw new IllegalStateException("Unexpected value: " + AutonomousOpMode.mTargetRegion);
                }
                break;

            case DRIVE_TO_SHOOTING_LOCATION:
                //TODO get coordinate and orientation of robot to fire first powershot
                trajectoryBuilder.strafeTo(new Vector2d(92, 78)); // idk what i'm doing here - @anishch
                break;

            case SHOOT1:
                //TODO roadrunner adjust to optimal shooting position
                trajectoryBuilder.strafeLeft(firstPowerShotX);
                break;

            case SHOOT2:
                //TODO roadrunner adjust to optimal shooting position
                trajectoryBuilder.strafeLeft(firstPowerShotDisplacement);
                break;

            case SHOOT3:
                //TODO roadrunner adjust to optimal shooting position
                trajectoryBuilder.strafeLeft(secondPowerShotDisplacement);
                break;

            case DRIVE_TO_SECOND_WOBBLE:
                //TODO Determine the trajectories to each dropoff box
                switch (AutonomousOpMode.mTargetRegion) {
                    case BOX_A:

                        break;

                    case BOX_B:

                        break;

                    case BOX_C:

                        break;

                    default:
                        throw new IllegalStateException("Unexpected value: " + AutonomousOpMode.mTargetRegion);
                }
                break;

            case COLLECT_SECOND_WOBBLE:
                break;

            case DELIVER_SECOND_WOBBLE:
                break;

            case RETURN_TO_NEST:
                break;

            default:
                return null;
        }
        return trajectoryBuilder.build();
    }
}
