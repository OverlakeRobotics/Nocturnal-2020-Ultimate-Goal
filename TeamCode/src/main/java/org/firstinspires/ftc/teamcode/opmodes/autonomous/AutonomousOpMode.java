package org.firstinspires.ftc.teamcode.opmodes.autonomous;

import org.firstinspires.ftc.teamcode.State;
import org.firstinspires.ftc.teamcode.components.Tensorflow;
import org.firstinspires.ftc.teamcode.components.Trajectories;
import org.firstinspires.ftc.teamcode.opmodes.base.BaseOpMode;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name = "AutonomousOpMode", group = "")
public class AutonomousOpMode extends BaseOpMode {

    private State mCurrentState;                         // Current State Machine State.
    private Tensorflow mTensorflow;
    public static Tensorflow.SquareState mTargetRegion;

    @Override
    public void init() {
        super.init();
        mTensorflow = new Tensorflow(hardwareMap.appContext.getResources().getIdentifier("tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName()));
        mTensorflow.activate();

        newState(State.INITIAL);
    }

    @Override
    public void init_loop() {
        mTargetRegion = mTensorflow.getTargetRegion();
    }

    @Override
    public void start() {
        mTensorflow.shutdown();
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
                roadRunnerDriveSystem.turn(-90);
                break;

            case DRIVE_TO_SHOOTING_LINE:
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
                newState(State.DELIVER_SECOND_WOBBLE);
                break;

            case DELIVER_SECOND_WOBBLE:
                //TODO drive to delivery location and drop off second wobble goal
                newState(State.RETURN_TO_NEST);
                break;

            case RETURN_TO_NEST:
                //TODO drive back to nest
                newState(State.COMPLETE);
                break;

            case COMPLETE:
                //TODO park the robot, shut down system, and release used resources
                break;
        }
    }

    @Override
    public void stop() {
        super.stop();
        if (mTensorflow != null) {
            mTensorflow.shutdown();
        }
    }

    private void newState(State newState) {
        mCurrentState = newState;
        trajectory = Trajectories.getTrajectory(mCurrentState);
    }
}