package org.firstinspires.ftc.teamcode.opmodes.base;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.internal.vuforia.VuforiaLocalizerImpl;
import org.firstinspires.ftc.teamcode.components.RoadRunnerDriveSystem;
import org.firstinspires.ftc.teamcode.components.VuforiaSystem;
import org.firstinspires.ftc.teamcode.components.VuforiaSystem.CameraChoice;

import java.util.EnumMap;

public abstract class BaseOpMode extends OpMode {

    protected RoadRunnerDriveSystem RoadRunnerDriveSystem;
    protected VuforiaSystem vuforia;
    private boolean stopRequested;

    public void init() {
        stopRequested = false;
        this.msStuckDetectInit = 20000;
        this.msStuckDetectInitLoop = 20000;
        vuforia = VuforiaSystem.getInstance();
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
