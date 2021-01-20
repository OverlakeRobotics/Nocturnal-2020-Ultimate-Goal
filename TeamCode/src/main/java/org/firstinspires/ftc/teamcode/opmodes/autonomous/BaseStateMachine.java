package org.firstinspires.ftc.teamcode.opmodes.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.teamcode.components.RoadRunnerDriveSystem;
import org.firstinspires.ftc.teamcode.components.Shooter;
import org.firstinspires.ftc.teamcode.components.Tensorflow;
import org.firstinspires.ftc.teamcode.components.VuforiaSystem;

@Autonomous(name = "BaseStateMachine", group = "")
public class BaseStateMachine extends OpMode {
    public enum State {
        STATE_INITIAL,//Game starts!
        DRIVE_FORWARD, //Robot drives forward
        //Robot uses vuforia with right side camera
        STATE_SCAN_RINGS, //Scan stack of rings
        DRIVE_TO_SHOOTING_LINE, //Robot drives forward to right behind shooting line
        STATE_SHOOT, //Shoot power shots, strafing left to get all 3
        STATE_ROADRUNNER, //Use roadrunner to go to specified target zone
        STATE_DELIVER_WOBBLE, //Drop off wobble goal
        STATE_DRIVE_TO_WOBBLE,//Turn around and drive towards second wobble goal
        STATE_COLLECT_WOBBLE,//Pick up second wobble goal
        //Turn around and drive back to target zone (STATE_ROADRUNNER)
        //Drop off second wobble goal (STATE_DELIVER_WOBBLE)
        STATE_RETURN_TO_NEST,//Backup and park on line using vuforia
        STATE_COMPLETE,
        LOGGING;
    }


    private final static String TAG = "BaseStateMachine";
    private static final float mmPerInch = 25.4f;
    private State mCurrentState;                         // Current State Machine State.
    private ElapsedTime mStateTime = new ElapsedTime();  // Time into current state
    private Tensorflow mTensorflow;
    private VuforiaSystem mVuforia;
    private Tensorflow.SquareState mTargetRegion;
    private ColorSensor colorSensor;
    private DistanceSensor distanceCenter;
//    private Shooter mShooter;
//    private RoadRunnerDriveSystem mRoadRunnerDriveSystem;
//    private IntakeSystem mIntakeSystem;

    public void init() {
        this.msStuckDetectInit = 15000;
        this.msStuckDetectInitLoop = 15000;
        mVuforia = VuforiaSystem.getInstance();
        mTensorflow = new Tensorflow(hardwareMap.appContext.getResources().getIdentifier("tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName()));
        mTensorflow.activate();
        //mRoadRunnerDriveSystem = new RoadRunnerDriveSystem(hardwareMap);

        //TODO add shooter and intakes system
        //mShooter = new Shooter(hardwareMap.get(DcMotor.class, "Shooter Motor"));
        newState(State.STATE_INITIAL);
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

        if (translation != null) {
            telemetry.addLine("null");
        } else {
            telemetry.addLine("not null");
        }
        telemetry.addData("State", mCurrentState);
        telemetry.update();

        switch (mCurrentState) { // TODO: This monstrosity.
            case LOGGING:
                telemetry.addData("DistanceFront", distanceCenter.getDistance(DistanceUnit.MM));
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
            case DRIVE_FORWARD:
                break;
            case STATE_SCAN_RINGS:
                break;
            case DRIVE_TO_SHOOTING_LINE:
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
            case STATE_ROADRUNNER:
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
            case STATE_DRIVE_TO_WOBBLE:
                break;
            case STATE_COLLECT_WOBBLE:
                break;
            case STATE_RETURN_TO_NEST:
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