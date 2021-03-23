package org.firstinspires.ftc.teamcode.opmodes.base;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.teamcode.components.RoadRunnerDriveSystem;
import org.firstinspires.ftc.teamcode.components.ShootingSystem;
import org.firstinspires.ftc.teamcode.components.VuforiaSystem;
import org.firstinspires.ftc.teamcode.components.YeetSystem;
import org.firstinspires.ftc.teamcode.helpers.Constants;
import org.firstinspires.ftc.teamcode.helpers.Coordinates;
import org.firstinspires.ftc.teamcode.helpers.PowerShotState;
import org.firstinspires.ftc.teamcode.helpers.Trajectories;

public abstract class BaseOpMode extends OpMode {

    // Variables
    protected Trajectory trajectory;
    protected boolean trajectoryFinished;
    protected Pose2d currentPosition;

    // Powershot
    private PowerShotState powerShotState;

    // Systems
    protected RoadRunnerDriveSystem roadRunnerDriveSystem;
    protected VuforiaSystem vuforia;
    protected ShootingSystem shootingSystem;
    protected YeetSystem yeetSystem;

    @Override
    public void init() {
        this.msStuckDetectInit = 20000;
        this.msStuckDetectInitLoop = 20000;

        currentPosition = new Pose2d(Coordinates.STARTING_POSITION.getX(), Coordinates.STARTING_POSITION.getY(), Math.PI);
        vuforia = VuforiaSystem.getInstance();
        powerShotState = PowerShotState.IDLE;

        //TODO initialize RoadRunnerDriveSystem, ShootingSystem, and IntakeSystem once hardware online
        try {
            roadRunnerDriveSystem = new RoadRunnerDriveSystem(hardwareMap);
            roadRunnerDriveSystem.setPoseEstimate(currentPosition);
        } catch (Exception e) {
            telemetry.addData(Constants.ROBOT_SYSTEM_ERROR, e.getStackTrace());
            telemetry.addData("Road Runner", e.getStackTrace());
        }

        try {
            shootingSystem = new ShootingSystem(hardwareMap.get(DcMotorEx.class, "ShootingSystem"), hardwareMap.get(Servo.class, "ShootingSystemServo"));
        } catch (Exception e) {
            telemetry.addData(Constants.ROBOT_SYSTEM_ERROR, e.getStackTrace());
            telemetry.addData("Shooting System", e.getStackTrace());
        }

        try {
            yeetSystem = new YeetSystem(hardwareMap.get(DcMotorEx.class, "YeetSystem"), hardwareMap.get(Servo.class, "LeftArmServo"), hardwareMap.get(Servo.class, "RightArmServo"));
        } catch (Exception e) {
            telemetry.addData(Constants.ROBOT_SYSTEM_ERROR, e.getStackTrace());
            telemetry.addData("Yeet System", e.getStackTrace());
        }
    }

    @Override
    public void start() {
        vuforia.activate();
    }

    /**
     * Initializes Vuforia data
     */
    protected void vuforiaData() {
        VectorF translation = vuforia.vector();

        // only one of these two will be used
        if (translation != null) {
            telemetry.addData("Pos (in)", "{X, Y, Z} = %.1f, %.1f, %.1f",
                    translation.get(0) / Constants.mmPerInch, translation.get(1) / Constants.mmPerInch, translation.get(2) / Constants.mmPerInch);
        }
        telemetry.addData("Pos (in)", "{X, Y, Z} = %.1f, %.1f, %.1f",
                vuforia.getXOffset() / Constants.mmPerInch, vuforia.getYOffset() / Constants.mmPerInch, vuforia.getZOffset() / Constants.mmPerInch);

        if (translation != null) {
            telemetry.addLine("null");
        } else {
            telemetry.addLine("not null");
        }
    }

    /**
     * Powershot routine
     * @return if all 3 powershots have been fired
     */
    protected boolean powerShotRoutine() {
        switch (powerShotState) {
            case IDLE:
                powerShotState = PowerShotState.ONE;
                updateShotTrajectory();
                break;

            case ONE:
                updateStatus(PowerShotState.TWO);
                break;

            case TWO:
                updateStatus(PowerShotState.THREE);
                break;

            case THREE:
                updateStatus(PowerShotState.FINISHED);
                break;

            case FINISHED:
                return true;
        }
        return false;
    }

    /**
     * Checks if the robot is at shooting position. If it is, shoots and updates the shot number and the shooting state.
     * @param nextState if an update of status is needed
     */
    private void updateStatus(PowerShotState nextState) {
        if (atShootingPosition()) {
            shootingSystem.shoot();
            powerShotState = nextState;
            updateShotTrajectory();
        }
    }

    /**
     * Updates the shot trajectory based on the current shot state
     */
    private void updateShotTrajectory() {
        trajectory = Trajectories.getTrajectory(powerShotState, currentPosition);
        roadRunnerDriveSystem.followTrajectoryAsync(trajectory);
    }

    /**
     * Assumes shooter is set to State PowerShot
     * @return if the robot has finished its current trajectory
     */
    private boolean atShootingPosition() {
        trajectoryFinished = roadRunnerDriveSystem.update();
        return trajectoryFinished;
    }

    @Override
    public void stop() {
        if (vuforia != null) {
            vuforia.disable();
        }

        if (shootingSystem != null) {
            shootingSystem.shutDown();
        }

        if (yeetSystem != null) {
            yeetSystem.shutDown();
        }
    }
}
