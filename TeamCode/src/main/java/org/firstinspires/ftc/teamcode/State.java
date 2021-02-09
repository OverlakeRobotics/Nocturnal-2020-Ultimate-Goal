package org.firstinspires.ftc.teamcode;

public enum State {
    INITIAL,//Game starts!
    //Robot uses vuforia with right side camera
    DELIVER_FIRST_WOBBLE, //Use roadrunner to go to specified target zone and drop off wobble goal
    DRIVE_TO_SHOOTING_LINE, //Robot drives forward to right behind shooting line
    POWERSHOT,
    SHOOT1, //Shoot the first power shot
    SHOOT2, //Shoot the first power shot
    SHOOT3, //Shoot the first power shot
    DRIVE_TO_SECOND_WOBBLE,//Turn around and drive towards second wobble goal
    COLLECT_SECOND_WOBBLE,//Pick up second wobble goal
    //Turn around and drive back to target zone (STATE_ROADRUNNER)
    DELIVER_SECOND_WOBBLE,
    //Drop off second wobble goal
    RETURN_TO_NEST,//Backup and park on line using vuforia
    COMPLETE
}
