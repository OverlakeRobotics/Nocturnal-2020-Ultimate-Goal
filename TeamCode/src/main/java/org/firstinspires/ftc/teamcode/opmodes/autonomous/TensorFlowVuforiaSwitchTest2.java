package org.firstinspires.ftc.teamcode.opmodes.autonomous;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
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
//        int cameraId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        mTensorflow = new Tensorflow(VuforiaLocalizer.CameraDirection.BACK, hardwareMap.appContext.getResources().getIdentifier("tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName()));
        Log.d(TAG, "Attempting to activate TensorFlow");
        mTensorflow.activate();
        Log.d(TAG, "Successfully activated TensorFlow");
        newState(BaseStateMachine.State.STATE_INITIAL);
    }

    @Override
    public void init_loop() {
        mTargetRegion = mTensorflow.getTargetRegion();
        Log.d(TAG, "TargetRegion is: " + mTargetRegion);
    }

    @Override
    public void start() {
        if (mTargetRegion == null) mTargetRegion = Tensorflow.SquareState.BOX_A;
        Log.d(TAG, "Attempting to initialize Vuforia");
        mVuforia = new VuforiaSystem(mTensorflow.getLocalizer(), hardwareMap, VuforiaSystem.CameraChoice.PHONE_BACK);
        Log.d(TAG, "Initialized Vuforia");
        Log.d(TAG, "Attempting to shut down TensorFlow");
        mTensorflow.shutdown();
        Log.d(TAG, "Successfully shut down TensorFlow");
        Log.d(TAG, "TargetRegion is: " + mTargetRegion);
    }

    @Override
    public void loop() {

        Log.d(TAG, "Attempting to activate Vuforia");
        mVuforia.targetsUltGoal.activate();
        Log.d(TAG, "Activated Vuforia");
    }

    private void newState(BaseStateMachine.State newState) {
        // Restarts the state clock as well as the state
        mStateTime.reset();
        mCurrentState = newState;
    }
}