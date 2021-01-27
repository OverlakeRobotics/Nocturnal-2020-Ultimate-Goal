package org.firstinspires.ftc.teamcode.components;

import com.acmerobotics.roadrunner.control.PIDCoefficients;
import com.acmerobotics.roadrunner.control.PIDFController;
import com.acmerobotics.roadrunner.drive.DriveSignal;
import com.acmerobotics.roadrunner.drive.MecanumDrive;
import com.acmerobotics.roadrunner.followers.HolonomicPIDVAFollower;
import com.acmerobotics.roadrunner.followers.TrajectoryFollower;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.profile.MotionProfile;
import com.acmerobotics.roadrunner.profile.MotionProfileGenerator;
import com.acmerobotics.roadrunner.profile.MotionState;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder;
import com.acmerobotics.roadrunner.trajectory.constraints.DriveConstraints;
import com.acmerobotics.roadrunner.trajectory.constraints.MecanumConstraints;
import com.acmerobotics.roadrunner.util.NanoClock;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;
import com.qualcomm.robotcore.util.Range;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.firstinspires.ftc.teamcode.Constants.BASE_CONSTRAINTS;
import static org.firstinspires.ftc.teamcode.Constants.MOTOR_VELO_PID;
import static org.firstinspires.ftc.teamcode.Constants.RUN_USING_ENCODER;
import static org.firstinspires.ftc.teamcode.Constants.TRACK_WIDTH;
import static org.firstinspires.ftc.teamcode.Constants.encoderTicksToInches;
import static org.firstinspires.ftc.teamcode.Constants.getMotorVelocityF;
import static org.firstinspires.ftc.teamcode.Constants.kV;
import static org.firstinspires.ftc.teamcode.Constants.kA;
import static org.firstinspires.ftc.teamcode.Constants.kStatic;

public class RoadRunnerDriveSystem extends MecanumDrive {
    public static PIDCoefficients TRANSLATIONAL_PID = new PIDCoefficients(0, 0, 0);
    public static PIDCoefficients HEADING_PID = new PIDCoefficients(0, 0, 0);

    public static double LATERAL_MULTIPLIER = 1;

    public enum Mode {
        IDLE,
        TURN,
        FOLLOW_TRAJECTORY
    }

    private NanoClock clock;

    public Mode mode;

    private PIDFController turnController;
    private MotionProfile turnProfile;
    private double turnStart;

    private static DriveConstraints constraints;
    private TrajectoryFollower follower;

    private List<Pose2d> poseHistory;

    private DcMotorEx leftFront, leftRear, rightRear, rightFront;
    private List<DcMotorEx> motors;

    private Pose2d lastPoseOnTurn;

    public static double VX_WEIGHT = 1;
    public static double VY_WEIGHT = 1;
    public static double OMEGA_WEIGHT = 1;
    private StandardTrackingWheelLocalizer standardTrackingWheelLocalizer;
    private boolean mSlowDrive;
    private boolean mPathComplete = false;
    public static final double SLOW_DRIVE_COEFF = 0.4;

    public RoadRunnerDriveSystem(HardwareMap hardwareMap) {
        super(kV, kA, kStatic, TRACK_WIDTH, TRACK_WIDTH, LATERAL_MULTIPLIER);

        clock = NanoClock.system();

        mode = Mode.IDLE;

        turnController = new PIDFController(HEADING_PID);
        turnController.setInputBounds(0, 2 * Math.PI);

        constraints = new MecanumConstraints(BASE_CONSTRAINTS, TRACK_WIDTH);
        follower = new HolonomicPIDVAFollower(TRANSLATIONAL_PID, TRANSLATIONAL_PID, HEADING_PID,
                new Pose2d(0.5, 0.5, Math.toRadians(5.0)), 0.5);

        poseHistory = new ArrayList<>();

        for (LynxModule module : hardwareMap.getAll(LynxModule.class)) {
            module.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }

        // TODO: if your hub is mounted vertically, remap the IMU axes so that the z-axis points
        // upward (normal to the floor) using a command like the following:
        // BNO055IMUUtil.remapAxes(imu, AxesOrder.XYZ, AxesSigns.NPN);

        leftFront = hardwareMap.get(DcMotorEx.class, "leftFront");
        leftRear = hardwareMap.get(DcMotorEx.class, "leftRear");
        rightRear = hardwareMap.get(DcMotorEx.class, "rightRear");
        rightFront = hardwareMap.get(DcMotorEx.class, "rightFront");

        motors = Arrays.asList(leftFront, leftRear, rightRear, rightFront);

        for (DcMotorEx motor : motors) {
            MotorConfigurationType motorConfigurationType = motor.getMotorType().clone();
            motorConfigurationType.setAchieveableMaxRPMFraction(1.0);
            motor.setMotorType(motorConfigurationType);
        }

        if (RUN_USING_ENCODER) {
            setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }

        setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        if (RUN_USING_ENCODER && MOTOR_VELO_PID != null) {
            setPIDCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, MOTOR_VELO_PID);
        }

        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftRear.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFront.setDirection(DcMotorSimple.Direction.FORWARD);
        rightRear.setDirection(DcMotorSimple.Direction.FORWARD);

