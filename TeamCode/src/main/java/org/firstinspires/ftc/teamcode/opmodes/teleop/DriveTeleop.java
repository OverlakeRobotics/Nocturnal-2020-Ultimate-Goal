package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.components.IntakeSystem;
import org.firstinspires.ftc.teamcode.helpers.Constants;
import org.firstinspires.ftc.teamcode.helpers.Target;
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
        RAISE_ARM_AFTER_YEET,
        LOWER_ARM,
        SUCK,
        SHOOT
    }

    // Variables
    private Set<Functions> calledFunctions;
    private boolean isSucking;

    // Systems
    private IntakeSystem intakeSystem;
    private boolean suckWasPressed = false;
    private boolean shooterWasPressed = false;
    private boolean shouldStartShooter = true;

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
        float rx = (float) Math.pow(gamepad1.right_stick_x, 1);
        float lx = -(float) Math.pow(gamepad1.left_stick_x, 1);
        float ly = -(float) Math.pow(gamepad1.left_stick_y, 1);
        roadRunnerDriveSystem.drive(rx, lx, ly);

        // Executes loaded functions
        Iterator<Functions> i = calledFunctions.iterator();
        while (i.hasNext()) {
            Functions f = i.next();
            switch (f) {
                case RAISE_ARM:
                    if (yeetSystem.pickedUp(true)) {
                        i.remove();
                    }
                    break;

                case RAISE_ARM_AFTER_YEET:
                    if (yeetSystem.pickedUp(false)) {
                        i.remove();
                    }
                    break;

                case LOWER_ARM:
                    if (yeetSystem.placed()) {
                        i.remove();
                    }
                    break;

                case SHOOT:
                    if (shootingSystem.shoot()) {
                        i.remove();
                    }
                    break;
            }
        }

        // IntakeSystem
        if (gamepad1.a) {
            if (!suckWasPressed) {
                isSucking = !isSucking;
            }
            suckWasPressed = true;
        } else {
            suckWasPressed = false;
        }

        if (gamepad1.x || gamepad1.y) {
            if (!shooterWasPressed) {
                if (shouldStartShooter) {
                    if (gamepad1.y) {
                        shootingSystem.warmUp(Target.POWER_SHOT);
                    } else {
                        shootingSystem.warmUp(Target.TOWER_GOAL);
                    }
                } else {
                    shootingSystem.shutDown();
                }
                shouldStartShooter = !shouldStartShooter;
            }
            shooterWasPressed = true;
        } else {
            shooterWasPressed = false;
        }

        if (isSucking) {
            intakeSystem.suck();
            if (gamepad1.right_trigger > 0.3) {
                intakeSystem.unsuck();
            }
        } else {
            intakeSystem.stop();
        }

        // YeetSystem
        if (gamepad1.left_bumper) {
            calledFunctions.add(Functions.RAISE_ARM);
        }

        if (gamepad1.left_trigger > 0.3f) {
            calledFunctions.add(Functions.RAISE_ARM_AFTER_YEET);
        }

        if (gamepad1.right_bumper) {
            calledFunctions.add(Functions.LOWER_ARM);
        }

        // ShootingSystem
        if (gamepad1.b) {
            isSucking = false;
            calledFunctions.add(Functions.SHOOT);
        }
    }
}