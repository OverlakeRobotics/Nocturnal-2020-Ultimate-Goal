package org.firstinspires.ftc.teamcode.opmodes.base;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.teamcode.State;
import org.firstinspires.ftc.teamcode.components.IntakeSystem;
import org.firstinspires.ftc.teamcode.components.RoadRunnerDriveSystem;
import org.firstinspires.ftc.teamcode.components.ShootingSystem;
import org.firstinspires.ftc.teamcode.components.Trajectories;
import org.firstinspires.ftc.teamcode.components.VuforiaSystem;
import org.firstinspires.ftc.teamcode.components.YeetSystem;

public abstract class BaseOpMode extends OpMode {

    // Variables
    private static final float mmPerInch = 25.4f;
    protected boolean trajectoryFinished;
    protected Pose2d currentPosition;

    // Systems
    protected RoadRunnerDriveSystem roadRunnerDriveSystem;
    protected VuforiaSystem vuforia;
    protected Trajectory trajectory;
    protected ShootingSystem shootingSystem;
    protected IntakeSystem intakeSystem;
    protected YeetSystem yeetSystem;

    @Override
    public void init() {
        this.msStuckDetectInit = 20000;
        this.msStuckDetectInitLoop = 20000;

        //TODO fill in starting position and robot initial heading
        currentPosition = new Pose2d(new Vector2d(), );

        vuforia = VuforiaSystem.getInstance();

        //TODO initialize RoadRunnerDriveSystem, ShootingSystem, and IntakeSystem once hardware online
//        roadRunnerDriveSystem = new RoadRunnerDriveSystem(hardwareMap);
//        shootingSystem = new ShootingSystem(hardwareMap.get(DcMotor.class, "ShootingSystem"));
//        intakeSystem = new IntakeSystem(hardwareMap.get(DcMotor.class, "ShootingSystem"));
//        yeetSystem = new YeetSystem(hardwareMap.get(DcMotor.class, "YeetSystem"));
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
                    translation.get(0) / mmPerInch, translation.get(1) / mmPerInch, translation.get(2) / mmPerInch);
        }
        telemetry.addData("Pos (in)", "{X, Y, Z} = %.1f, %.1f, %.1f",
                vuforia.getXOffset() / mmPerInch, vuforia.getYOffset() / mmPerInch, vuforia.getZOffset() / mmPerInch);

        if (translation != null) {
            telemetry.addLine("null");
        } else {
            telemetry.addLine("not null");
        }
    }

    /**
     * Powershot routine
     */
    protected void powershotRoutine() {
        shootingSystem.setTarget(ShootingSystem.Target.POWER_SHOT);

        // Shoot 1
        singlePowershot(State.SHOOT1);

        // Shoot 2
        singlePowershot(State.SHOOT2);

        // Shoot 3
        singlePowershot(State.SHOOT3);
    }

    /**
     * Assumes shooter is set to State Powershot
     * @param shot number to be performed
     */
    private void singlePowershot(State shot) {
        trajectory = Trajectories.getTrajectory(shot, currentPosition);
        trajectoryFinished = false;
        while (!trajectoryFinished) trajectoryFinished = roadRunnerDriveSystem.followTrajectoryAsync(trajectory);
        shootingSystem.shoot();
    }

    @Override
    public void stop() {
        if (vuforia != null) {
            vuforia.disable();
        }

        if (shootingSystem != null) {
            shootingSystem.stop();
        }

        if (intakeSystem != null) {
            intakeSystem.stop();
        }
    }
}
