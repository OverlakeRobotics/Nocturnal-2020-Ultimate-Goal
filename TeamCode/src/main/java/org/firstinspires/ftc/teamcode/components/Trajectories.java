package org.firstinspires.ftc.teamcode.components;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder;

import org.firstinspires.ftc.teamcode.opmodes.autonomous.Autonomous;


public class Trajectories {

    /**
     * Gets the trajectory for a given state
     * @param currentState to draw trajectory for
     * @return Trajectory for currentState
     */
    public static Trajectory getTrajectory(Autonomous.State currentState) {
        TrajectoryBuilder trajectoryBuilder = RoadRunnerDriveSystem.trajectoryBuilder(new Pose2d());
        //TODO Figure out the trajectories for each state
        switch (currentState) {
            case DRIVE_FORWARD:
                trajectoryBuilder.forward(36);
                break;

            case DRIVE_TO_SHOOTING_LINE:
                trajectoryBuilder.forward(24);
                break;

            case STATE_DELIVER_WOBBLE:
                //TODO Search for goal? Drop off goal? (something).dropWobbleGoal() maybe pickup wobblegoal
                switch (Autonomous.mTargetRegion) {
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
                        throw new IllegalStateException("Unexpected value: " + Autonomous.mTargetRegion);
                }
                break;

            default:
                return null;
        }
        return trajectoryBuilder.build();
    }
}
