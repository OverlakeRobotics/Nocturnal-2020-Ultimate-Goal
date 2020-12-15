package org.firstinspires.ftc.teamcode.opmodes.base;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.components.DriveSystem;
import org.firstinspires.ftc.teamcode.components.VuforiaSystem;
import org.firstinspires.ftc.teamcode.components.VuforiaSystem.CameraChoice;

import java.util.EnumMap;

public abstract class BaseOpMode extends OpMode {

    protected DriveSystem driveSystem;
    protected VuforiaSystem vuforia;
    private boolean stopRequested;

    public void init(){
        stopRequested = false;
        this.msStuckDetectInit = 20000;
        this.msStuckDetectInitLoop = 20000;
        EnumMap<DriveSystem.MotorNames, DcMotor> driveMap = new EnumMap<>(DriveSystem.MotorNames.class);
        for(DriveSystem.MotorNames name : DriveSystem.MotorNames.values()){
            driveMap.put(name,hardwareMap.get(DcMotor.class, name.toString()));
        }
        driveSystem = new DriveSystem(driveMap);
    }

    protected void setCamera(CameraChoice cameraChoice){
        vuforia = new VuforiaSystem(hardwareMap, cameraChoice);
    }

    public final boolean isStopRequested() {
        return this.stopRequested || Thread.currentThread().isInterrupted();
    }

    @Override
    public void stop() {
        stopRequested = true;
        super.stop();
    }
}
