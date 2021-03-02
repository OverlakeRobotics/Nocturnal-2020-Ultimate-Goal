package org.firstinspires.ftc.teamcode.tests;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.components.Encoder;
import org.firstinspires.ftc.teamcode.components.IMUSystem;
import org.firstinspires.ftc.teamcode.components.IntakeSystem;
import org.firstinspires.ftc.teamcode.components.RoadRunnerDriveSystem;
import org.firstinspires.ftc.teamcode.components.ShootingSystem;
import org.firstinspires.ftc.teamcode.components.Tensorflow;
import org.firstinspires.ftc.teamcode.helpers.GameState;
import org.firstinspires.ftc.teamcode.opmodes.autonomous.AutonomousOpMode;

import static org.firstinspires.ftc.teamcode.helpers.Constants.encoderTicksToInches;

class JUnitTests extends AutonomousSelfCheck{

        private Pose2d oldPosEstimate;
        private IntakeSystem intake = new IntakeSystem(hardwareMap.get(DcMotor.class, "IntakeSystem"));
        private boolean first = true;
        TrajectoryBuilder trajectoryBuilder = RoadRunnerDriveSystem.trajectoryBuilder(currentPosition);
        private RoadRunnerDriveSystem roadRunnerDriveSystem;

        @Override
        public void loop() {
            double x = time;
            int i = 0;
            switch (currentGameState) { // TODO: This monstrosity.
                //TODO Do we need a trajectory as a field?
                case TEST_ENCODERS:
                    boolean bool = true;
                    for (DcMotorEx motors : roadRunnerDriveSystem.getMotors()){
                        Encoder encoder = new Encoder(motors);
                        if (encoder.getCorrectedVelocity() != 0){
                            bool = false;
                        }
                        if (encoder.getCorrectedVelocity() != 0){
                            bool = false;
                        }
                        Encoder.Direction direction = encoder.getDirection();
                        encoder.setDirection(encoder.getDirection());
                        if (direction != encoder.getDirection()){
                            bool = false;
                        }
                        // if (encoderTicksToInches == ) TODO: we need a way to check that our conversions are correct
                        if (bool = false){
                            telemetry.addLine("we have an issue with this encoder");
                        }
                        else{
                            telemetry.addLine("all good!");
                        }

                    }
                    newGameState(GameState.TEST_IMU);

                case TEST_ROADRUNNER:
                    Trajectory trajectory = roadRunnerDriveSystem.trajectoryBuilder(new Pose2d())
                            .forward(12)
                            .splineTo(new Vector2d(0, 0), Math.toRadians(0)) // TODO - these numbers should be changed
                            .strafeLeft(6)
                            .strafeRight(6)
                            .build();
                    roadRunnerDriveSystem.followTrajectory(trajectory);
                    roadRunnerDriveSystem.turn(-90);
                    roadRunnerDriveSystem.turn(90);
                    if (roadRunnerDriveSystem.getPositionEstimate() == new Pose2d(0, 0)){ // TODO - Pose2D needs coordinates to compare and calculate
                        telemetry.addLine("RoadRunner: ✔");
                    } else{
                        telemetry.addLine("RoadRunner: ❌");
                    }
                    newGameState(GameState.TEST_IMU);
                    break;
                case TEST_IMU:
                    shootingSystem.warmUp(ShootingSystem.Target.POWER_SHOT);
                    shootingSystem.shoot();
                    shootingSystem.shutDown();
                    newGameState(GameState.TEST_ROADRUNNER);
                case TEST_INTAKE:
                    intake.suck();
                    intake.stop();
                    newGameState(GameState.TEST_VUFORIA);
                case TEST_VUFORIA:
                    vuforiaData();
                    telemetry.addData("GameState", currentGameState);
                    telemetry.update();
                    newGameState(GameState.TEST_TENSORFLOW);
                case TEST_TENSORFLOW:
                    targetRegion = tensorflow.getTargetRegion();
                    if (targetRegion == Tensorflow.SquareState.BOX_C){
                        telemetry.addData("TensorFlow Target Correct (Viewing 0 Rings)", targetRegion);
                        telemetry.update();
                    }
                    else{
                        telemetry.addData("TensorFlow Target Inorrect (Viewing >0 Rings)", targetRegion);

                    }
                    newGameState(GameState.TEST_ROADRUNNER);

                case COMPLETE:
                    //TODO park the robot, shut down system, and release used resources
                    stop();
                    break;
            }
        }

        @Override
        public void stop() {
            super.stop();
            if (tensorflow != null) {
                tensorflow.shutdown();
            }
        }
    }



