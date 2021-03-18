package org.firstinspires.ftc.teamcode.opmodes.base;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.teamcode.components.RoadRunnerDriveSystem;
import org.firstinspires.ftc.teamcode.components.ShootingSystem;
import org.firstinspires.ftc.teamcode.components.VuforiaSystem;
import org.firstinspires.ftc.teamcode.components.YeetSystem;
import org.firstinspires.ftc.teamcode.helpers.Constants;
import org.firstinspires.ftc.teamcode.helpers.Coordinates;
import org.firstinspires.ftc.teamcode.helpers.GameState;
import org.firstinspires.ftc.teamcode.helpers.Trajectories;

public abstract class BaseOpMode extends OpMode {

    // Variables
    protected Trajectory trajectory;
    protected boolean trajectoryFinished;
    protected Pose2d currentPosition;

    // Powershot
    private enum PowerShotState {
        IDLE,
        ONE,
        TWO,
        THREE,
        FINISHED
    }
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
        }

        try {
            shootingSystem = new ShootingSystem(hardwareMap.get(DcMotorEx.class, "ShootingSystem"), hardwareMap.get(Servo.class, "ShootingSystemServo"));
        } catch (Exception e) {
            telemetry.addData(Constants.ROBOT_SYSTEM_ERROR, e.getStackTrace());
        }

        try {
            yeetSystem = new YeetSystem(hardwareMap.get(DcMotorEx.class, "YeetSystem"), hardwareMap.get(Servo.class, "LeftArmServo"), hardwareMap.get(Servo.class, "RightArmServo"));
        } catch (Exception e) {
            telemetry.addData(Constants.ROBOT_SYSTEM_ERROR, e.getStackTrace());
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
                break;

            case ONE:
                if (atShootingPosition(GameState.SHOOT1)) {
                    shootingSystem.shoot();
                    powerShotState = PowerShotState.TWO;
                }
                break;

            case TWO:
                if (atShootingPosition(GameState.SHOOT2)) {
                    shootingSystem.shoot();
                    powerShotState = PowerShotState.THREE;
                }
                break;

            case THREE:
                if (atShootingPosition(GameState.SHOOT3)) {
                    shootingSystem.shoot();
                    powerShotState = PowerShotState.FINISHED;
                }
                break;

            case FINISHED:
                return true;
        }
        return false;
    }

    /**
     * Assumes shooter is set to State Powershot
     * @param shot number to be performed
     */
    private boolean atShootingPosition(GameState shot) {
        trajectory = Trajectories.getTrajectory(shot, currentPosition);
        roadRunnerDriveSystem.followTrajectoryAsync(trajectory);
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
