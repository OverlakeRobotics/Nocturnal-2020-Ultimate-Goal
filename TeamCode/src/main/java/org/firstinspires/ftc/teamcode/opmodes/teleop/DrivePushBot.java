package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.components.RoadRunnerDriveSystem;

import java.util.EnumMap;

@TeleOp(name = "Pushbot", group="TeleOp")
public class DrivePushBot extends OpMode{

    private org.firstinspires.ftc.teamcode.components.RoadRunnerDriveSystem RoadRunnerDriveSystem;
    public void init() {
//        EnumMap<RoadRunnerDriveSystem.MotorNames, DcMotor> driveMap = new EnumMap<>(RoadRunnerDriveSystem.MotorNames.class);
//        for(RoadRunnerDriveSystem.MotorNames name : RoadRunnerDriveSystem.MotorNames.values()){
//            driveMap.put(name, hardwareMap.get(DcMotor.class, name.toString()));
//            // FRONTLEFT, hardwareMap.get(DcMotor.class, name.toString())
//        }
//        RoadRunnerDriveSystem = new RoadRunnerDriveSystem(driveMap, hardwareMap.get(BNO055IMU.class, "imu"));
    }

    public void loop() {
        float rx = (float) Math.pow(gamepad1.right_stick_x, 3);
        float lx = (float) Math.pow(gamepad1.left_stick_x, 3);
        float ly = (float) Math.pow(gamepad1.left_stick_y, 3);
//        RoadRunnerDriveSystem.slowDrive(gamepad1.left_trigger > 0.3f);
//        RoadRunnerDriveSystem.drive(rx, lx, ly);
    }
}
