package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.components.IntakeSystem;
import org.firstinspires.ftc.teamcode.components.YeetSystem;
import org.firstinspires.ftc.teamcode.helpers.Constants;
import org.firstinspires.ftc.teamcode.opmodes.base.BaseOpMode;

@TeleOp(name = "Real Teleop", group="TeleOp")
public class DriveTeleop extends BaseOpMode {

    // Variables
    private boolean suckButtonDown;

    // Systems
    private IntakeSystem intakeSystem;
    private YeetSystem yeetSystem;
    private boolean up;
    private boolean down;

    @Override
    public void init() {
        super.init();
        try {
            intakeSystem = new IntakeSystem(hardwareMap.get(DcMotor.class, "IntakeSystem"));
            yeetSystem = new YeetSystem(hardwareMap.get(DcMotorEx.class, "YeetSystem"),hardwareMap.get(Servo.class, "YeetSystemLeft"), hardwareMap.get(Servo.class, "YeetSystemRight"));
        } catch (Exception e) {
            telemetry.addData(Constants.ROBOT_SYSTEM_ERROR, e.getStackTrace());
        }

        //TODO implement gamepad mechanics
    }

    @Override
    public void loop() {
        float rx = (float) Math.pow(gamepad1.right_stick_x, 3);
        float lx = (float) Math.pow(gamepad1.left_stick_x, 3);
        float ly = (float) Math.pow(gamepad1.left_stick_y, 3);
        roadRunnerDriveSystem.slowDrive(gamepad1.left_trigger > 0.3f);
        roadRunnerDriveSystem.drive(rx, lx, ly);
        if (suckButtonDown) {
            intakeSystem.suck();
        }
        else {
            intakeSystem.stop();
        }

        if (gamepad1.a){
            if (yeetSystem.isOpen()){
                yeetSystem.grab();
            }
            else if (yeetSystem.isGrabbed()){
                yeetSystem.release();
            }
            else{

            }
        }

        if (gamepad1.dpad_up && !up) {
            yeetSystem.motor.setTargetPosition(yeetSystem.motor.getCurrentPosition() + 1);
            up = true;
        } else if (!gamepad1.right_bumper) {
            up = false;
        }

        if (gamepad1.dpad_down && !down) {
            yeetSystem.motor.setTargetPosition(yeetSystem.motor.getCurrentPosition() - 1);
            down = true;
        } else if (!gamepad1.left_bumper) {
            down = false;
        }


    }
}