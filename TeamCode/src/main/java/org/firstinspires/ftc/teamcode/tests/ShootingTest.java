package org.firstinspires.ftc.teamcode.tests;

import android.util.Log;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.components.Tensorflow;
import org.firstinspires.ftc.teamcode.helpers.Constants;
import org.firstinspires.ftc.teamcode.helpers.Coordinates;
import org.firstinspires.ftc.teamcode.helpers.GameState;
import org.firstinspires.ftc.teamcode.helpers.Target;
import org.firstinspires.ftc.teamcode.helpers.TargetDropBox;
import org.firstinspires.ftc.teamcode.helpers.Trajectories;
import org.firstinspires.ftc.teamcode.opmodes.base.BaseOpMode;

@Autonomous(name = "ShootingTest", group = "Autonomous")
public class ShootingTest extends BaseOpMode {

    //TODO add method that keeps looping until see target
    //TODO make sure CALIBRATE_LOCATION is moving onto the next state

    // Variables
    private GameState currentGameState;                         // Current GameState Machine GameState.
    private static TargetDropBox targetRegion;
    private boolean deliveredFirstWobble;
    private boolean isTurning;
    private ElapsedTime elapsedTime;

    // Systems
    private Tensorflow tensorflow;

    @Override
    public void init() {
        super.init();
        elapsedTime = new ElapsedTime();
        deliveredFirstWobble = false;
        isTurning = false;
        tensorflow = new Tensorflow();
        elapsedTime = new ElapsedTime();
        tensorflow.activate();
        newGameState(GameState.INITIAL);
    }

    @Override
    public void init_loop() {
        targetRegion = tensorflow.getTargetRegion();
    }

    @Override
    public void start() {
        tensorflow.shutdown();
        super.start();
    }

    @Override
    public void loop() {
//        vuforiaData();
        telemetry.addData("GameState", currentGameState);
        telemetry.update();

        switch (currentGameState) {
            case INITIAL:
                // Initialize
                newGameState(GameState.AVOID_RINGS);
                break;

            case AVOID_RINGS:
                shootingSystem.warmUp(Target.POWER_SHOT);
                elapsedTime.reset();
                newGameState(GameState.DELIVER_WOBBLE);
                break;

            case DELIVER_WOBBLE:
                if (elapsedTime.seconds() > 2) {
                    newGameState(GameState.CALIBRATE_LOCATION);
                }
                break;

            case CALIBRATE_LOCATION:
                if (shootingSystem.shoot()) {
                    newGameState(GameState.POWERSHOT);
                }
                break;

            case POWERSHOT:
                if (shootingSystem.shoot()) {
                    newGameState(GameState.PICK_UP_SECOND_WOBBLE);
                }
                break;

            case PICK_UP_SECOND_WOBBLE:
                if (shootingSystem.shoot()) {
                    newGameState(GameState.COMPLETE);
                }
                break;

            case RETURN_TO_NEST:
                if (trajectoryFinished) {
                    newGameState(GameState.COMPLETE);
                }
                break;

            case COMPLETE:
                shootingSystem.shutDown();
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

    /**
     * Updates the state of the system and updates RoadRunner trajectory
     * @param newGameState to switch to
     */
    protected void newGameState(GameState newGameState) {
        currentGameState = newGameState;
    }
}