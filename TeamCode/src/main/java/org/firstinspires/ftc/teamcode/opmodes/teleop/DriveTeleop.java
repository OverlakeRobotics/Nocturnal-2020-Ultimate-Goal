package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.components.IntakeSystem;
import org.firstinspires.ftc.teamcode.helpers.Constants;
import org.firstinspires.ftc.teamcode.opmodes.base.BaseOpMode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@TeleOp(name = "Real Teleop", group="TeleOp")
public class DriveTeleop extends BaseOpMode {

    // FunctionsList
    private enum Functions {
        RAISE_ARM,
        LOWER_ARM,
        SUCK,
        SHOOT,
        DRIVE
    }

    // Variables
    private boolean suckButtonDown;
    private List<Functions> calledFunctions;

    // Systems
    private IntakeSystem intakeSystem;

    @Override
    public void init() {
        super.init();
        try {
            intakeSystem = new IntakeSystem(hardwareMap.get(DcMotor.class, "IntakeSystem"));
        } catch (Exception e) {
            telemetry.addData(Constants.ROBOT_SYSTEM_ERROR, e.getStackTrace());
        }
        calledFunctions = new ArrayList<>();
        //TODO implement gamepad mechanics
    }

    @Override
    public void loop() {
        float rx = (float) Math.pow(gamepad1.right_stick_x, 3);
        float lx = (float) Math.pow(gamepad1.left_stick_x, 3);
        float ly = (float) Math.pow(gamepad1.left_stick_y, 3);
        roadRunnerDriveSystem.slowDrive(gamepad1.left_trigger > 0.3f);
        roadRunnerDriveSystem.drive(rx, lx, ly);

        for (int i = 0; i < calledFunctions.size(); i++) {
            switch (calledFunctions.get(i)) {
                case RAISE_ARM:
                    if (yeetSystem.pickedUp()) {
                        calledFunctions.remove(i);
                        i--;
                    }
                    break;

                case LOWER_ARM:
                    if (yeetSystem.placed()) {
                        calledFunctions.remove(i);
                        i--;
                    }
                    break;

                case SHOOT:
                    if (powerShotRoutine()) {
                        calledFunctions.remove(i);
                        i--;
                    }
                    break;
            }
        }

        if (gamepad1.a) {
            suckButtonDown = !suckButtonDown;
        }

        if (suckButtonDown) {
            intakeSystem.suck();
        } else {
            intakeSystem.stop();
        }

        if (gamepad1.left_bumper) {
            calledFunctions.add(Functions.RAISE_ARM);
        }

        if (gamepad1.right_bumper) {
            calledFunctions.add(Functions.LOWER_ARM);
        }

        if (gamepad1.b) {
            calledFunctions.add(Functions.SHOOT);
        }
    }
}