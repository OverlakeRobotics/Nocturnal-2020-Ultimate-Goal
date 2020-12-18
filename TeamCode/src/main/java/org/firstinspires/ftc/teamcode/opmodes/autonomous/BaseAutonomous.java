package org.firstinspires.ftc.teamcode.opmodes.autonomous;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.components.DriveSystem;
import org.firstinspires.ftc.teamcode.components.Tensorflow;
import org.firstinspires.ftc.teamcode.opmodes.base.BaseOpMode;

import java.util.EnumMap;

public abstract class BaseAutonomous extends BaseOpMode {
    DistanceSensor distanceCenter;
    DistanceSensor distanceOutside;
    DriveSystem.Direction centerDirection;
    DriveSystem.Direction outsideDirection;
    Tensorflow tensorflow;
    ColorSensor colorSensor;

    public void init() {
        super.init();

        //TODO configure driveSystem
        EnumMap<DriveSystem.MotorNames, DcMotor> driveMap = new EnumMap<>(DriveSystem.MotorNames.class);
        for (DriveSystem.MotorNames name : DriveSystem.MotorNames.values()) {
            driveMap.put(name,hardwareMap.get(DcMotor.class, name.toString()));
        }
        driveSystem = new DriveSystem(driveMap, hardwareMap.get(BNO055IMU.class, "imu"));

        //TODO configure distanceCenter
//        if (team == BaseStateMachine.Team.RED) {
//            distanceCenter = hardwareMap.get(DistanceSensor.class, "FRONTLEFTLIDAR");
//            distanceOutside = hardwareMap.get(DistanceSensor.class, "FRONTRIGHTLIDAR");
//            centerDirection = DriveSystem.Direction.LEFT;
//            outsideDirection = DriveSystem.Direction.RIGHT;
//        } else {
//            distanceCenter = hardwareMap.get(DistanceSensor.class, "FRONTRIGHTLIDAR");
//            distanceOutside = hardwareMap.get(DistanceSensor.class, "FRONTLEFTLIDAR");
//            centerDirection = DriveSystem.Direction.RIGHT;
//            outsideDirection = DriveSystem.Direction.LEFT;
//        }

        colorSensor = hardwareMap.get(ColorSensor.class, "COLORSENSOR");
    }
}