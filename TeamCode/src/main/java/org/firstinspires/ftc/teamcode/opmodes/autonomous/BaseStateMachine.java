package org.firstinspires.ftc.teamcode.opmodes.autonomous;

import android.util.Log;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.internal.vuforia.VuforiaLocalizerImpl;
import org.firstinspires.ftc.teamcode.components.DriveSystem;
import org.firstinspires.ftc.teamcode.components.RoadRunnerDriveSystem;
import org.firstinspires.ftc.teamcode.components.Shooter;
import org.firstinspires.ftc.teamcode.components.Tensorflow;
import org.firstinspires.ftc.teamcode.components.VuforiaSystem;

public class BaseStateMachine extends BaseAutonomous {
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
    private VuforiaTrackable sideWallTrackable = VuforiaSystem.getTrackables().get(3);
    private State mCurrentState;                         // Current State Machine State.
    private ElapsedTime mStateTime = new ElapsedTime();  // Time into current state
    private Tensorflow mTensorflow;
    private VuforiaSystem mVuforia;
    private Tensorflow.SquareState mTargetRegion;
    private Shooter mShooter;
    private RoadRunnerDriveSystem mRoadRunnerDriveSystem;
//    private IntakeSystem mIntakeSystem;

    public void init() {
        super.init();
        this.msStuckDetectInit = 15000;
        this.msStuckDetectInitLoop = 15000;
        mTensorflow = new Tensorflow(VuforiaSystem.getVuforiaLocalizer(hardwareMap, VuforiaSystem.CameraChoice.PHONE_BACK), hardwareMap.appContext.getResources().getIdentifier("tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName()));
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
        mVuforia = new VuforiaSystem();
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
                //TODO use vuforia to find the target
                OpenGLMatrix lastLocation = new OpenGLMatrix();
                OpenGLMatrix robotLocationTransform = ((VuforiaTrackableDefaultListener) sideWallTrackable.getListener()).getUpdatedRobotLocation();
                if (robotLocationTransform != null) {
                    lastLocation = robotLocationTransform;
                }
                float xOffset = mVuforia.getXOffset(sideWallTrackable, lastLocation);
                float yOffset = mVuforia.getYOffset(sideWallTrackable, lastLocation);
                //TODO use xOffset and yOffset to calibrate roadrunner.
                break;

            case STATE_COLLECT_RINGS:
                //TODO Use the intake system to collect the rings
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