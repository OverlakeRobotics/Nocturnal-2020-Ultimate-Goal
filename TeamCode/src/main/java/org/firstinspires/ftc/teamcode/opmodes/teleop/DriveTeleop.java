package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.components.IntakeSystem;
import org.firstinspires.ftc.teamcode.helpers.Constants;
import org.firstinspires.ftc.teamcode.opmodes.base.BaseOpMode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
    private Set<Functions> calledFunctions;

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
        calledFunctions = new HashSet<>();
    }

    @Override
    public void loop() {
        //TODO implement gamepad mechanics
        // Drive
        float rx = (float) Math.pow(gamepad1.right_stick_x, 3);
        float lx = (float) Math.pow(gamepad1.left_stick_x, 3);
        float ly = (float) Math.pow(gamepad1.left_stick_y, 3);
        roadRunnerDriveSystem.slowDrive(gamepad1.left_trigger > 0.3f);
        roadRunnerDriveSystem.drive(rx, lx, ly);

        // Executes loaded functions
        Iterator<Functions> i = calledFunctions.iterator();
        while (i.hasNext()) {
            Functions f = i.next();
            switch (f) {
                case RAISE_ARM:
                    if (yeetSystem.pickedUp()) {
                        i.remove();
                    }
                    break;

                case LOWER_ARM:
                    if (yeetSystem.placed()) {
                        i.remove();
                    }
                    break;

                case SHOOT:
                    if (powerShotRoutine()) {
                        i.remove();
                    }
                    break;
            }
        }

        // IntakeSystem
        if (gamepad1.a) {
            intakeSystem.suck();
        } else {
            intakeSystem.stop();
        }

        // YeetSystem
        if (gamepad1.left_bumper) {
            calledFunctions.add(Functions.RAISE_ARM);
        }

        if (gamepad1.right_bumper) {
            calledFunctions.add(Functions.LOWER_ARM);
        }

        // ShootingSystem
        if (gamepad1.b) {
            calledFunctions.add(Functions.SHOOT);
        }
    }
}