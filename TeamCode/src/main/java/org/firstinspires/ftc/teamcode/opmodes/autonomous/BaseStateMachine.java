package org.firstinspires.ftc.teamcode.opmodes.autonomous;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.components.DriveSystem;
import org.firstinspires.ftc.teamcode.components.RoadRunnerDriveSystem;
import org.firstinspires.ftc.teamcode.components.Shooter;
import org.firstinspires.ftc.teamcode.components.Tensorflow;
import org.firstinspires.ftc.teamcode.components.VuforiaSystem;

public abstract class BaseStateMachine extends BaseAutonomous {
    public enum State {
        STATE_INITIAL,
        STATE_GRAB,
        STATE_DRIVE_TO_TARGET,
        STATE_DELIVER_WOBBLE,
        IDENTIFY_TARGETS,
        STATE_SHOOT,
        STATE_COLLECT_RINGS,
        STATE_COMPLETE,
        LOGGING
    }

    private final static String TAG = "BaseStateMachine";
    private State mCurrentState;                         // Current State Machine State.
    private ElapsedTime mStateTime = new ElapsedTime();  // Time into current state
    private Tensorflow mTensorflow;
    private VuforiaSystem mVuforia;
    private Tensorflow.SquareState mTargetRegion;
    private Shooter mShooter;
    private RoadRunnerDriveSystem mRoadRunnerDriveSystem;
//    private IntakeSystem mIntakeSystem;

    public void init(Team team) {
        super.init(team);
        this.msStuckDetectInit = 15000;
        this.msStuckDetectInitLoop = 15000;
        int cameraId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        mTensorflow = new Tensorflow(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraId);
        mTensorflow.activate();
        mRoadRunnerDriveSystem = new RoadRunnerDriveSystem(hardwareMap);

        //TODO add shooter and intakes system
        //mShooter = new Shooter(hardwareMap.get(DcMotor.class, "Shooter Motor"));
        newState(State.STATE_INITIAL);
    }

    @Override
    public void init_loop() {
        mTargetRegion = mTensorflow.getTargetRegion();
    }

    @Override
    public void start() {
        if (mTargetRegion == null) mTargetRegion = Tensorflow.SquareState.BOX_A;
        mVuforia = new VuforiaSystem(mTensorflow.getLocalizer(), hardwareMap, VuforiaSystem.CameraChoice.PHONE_BACK);
        mTensorflow.shutdown();
    }

    @Override
    public void loop() {
        telemetry.addData("State", mCurrentState);
        telemetry.update();
        switch (mCurrentState) {
            case LOGGING:
                // telemetry.addData("DistanceFront", distanceCenter.getDistance(DistanceUnit.MM));
                telemetry.addData("Color Blue", colorSensor.blue());
                telemetry.addData("Color Red", colorSensor.red());
                telemetry.addData("Color Green", colorSensor.green());
                telemetry.addData("Color Alpha", colorSensor.alpha());
                telemetry.addData("Color Hue", colorSensor.argb());
                telemetry.update();
                break;
            case STATE_INITIAL:
                // Initialize
                // Drive 0.5m (1 tile) to the left
                newState(State.STATE_DELIVER_WOBBLE);
                break;
            case STATE_DRIVE_TO_TARGET:
//                if (driveSystem.driveToPosition(975, centerDirection, 0.7)) {
//                    newState(State.STATE_COMPLETE);
//                }
                //TODO Add drive after confirmed the targets / target actions using search. Use roadrunner
                /*
                some variation of roadrunner.drive to be implemented and calibrated later. Probably with hardware help
                 */
                break;
            case STATE_GRAB:

                break;
            case STATE_DELIVER_WOBBLE:
                //TODO Search for goal? Drop off goal? (something).dropWobbleGoal() maybe pickup wobblegoal

                switch (mTargetRegion) {
                    case BOX_A:
                        //driveSystem.driveToPosition()
                    case BOX_B:
                        //driveSystem.driveToPosition()
                    case BOX_C:
                        //driveSystem.driveToPosition()
                }
                break;
            case IDENTIFY_TARGETS:

                break;
            case STATE_COLLECT_RINGS:

                break;
            case STATE_SHOOT:
                mVuforia.
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
                break;

            case STATE_COMPLETE:
                break;
        }
    }

    private void newState(State newState) {
        // Restarts the state clock as well as the state
        mStateTime.reset();
        mCurrentState = newState;
    }

}