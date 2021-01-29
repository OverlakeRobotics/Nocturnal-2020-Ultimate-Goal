package org.firstinspires.ftc.teamcode.opmodes.autonomous;

import com.acmerobotics.roadrunner.trajectory.Trajectory;

import org.firstinspires.ftc.teamcode.components.Tensorflow;
import org.firstinspires.ftc.teamcode.components.Trajectories;
import org.firstinspires.ftc.teamcode.opmodes.base.BaseOpMode;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name = "Autonomous", group = "")
public class Autonomous extends BaseOpMode {
    public enum State {
        STATE_INITIAL,//Game starts!
        DRIVE_FORWARD, //Robot drives forward
        //Robot uses vuforia with right side camera
        STATE_SCAN_RINGS, //Scan stack of rings
        DRIVE_TO_SHOOTING_LINE, //Robot drives forward to right behind shooting line
        STATE_SHOOT, //Shoot power shots, strafing left to get all 3
        STATE_DELIVER_WOBBLE, //Use roadrunner to go to specified target zone and drop off wobble goal
        STATE_DRIVE_TO_WOBBLE,//Turn around and drive towards second wobble goal
        STATE_COLLECT_WOBBLE,//Pick up second wobble goal
        //Turn around and drive back to target zone (STATE_ROADRUNNER)
        //Drop off second wobble goal (STATE_DELIVER_WOBBLE)
        STATE_RETURN_TO_NEST,//Backup and park on line using vuforia
        STATE_COMPLETE
    }

    private State mCurrentState;                         // Current State Machine State.
    private Tensorflow mTensorflow;
    public static Tensorflow.SquareState mTargetRegion;

    @Override
    public void init() {
        super.init();
        mTensorflow = new Tensorflow(hardwareMap.appContext.getResources().getIdentifier("tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName()));
        mTensorflow.activate();

        //TODO add shooter and intakes system
        //mShooter = new Shooter(hardwareMap.get(DcMotor.class, "Shooter Motor"));
        newState(State.STATE_INITIAL);
    }

    @Override
    public void init_loop() {
        mTargetRegion = mTensorflow.getTargetRegion();
        telemetry.addData("MESSAGE:", "TargetRegion is: " + mTargetRegion);
    }

    @Override
    public void start() {
        if (mTargetRegion == null) mTargetRegion = Tensorflow.SquareState.BOX_A;
        mTensorflow.shutdown();
        super.start();
    }

    @Override
    public void loop() {
        vuforiaData();
        telemetry.addData("State", mCurrentState);
        telemetry.update();
        if (trajectory != null) {
            trajectoryFinished = roadRunnerDriveSystem.followTrajectoryAsync(trajectory);
        }

        switch (mCurrentState) { // TODO: This monstrosity.
            //TODO Do we need a trajectory as a field?
            case STATE_INITIAL:
                // Initialize
                newState(State.STATE_DELIVER_WOBBLE);
                break;

            case DRIVE_FORWARD:
                if (trajectoryFinished) {
                    newState(State.STATE_SCAN_RINGS);
                }
                break;

            case STATE_SCAN_RINGS:
                newState(State.DRIVE_TO_SHOOTING_LINE);
                break;

            case DRIVE_TO_SHOOTING_LINE:
                if (trajectoryFinished) {
                    newState(State.STATE_SHOOT);
                }
                break;

            case STATE_SHOOT:
                //**Basic Version, stop at white line**
                //DriveSystem.stop()
                //mShooter.setMotorPower(**Whichever target it's going for**);
                //mShooter.shoot();
                //for (int i = 1; i < mTotalRings; i++){
                //  if (sticks) {
                //      DriveSystem.strafe(**Proper number to do it**)
                //  }
                //  mShooter.shoot();
                //}
                //mShooter.stop();
                //mTotalRings = 0;

                //**Advanced Version, from anywhere**
                //DriveSystem.stop()
                //mShooter.setMotorPower(**Whichever target it's going for**);
                //mShooter.shoot();
                //for (int i = 1; i < totalRings; i++) {
                //  if (sticks) {
                //      DriveSystem.strafe(**Proper number to do it**)
                //  }
                //  mShooter.shoot();
                //}
                //mShooter.stop();
                //mTotalRings = 0;
                newState(State.STATE_DELIVER_WOBBLE);
                break;

            case STATE_DELIVER_WOBBLE:
                //TODO Search for goal? Drop off goal? (something).dropWobbleGoal() maybe pickup wobblegoal
                roadRunnerDriveSystem.turn(-90);
                break;

            case STATE_DRIVE_TO_WOBBLE:
                break;

            case STATE_COLLECT_WOBBLE:
                newState(State.STATE_RETURN_TO_NEST);
                break;

            case STATE_RETURN_TO_NEST:
                newState(State.STATE_COMPLETE);
                break;

            case STATE_COMPLETE:
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