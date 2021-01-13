package org.firstinspires.ftc.teamcode.opmodes.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.teamcode.components.Tensorflow;
import org.firstinspires.ftc.teamcode.components.VuforiaSystem;

@Autonomous(name = "TensorFlowVuforiaSwitchTest2", group = "")
public class TensorFlowVuforiaSwitchTest2 extends OpMode {
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

    private final static String TAG = "MESSAGE";
    private BaseStateMachine.State mCurrentState;                         // Current State Machine State.
    private ElapsedTime mStateTime = new ElapsedTime();  // Time into current state
    private Tensorflow mTensorflow;
    private VuforiaSystem mVuforia;
    private Tensorflow.SquareState mTargetRegion;

    @Override
    public void init() {
        this.msStuckDetectInit = 15000;
        this.msStuckDetectInitLoop = 15000;

        mVuforia = new VuforiaSystem(hardwareMap, VuforiaSystem.CameraChoice.PHONE_BACK);
        mTensorflow = new Tensorflow(mVuforia.getVuforiaLocalizer(), hardwareMap.appContext.getResources().getIdentifier("tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName()));
        mTensorflow.activate();
        newState(BaseStateMachine.State.STATE_INITIAL);
    }

    @Override
    public void init_loop() {
        mTargetRegion = mTensorflow.getTargetRegion();
        telemetry.addData(TAG, "TargetRegion is: " + mTargetRegion);
    }

    @Override
    public void start() {
        if (mTargetRegion == null) mTargetRegion = Tensorflow.SquareState.BOX_A;
        mTensorflow.shutdown();
        mVuforia.activate();
    }

    @Override
    public void loop() {

        telemetry.addData(TAG, mVuforia.isAnyTargetVisible() ? "There are targets visible." : "There aren't targets visible");
        OpenGLMatrix robotLocationTransform = ((VuforiaTrackableDefaultListener)mVuforia.trackable.getListener()).getUpdatedRobotLocation();
        if (robotLocationTransform != null) {
            mVuforia.setLastLocation(robotLocationTransform);
        }
        telemetry.addData(TAG,
                "X:" + mVuforia.getXOffset(mVuforia.trackable) +
                "Y:" + mVuforia.getYOffset(mVuforia.trackable) +
                "Z:" + mVuforia.getZOffset(mVuforia.trackable));
    }

    @Override
    public void stop() {
        if (mVuforia != null) {
            mVuforia.disable();
        }
    }

    private void newState(BaseStateMachine.State newState) {
        // Restarts the state clock as well as the state
        mStateTime.reset();
        mCurrentState = newState;
    }
}
