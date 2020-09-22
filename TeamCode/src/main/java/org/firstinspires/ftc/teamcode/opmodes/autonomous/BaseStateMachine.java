package org.firstinspires.ftc.teamcode.opmodes.autonomous;

import android.util.Log;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.teamcode.components.DriveSystem;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseStateMachine extends BaseAutonomous {
    public enum State {
        STATE_INITIAL,
        STATE_DRIVE,
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
                if (driveSystem.driveToPosition(975, centerDirection, 0.7)) {
                    newState(State.STATE_COMPLETE);
                }
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