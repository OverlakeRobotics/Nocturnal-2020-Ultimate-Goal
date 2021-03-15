package org.firstinspires.ftc.teamcode.tests;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.teamcode.components.RoadRunnerDriveSystem;
import org.firstinspires.ftc.teamcode.components.ShootingSystem;
import org.firstinspires.ftc.teamcode.components.Tensorflow;
import org.firstinspires.ftc.teamcode.components.VuforiaSystem;
import org.firstinspires.ftc.teamcode.components.YeetSystem;
import org.firstinspires.ftc.teamcode.helpers.Constants;
import org.firstinspires.ftc.teamcode.helpers.Coordinates;
import org.firstinspires.ftc.teamcode.helpers.GameState;
import org.firstinspires.ftc.teamcode.helpers.Target;

@Autonomous(name = "AutonomousDriveTest", group = "Autonomous")
public class AutonomousDriveTest extends OpMode {

    // Variables
    protected GameState currentGameState;

    // Variables
    protected Pose2d currentPosition;

    // Systems
//    protected RoadRunnerDriveSystem roadRunnerDriveSystem;
//    protected YeetSystem yeetSystem;
    ShootingSystem shootingSystem;
    protected ElapsedTime elapsedTime;

    @Override
    public void init() {
        this.msStuckDetectInit = 20000;
        this.msStuckDetectInitLoop = 20000;
        elapsedTime = new ElapsedTime();

        currentPosition = new Pose2d(Coordinates.STARTING_POSITION.getX(), Coordinates.STARTING_POSITION.getY(), Math.PI);

        //Initialize RoadRunner
        try {
//            roadRunnerDriveSystem = new RoadRunnerDriveSystem(hardwareMap);
//            roadRunnerDriveSystem.setPoseEstimate(currentPosition);
        } catch (Exception e) {
            telemetry.addData(Constants.ROBOT_SYSTEM_ERROR, e.getStackTrace());
        }

        shootingSystem = new ShootingSystem(hardwareMap.get(DcMotor.class, "ShootingSystem"), hardwareMap.get(Servo.class, "ShootingSystemServo"));

//        yeetSystem = new YeetSystem(hardwareMap.get(DcMotor.class, "YeetSystem"), hardwareMap.get(Servo.class, "LeftArmServo"), hardwareMap.get(Servo.class, "RightArmServo"));
        newGameState(GameState.INITIAL);
    }

    @Override
    public void init_loop() {

    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void loop() {
        telemetry.addData("GameState", currentGameState);


        // Makes sure the trajectory is finished before doing anything else
//        boolean trajectoryFinished = roadRunnerDriveSystem.update();
//        Pose2d poseEstimate = roadRunnerDriveSystem.getPoseEstimate();
//        telemetry.addData("x", poseEstimate.getX());
//        telemetry.addData("y", poseEstimate.getY());
//        telemetry.addData("heading", poseEstimate.getHeading());
        telemetry.update();

        switch (currentGameState) {
            case INITIAL:
                // Initialize
                newGameState(GameState.DELIVER_WOBBLE);
                elapsedTime.reset();
                break;

            case DELIVER_WOBBLE:
                shootingSystem.warmUp(Target.POWER_SHOT);
                if (elapsedTime.seconds() > 2) {
                    elapsedTime.reset();
                    newGameState(GameState.CALIBRATE_LOCATION);
                }

                break;

            case CALIBRATE_LOCATION:
                shootingSystem.warmUp(Target.POWER_SHOT);
                if (shootingSystem.shoot()) {
                    newGameState(GameState.COMPLETE);
                }
                break;

            case DRIVE_TO_SHOOTING_LOCATION:

                break;

            case POWERSHOT:
                //TODO do the powershot routine
                newGameState(GameState.DRIVE_TO_SECOND_WOBBLE);
                break;

            case DRIVE_TO_SECOND_WOBBLE:
                //TODO drive to the second wobble goal
                newGameState(GameState.COLLECT_SECOND_WOBBLE);
                break;

            case COLLECT_SECOND_WOBBLE:
                //TODO position the robot and collect the second wobble goal
                newGameState(GameState.DELIVER_WOBBLE);
                break;

            case RETURN_TO_NEST:
                //TODO drive back to nest
                newGameState(GameState.COMPLETE);
                break;

            case COMPLETE:
                //TODO park the robot, shut down system, and release used resources
                stop();
                break;
        }
    }

    @Override
    public void stop() {

    }

    /**
     * Updates the state of the system and updates RoadRunner trajectory
     * @param newGameState to switch to
     */
    protected void newGameState(GameState newGameState) {
        currentGameState = newGameState;
    }
}