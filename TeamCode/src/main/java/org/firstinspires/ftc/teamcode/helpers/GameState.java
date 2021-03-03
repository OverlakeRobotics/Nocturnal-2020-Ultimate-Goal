package org.firstinspires.ftc.teamcode.helpers;

public enum GameState {
    INITIAL,//Game starts!
    //Robot uses vuforia with right side camera
    DELIVER_WOBBLE, //Use roadrunner to go to specified target zone and drop off wobble goal
    CALIBRATE_LOCATION,
    DRIVE_TO_SHOOTING_LOCATION, //Robot drives forward to right behind shooting line
    POWERSHOT,
    SHOOT1, //Shoot the first power shot
    SHOOT2, //Shoot the first power shot
    SHOOT3, //Shoot the first power shot
    DRIVE_TO_SECOND_WOBBLE, //Drive to second wobble goal
    COLLECT_SECOND_WOBBLE, //Pick up second wobble goal
    RETURN_TO_NEST,//Backup and park on line using vuforia
    COMPLETE,
    //FOR TESTING PURPOSES (CALLIBRATION)
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
