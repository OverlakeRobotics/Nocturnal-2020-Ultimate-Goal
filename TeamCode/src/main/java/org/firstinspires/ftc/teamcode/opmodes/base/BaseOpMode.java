package org.firstinspires.ftc.teamcode.opmodes.base;

import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.teamcode.State;
import org.firstinspires.ftc.teamcode.components.IntakeSystem;
import org.firstinspires.ftc.teamcode.components.RoadRunnerDriveSystem;
import org.firstinspires.ftc.teamcode.components.ShootingSystem;
import org.firstinspires.ftc.teamcode.components.Trajectories;
import org.firstinspires.ftc.teamcode.components.VuforiaSystem;

public abstract class BaseOpMode extends OpMode {

    protected RoadRunnerDriveSystem roadRunnerDriveSystem;
    private static final float mmPerInch = 25.4f;
    protected VuforiaSystem vuforia;
    protected Trajectory trajectory;
    protected boolean trajectoryFinished;
    protected ShootingSystem shootingSystem;
    protected IntakeSystem intakeSystem;

    @Override
    public void init() {
        this.msStuckDetectInit = 20000;
        this.msStuckDetectInitLoop = 20000;

        roadRunnerDriveSystem = new RoadRunnerDriveSystem(hardwareMap);
        vuforia = VuforiaSystem.getInstance();
        //TODO initialize RoadRunnerDriveSystem, ShootingSystem, and IntakeSystem once hardware online
        shootingSystem = new ShootingSystem(hardwareMap.get(DcMotor.class, "ShootingSystem"));
//        intakeSystem = new IntakeSystem(hardwareMap.get(DcMotor.class, "ShootingSystem"));
    }

    @Override
    public void start() {
        vuforia.activate();
    }

    /**
     * Initializes Vuforia data
     */
    public void vuforiaData() {
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
    public void powershotRoutine() {
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
        trajectory = Trajectories.getTrajectory(shot);
        trajectoryFinished = false;
        while (!trajectoryFinished) trajectoryFinished = roadRunnerDriveSystem.followTrajectoryAsync(trajectory);
        shootingSystem.shoot();
    }

    @Override
    public void stop() {
        if (vuforia != null) {
            vuforia.disable();
        }
    }
}
