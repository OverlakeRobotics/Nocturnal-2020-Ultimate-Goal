package org.firstinspires.ftc.teamcode.helpers;

/**
 *
 */
public enum GameState {
    INITIAL,//Game starts!
    //Robot uses vuforia with right side camera
    AVOID_RINGS,
    DELIVER_WOBBLE, //Use roadrunner to go to specified target zone and drop off wobble goal
    CALIBRATE_LOCATION,
    POWERSHOT,
    PICK_UP_SECOND_WOBBLE, //Drive to second wobble goal
    RETURN_TO_NEST,//Backup and park on line using vuforia
    COMPLETE,

    //TODO Test GameStates, to be deleted later on
    INIT,//Game starts!
    //Robot uses vuforia with right side camera
    TEST_ROADRUNNER, //Use roadrunner to go to specified target zone and drop off wobble goal
    TEST_IMU, //Robot drives forward to right behind shooting line
    TEST_SHOOTING,
    TEST_INTAKE, //Drive to second wobble goal
    TEST_VUFORIA, //Pick up second wobble goal
    TEST_YEET_UP,//Backup and park on line using vuforia
    TEST_YEET_DOWN,//Backup and park on line using vuforia
    TEST_TENSORFLOW,
    TEST_ENCODERS,
    TERMINATE
}
