package org.firstinspires.ftc.teamcode.opmodes.autonomous;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.State;
import org.firstinspires.ftc.teamcode.components.Tensorflow;
import org.firstinspires.ftc.teamcode.components.Trajectories;
import org.firstinspires.ftc.teamcode.opmodes.base.BaseOpMode;

@Autonomous(name = "AutonomousOpMode", group = "Autonomous")
public class AutonomousOpMode extends BaseOpMode {

    // Variables
    private State mCurrentState;                         // Current State Machine State.
    public static Tensorflow.SquareState mTargetRegion;

    // Systems
    private Tensorflow tensorflow;

    @Override
    public void init() {
        super.init();
        tensorflow = new Tensorflow(hardwareMap.appContext.getResources().getIdentifier("tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName()));
        tensorflow.activate();

        newState(State.INITIAL);
    }

    @Override
    public void init_loop() {
        mTargetRegion = tensorflow.getTargetRegion();
    }

    @Override
    public void start() {
        tensorflow.shutdown();
        super.start();
    }

    @Override
    public void loop() {
        vuforiaData();
        telemetry.addData("State", mCurrentState);
        telemetry.update();
        if (!trajectoryFinished && trajectory != null) {
            trajectoryFinished = roadRunnerDriveSystem.followTrajectoryAsync(trajectory);
        }

        switch (mCurrentState) { // TODO: This monstrosity.
            //TODO Do we need a trajectory as a field?
            case INITIAL:
                // Initialize
                newState(State.DELIVER_FIRST_WOBBLE);
                break;

            case DELIVER_FIRST_WOBBLE:
                //TODO Search for goal? Drop off goal? (something).dropWobbleGoal() maybe pickup wobblegoal
                yeetSystem.place();
                roadRunnerDriveSystem.turn(-90);
                newState(State.DRIVE_TO_SHOOTING_LOCATION);
                break;

            case DRIVE_TO_SHOOTING_LOCATION:
                // [TODO, NOCTURNAL] CHECK IF WE NEED THIS UNIVERSALLY OR
                //  BELOW GIVEN ASYNC CAN BE PRETTY ANNOYING
                if (trajectoryFinished) {
                    newState(State.POWERSHOT);
                }
                break;

            case POWERSHOT:
                powershotRoutine();
                newState(State.DRIVE_TO_SECOND_WOBBLE);
                break;

            case DRIVE_TO_SECOND_WOBBLE:
                //TODO drive to the second wobble goal
                newState(State.COLLECT_SECOND_WOBBLE);
                break;

            case COLLECT_SECOND_WOBBLE:
                //TODO position the robot and collect the second wobble goal
                yeetSystem.pickup();
                newState(State.DELIVER_SECOND_WOBBLE);
                break;

            case DELIVER_SECOND_WOBBLE:
                //TODO drive to delivery location and drop off second wobble goal
                yeetSystem.place();
                newState(State.RETURN_TO_NEST);
                break;

            case RETURN_TO_NEST:
                //TODO drive back to nest
                newState(State.COMPLETE);
                break;

            case COMPLETE:
                //TODO park the robot, shut down system, and release used resources
                stop();
                break;
        }
    }

    @Override
    public void stop() {
        super.stop();
        if (tensorflow != null) {
            tensorflow.shutdown();
        }
    }

    private void newState(State newState) {
        mCurrentState = newState;
        Pose2d posEstimate = roadRunnerDriveSystem.getPositionEstimate();
        trajectory = Trajectories.getTrajectory(mCurrentState, posEstimate);
    }
}