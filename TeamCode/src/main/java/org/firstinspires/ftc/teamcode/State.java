package org.firstinspires.ftc.teamcode;

public enum State {
    INITIAL,//Game starts!
    //Robot uses vuforia with right side camera
    DELIVER_WOBBLE, //Use roadrunner to go to specified target zone and drop off wobble goal
    DRIVE_TO_SHOOTING_LOCATION, //Robot drives forward to right behind shooting line
    POWERSHOT,
    SHOOT1, //Shoot the first power shot
    SHOOT2, //Shoot the first power shot
    SHOOT3, //Shoot the first power shot
    DRIVE_TO_SECOND_WOBBLE, //Drive to second wobble goal
    COLLECT_SECOND_WOBBLE, //Pick up second wobble goal
    RETURN_TO_NEST,//Backup and park on line using vuforia
    COMPLETE
}
