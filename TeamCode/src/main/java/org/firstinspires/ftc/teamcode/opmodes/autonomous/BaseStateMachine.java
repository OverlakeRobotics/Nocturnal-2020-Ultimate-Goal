package org.firstinspires.ftc.teamcode.opmodes.autonomous;

import android.util.Log;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.teamcode.components.DriveSystem;
import org.firstinspires.ftc.teamcode.components.Shooter;
import org.firstinspires.ftc.teamcode.components.Tensorflow;
import org.firstinspires.ftc.teamcode.components.Vuforia;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseStateMachine extends BaseAutonomous {
    public enum State {
        STATE_INITIAL,
        STATE_PARK,
        STATE_DRIVE,
        STATE_DELIVER_WOBBLE,
        STATE_SHOOT,
        STATE_COLLECT,
        STATE_COMPLETE,
        LOGGING
    }

    private Tensorflow mTensorflow;
    private Vuforia mVuforia;
    private final static String TAG = "BaseStateMachine";
    private State mCurrentState;                         // Current State Machine State.
    private ElapsedTime mStateTime = new ElapsedTime();  // Time into current state
    Tensorflow.SquareState mTargetRegion;
    Shooter mShooter;
    private int mTotalRings;


    public void init(Team team) {
        super.init(team);
        this.msStuckDetectInit = 15000;
        this.msStuckDetectInitLoop = 15000;
        int cameraId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        mTensorflow = new Tensorflow(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraId);
        mTargetRegion = mTensorflow.getTargetRegion();
        mVuforia = new Vuforia(hardwareMap, Vuforia.CameraChoice.WEBCAM1);
        //mShooter = new Shooter(hardwareMap.get(DcMotor.class, "Shooter Motor"));
        newState(State.STATE_INITIAL);
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
            case STATE_DRIVE:
//                if (driveSystem.driveToPosition(975, centerDirection, 0.7)) {
//                    newState(State.STATE_COMPLETE);
//                }
                //TODO Add drive after confirmed the targets / target actions using search. Use roadrunner
                /*
                some variation of roadrunner.drive to be implemented and calibrated later. Probably with hardware help
                 */
                break;
            case STATE_PARK:
                driveSystem.setMotorPower(0);
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
            case STATE_COLLECT:

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