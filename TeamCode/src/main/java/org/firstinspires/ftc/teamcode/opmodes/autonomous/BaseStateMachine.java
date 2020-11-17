package org.firstinspires.ftc.teamcode.opmodes.autonomous;

import android.util.Log;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.teamcode.components.DriveSystem;
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
        STATE_YEET_WOBBLE,
        STATE_SHOOT,
        STATE_SEARCH,
        STATE_COMPLETE,
        LOGGING,
    }

    private final static String TAG = "BaseStateMachine";
    private State mCurrentState;                         // Current State Machine State.
    private ElapsedTime mStateTime = new ElapsedTime();  // Time into current state

    public void init(Team team) {
        super.init(team);
        this.msStuckDetectInit = 15000;
        this.msStuckDetectInitLoop = 15000;
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
                newState(State.STATE_DRIVE);
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
                break;
            case STATE_DELIVER_WOBBLE:
                //TODO Search for goal? Drop off goal? (something).dropWobbleGoal() maybe pickup wobblegoal
                Tensorflow tensorflow = new Tensorflow(hardwareMap.get(WebcamName.class, "Webcam 1"), 0);

                switch (tensorflow.getTargetRegion()){
                    case BOX_A:
                        //move to box a
                        break;
                    case BOX_B:
                        //move to box b
                        break;
                    case BOX_C:
                        //move to box c
                        break;
                }
                break;
            case STATE_YEET_WOBBLE:
                break;
            case STATE_SHOOT:
                //TODO Shoot the ring after target.
                // Shooter.shoot()
                /*
                shooter will either receive the information to set power or this state is only called if the robot is parked in position to shoot
                 */
                break;
            case STATE_SEARCH:
                //TODO Add tensorflow / vuforia to search for targets.
                /*
                Need to identify either parking space, wobble goal dropoff space, goals, powershots, etc.
                 */
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