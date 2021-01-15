package org.firstinspires.ftc.teamcode.opmodes.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
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
    private static final float mmPerInch = 25.4f;
    private ElapsedTime mStateTime = new ElapsedTime();  // Time into current state
    private Tensorflow mTensorflow;
    private VuforiaSystem mVuforia;
    private Tensorflow.SquareState mTargetRegion;

    @Override
    public void init() {
        this.msStuckDetectInit = 15000;
        this.msStuckDetectInitLoop = 15000;

        /*
        * hardwareMap.get(WebcamName.class, "Webcam 1")
        * For use when using the webcame in place of null for get Instance
        */
        mVuforia = VuforiaSystem.getInstance(null);
        mTensorflow = new Tensorflow(hardwareMap.appContext.getResources().getIdentifier("tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName()));
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
        VectorF translation = mVuforia.vector();
        if (translation != null) {
            telemetry.addData("Pos (in)", "{X, Y, Z} = %.1f, %.1f, %.1f",
                    translation.get(0) / mmPerInch, translation.get(1) / mmPerInch, translation.get(2) / mmPerInch);
        }
        telemetry.addData("Pos (in)", "{X, Y, Z} = %.1f, %.1f, %.1f",
                mVuforia.getXOffset() / mmPerInch, mVuforia.getYOffset() / mmPerInch, mVuforia.getZOffset() / mmPerInch);
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
