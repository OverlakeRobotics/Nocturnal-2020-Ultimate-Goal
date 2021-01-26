package org.firstinspires.ftc.teamcode.opmodes.autonomous;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.components.Tensorflow;
import org.firstinspires.ftc.teamcode.components.VuforiaSystem;
import org.firstinspires.ftc.teamcode.opmodes.base.BaseOpMode;

@Autonomous(name = "BaseStateMachine", group = "")
public class BaseStateMachine extends BaseOpMode {
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
    private ElapsedTime mStateTime = new ElapsedTime();  // Time into current state
    private Tensorflow mTensorflow;
    private Tensorflow.SquareState mTargetRegion;
    Trajectory trajectory;
//    private Shooter mShooter;
//    private IntakeSystem mIntakeSystem;

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
        switch (mCurrentState) { // TODO: This monstrosity.
            //TODO Do we need a trajectory as a field?
            case STATE_INITIAL:
                // Initialize
                newState(State.STATE_DELIVER_WOBBLE);
                break;

            case DRIVE_FORWARD:
                trajectory = roadRunnerDriveSystem.trajectoryBuilder(new Pose2d())
                        .forward(36)
                        .build();
                newState(State.STATE_SCAN_RINGS);
                break;

            case STATE_SCAN_RINGS:
                newState(State.DRIVE_TO_SHOOTING_LINE);
                break;

            case DRIVE_TO_SHOOTING_LINE:
                trajectory = roadRunnerDriveSystem.trajectoryBuilder(new Pose2d())
                        .forward(24)
                        .build();
                newState(State.STATE_SHOOT);
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
                TrajectoryBuilder trajectoryBuilder = roadRunnerDriveSystem.trajectoryBuilder(new Pose2d());
                switch (mTargetRegion) {
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
                        throw new IllegalStateException("Unexpected value: " + mTargetRegion);
                }
                trajectory = trajectoryBuilder.build();
                roadRunnerDriveSystem.turn(-90);
                roadRunnerDriveSystem.followTrajectory(trajectory);
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
        // Restarts the state clock as well as the state
        mStateTime.reset();
        mCurrentState = newState;
    }
}