        standardTrackingWheelLocalizer = new StandardTrackingWheelLocalizer(hardwareMap);

        setLocalizer(standardTrackingWheelLocalizer);
    }

    public ArrayList<Double> getEncoders() {
        return new ArrayList<Double>(
                Arrays.asList(standardTrackingWheelLocalizer.leftEncoderValue(),
                standardTrackingWheelLocalizer.rightEncoderValue(),
                standardTrackingWheelLocalizer.frontEncoderValue()));
    }

    public static TrajectoryBuilder trajectoryBuilder(Pose2d startPose) {
        return new TrajectoryBuilder(startPose, constraints);
    }

    public static TrajectoryBuilder trajectoryBuilder(Pose2d startPose, boolean reversed) {
        return new TrajectoryBuilder(startPose, reversed, constraints);
    }

    public static TrajectoryBuilder trajectoryBuilder(Pose2d startPose, double startHeading) {
        return new TrajectoryBuilder(startPose, startHeading, constraints);
    }

    public void turnAsync(double angle) {
        double heading = getPoseEstimate().getHeading();

        lastPoseOnTurn = getPoseEstimate();

        turnProfile = MotionProfileGenerator.generateSimpleMotionProfile(
                new MotionState(heading, 0, 0, 0),
                new MotionState(heading + angle, 0, 0, 0),
                constraints.maxAngVel,
                constraints.maxAngAccel,
                constraints.maxAngJerk
        );

        turnStart = clock.seconds();
        mode = Mode.TURN;
    }

    public void turn(double angle) {
        turnAsync(angle);
        waitForIdle();
    }

    public boolean followTrajectoryAsync(Trajectory trajectory) {
        if (mPathComplete) {
            mPathComplete = false;
            return true;
        }

        follower.followTrajectory(trajectory);
        mode = Mode.FOLLOW_TRAJECTORY;

        return false;
    }

    public void followTrajectory(Trajectory trajectory) {
        followTrajectoryAsync(trajectory);
        waitForIdle();
    }

    public Pose2d getLastError() {
        switch (mode) {
            case FOLLOW_TRAJECTORY:
                return follower.getLastError();
            case TURN:
                return new Pose2d(0, 0, turnController.getLastError());
            case IDLE:
                return new Pose2d();
        }
        throw new AssertionError();
    }

    public void update() {
        updatePoseEstimate();

        Pose2d currentPose = getPoseEstimate();
        Pose2d lastError = getLastError();

        poseHistory.add(currentPose);

        switch (mode) {
            case IDLE:
                // do nothing
                break;
            case TURN: {
                double t = clock.seconds() - turnStart;

                MotionState targetState = turnProfile.get(t);

                turnController.setTargetPosition(targetState.getX());

                double correction = turnController.update(currentPose.getHeading());

                double targetOmega = targetState.getV();
                double targetAlpha = targetState.getA();
                setDriveSignal(new DriveSignal(new Pose2d(
                        0, 0, targetOmega + correction
                ), new Pose2d(
                        0, 0, targetAlpha
                )));

                Pose2d newPose = lastPoseOnTurn.copy(lastPoseOnTurn.getX(), lastPoseOnTurn.getY(), targetState.getX());

                if (t >= turnProfile.duration()) {
                    mode = Mode.IDLE;
                    mPathComplete = true;
                    setDriveSignal(new DriveSignal());
                }

                break;
            }
            case FOLLOW_TRAJECTORY: {
                setDriveSignal(follower.update(currentPose));

                Trajectory trajectory = follower.getTrajectory();

                double t = follower.elapsedTime();

                if (!follower.isFollowing()) {
                    mode = Mode.IDLE;
                    mPathComplete = true;
                    setDriveSignal(new DriveSignal());
                }

                break;
            }
        }

    }

    public void waitForIdle() {
        while (!Thread.currentThread().isInterrupted() && isBusy()) {
            update();
        }
    }

    public boolean isBusy() {
        return mode != Mode.IDLE;
    }

    public void setMode(DcMotor.RunMode runMode) {
        for (DcMotorEx motor : motors) {
            motor.setMode(runMode);
        }
    }

    public void setZeroPowerBehavior(DcMotor.ZeroPowerBehavior zeroPowerBehavior) {
        for (DcMotorEx motor : motors) {
            motor.setZeroPowerBehavior(zeroPowerBehavior);
        }
    }

    public PIDCoefficients getPIDCoefficients(DcMotor.RunMode runMode) {
        PIDFCoefficients coefficients = leftFront.getPIDFCoefficients(runMode);
        return new PIDCoefficients(coefficients.p, coefficients.i, coefficients.d);
    }

    public void setPIDCoefficients(DcMotor.RunMode runMode, PIDCoefficients coefficients) {
        for (DcMotorEx motor : motors) {
            motor.setPIDFCoefficients(runMode, new PIDFCoefficients(
                    coefficients.kP, coefficients.kI, coefficients.kD, getMotorVelocityF()
            ));
        }
    }

    public void setWeightedDrivePower(Pose2d drivePower) {
        Pose2d vel = drivePower;

        if (Math.abs(drivePower.getX()) + Math.abs(drivePower.getY())
                + Math.abs(drivePower.getHeading()) > 1) {
            // re-normalize the powers according to the weights
            double denom = VX_WEIGHT * Math.abs(drivePower.getX())
                    + VY_WEIGHT * Math.abs(drivePower.getY())
                    + OMEGA_WEIGHT * Math.abs(drivePower.getHeading());

            vel = new Pose2d(
                    VX_WEIGHT * drivePower.getX(),
                    VY_WEIGHT * drivePower.getY(),
                    OMEGA_WEIGHT * drivePower.getHeading()
            ).div(denom);
        }

        setDrivePower(vel);
    }

    @Override
    public List<Double> getWheelPositions() {
        List<Double> wheelPositions = new ArrayList<>();
        for (DcMotorEx motor : motors) {
            wheelPositions.add(encoderTicksToInches(motor.getCurrentPosition()));
        }
        return wheelPositions;
    }

    public List<Double> getWheelVelocities() {
        List<Double> wheelVelocities = new ArrayList<>();
        for (DcMotorEx motor : motors) {
            wheelVelocities.add(encoderTicksToInches(motor.getVelocity()));
        }
        return wheelVelocities;
    }

    @Override
    public void setMotorPowers(double v, double v1, double v2, double v3) {
        leftFront.setPower(v);
        leftRear.setPower(v1);
        rightRear.setPower(v2);
        rightFront.setPower(v3);
    }

    @Override
    public double getRawExternalHeading() {
        // Three wheel tracking does not use imu for heading
        return 0;
    }

    /**
     * Clips joystick values and drives the motors.
     * @param rightX Right X joystick value
     * @param leftX Left X joystick value
     * @param leftY Left Y joystick value in case you couldn't tell from the others
     */
    public void drive(float rightX, float leftX, float leftY) {
        // Prevent small values from causing the robot to drift
        if (Math.abs(rightX) < 0.01) {
            rightX = 0.0f;
        }
        if (Math.abs(leftX) < 0.01) {
            leftX = 0.0f;
        }
        if (Math.abs(leftY) < 0.01) {
            leftY = 0.0f;
        }

        double frontLeftPower = -leftY + rightX + leftX;
        double frontRightPower = -leftY - rightX - leftX;
        double backLeftPower = -leftY + rightX - leftX;
        double backRightPower = -leftY - rightX + leftX;

        rightFront.setPower(getDrivePower(frontRightPower));
        leftRear.setPower(getDrivePower(backLeftPower));
        leftFront.setPower(getDrivePower(frontLeftPower));
        rightRear.setPower(getDrivePower(backRightPower));

        mSlowDrive = false;
    }

    public void slowDrive(boolean slowDrive) {
        mSlowDrive = slowDrive;
    }

    private double getDrivePower( double motorPower) {
        return Range.clip(mSlowDrive ?
                SLOW_DRIVE_COEFF * motorPower : motorPower, -1, 1);
    }
}
