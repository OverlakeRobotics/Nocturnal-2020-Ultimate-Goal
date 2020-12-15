package org.firstinspires.ftc.teamcode.components;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

public class ArmSystem {

    Servo rotator;
    DcMotor outwardextender;
    DcMotor lowerraise; //or should this be a Servo?

    private static final double RETRACTED = 6.8; //wrong, incorrect placeholder values that need to be updated
    private static final double EXTENDED = 13.8; //^^

    private static final double RAISED = 3.4; // ^^
    private static final double LOWERED = 2.8; // ^^

    private static final double ZERO = 0.0; // ^^^


    public ArmSystem(Servo servo, DcMotor motor1, DcMotor motor2) { //constructor
        this.rotator = servo;
        this.outwardextender = motor1;
        this.lowerraise = motor2;
    }

    private void init() {

        while (outwardextender.getCurrentPosition() != RETRACTED) {
            outwardextender.setPower(-1);
        }

        while (lowerraise.getCurrentPosition() != RAISED) {
            lowerraise.setPower(-1);
        }
        rotator.setPosition(ZERO);
    }
}




}
