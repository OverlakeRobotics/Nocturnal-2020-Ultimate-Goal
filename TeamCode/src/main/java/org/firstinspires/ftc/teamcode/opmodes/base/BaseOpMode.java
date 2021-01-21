package org.firstinspires.ftc.teamcode.opmodes.base;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.internal.vuforia.VuforiaLocalizerImpl;
import org.firstinspires.ftc.teamcode.components.DriveSystem;
import org.firstinspires.ftc.teamcode.components.RoadRunnerDriveSystem;
import org.firstinspires.ftc.teamcode.components.VuforiaSystem;
import org.firstinspires.ftc.teamcode.components.VuforiaSystem.CameraChoice;

import java.util.EnumMap;

public abstract class BaseOpMode extends OpMode {

    protected RoadRunnerDriveSystem driveSystem;
    private static final float mmPerInch = 25.4f;
    protected RoadRunnerDriveSystem RoadRunnerDriveSystem;
    protected VuforiaSystem vuforia;

    @Override
    public void init() {
        stopRequested = false;
        this.msStuckDetectInit = 20000;
        this.msStuckDetectInitLoop = 20000;

        driveSystem = new RoadRunnerDriveSystem(hardwareMap);
        this.msStuckDetectInit = 15000;
        this.msStuckDetectInitLoop = 15000;
        vuforia = VuforiaSystem.getInstance();

        //TODO initialize RoadRunnerDriveSystem once hardware online
    }

    @Override
    public void start() {
        vuforia.activate();
    }

    public void vuforiaData() {
        VectorF translation = vuforia.vector();

        // only one of these two will be used
        if (translation != null) {
            telemetry.addData("Pos (in)", "{X, Y, Z} = %.1f, %.1f, %.1f",
                    translation.get(0) / mmPerInch, translation.get(1) / mmPerInch, translation.get(2) / mmPerInch);
        }
        telemetry.addData("Pos (in)", "{X, Y, Z} = %.1f, %.1f, %.1f",
                vuforia.getXOffset() / mmPerInch, vuforia.getYOffset() / mmPerInch, vuforia.getZOffset() / mmPerInch);


        if (translation != null) {
            telemetry.addLine("null");
        } else {
            telemetry.addLine("not null");
        }

    }

    @Override
    public void stop() {
        if (vuforia != null) {
            vuforia.disable();
        }
    }
}
