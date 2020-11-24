package org.firstinspires.ftc.teamcode.opmodes.autonomous;

//import com.acmerobotics.roadrunner.geometry.Pose2d;
//import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
//import org.firstinspires.ftc.teamcode.components.RoadRunnerDriveSystem;
import org.firstinspires.ftc.teamcode.components.Vuforia;

import java.util.Arrays;
import java.util.List;

@Autonomous
public class VuforiaTest extends LinearOpMode {

    private static final float mmPerInch        = 25.4f;                    // constant for converting measurements from inches to millimeters
    private static final float mmTargetHeight   = (6) * mmPerInch;          // the height of the center of the target image above the floor

    private static final float halfField = 72 * mmPerInch;                  // constants for perimeter targets
    private static final float quadField  = 36 * mmPerInch;

    private OpenGLMatrix redLocation;
    private OpenGLMatrix blueLocation;

    @Override
    public void runOpMode() throws InterruptedException, NullPointerException {

        Vuforia vuforia = new Vuforia(hardwareMap, Vuforia.CameraChoice.PHONE_BACK);
        OpenGLMatrix lastLocation = new OpenGLMatrix();

        vuforia.targetsUltGoal.activate();

        while (opModeIsActive()) {

            for (VuforiaTrackable trackable : Vuforia.getTrackables()) {
                telemetry.addData(trackable.getName(), ((VuforiaTrackableDefaultListener)trackable.getListener()).isVisible() ? "Visible" : "Not Visible");    //

                OpenGLMatrix robotLocationTransform = ((VuforiaTrackableDefaultListener)trackable.getListener()).getUpdatedRobotLocation();
                if (robotLocationTransform != null) {
                    lastLocation = robotLocationTransform;
                }
            }

            if (lastLocation.getData() != null) {
                telemetry.addData("Pos", format(lastLocation));
            } else {
                telemetry.addData("Pos", "Unknown");
            }
            telemetry.addData("Offset", vuforia.getXOffset(vuforia.targetsUltGoal.get(2), lastLocation) + ", " + vuforia.getYOffset(vuforia.targetsUltGoal.get(2), lastLocation));
            telemetry.update();
        }
    }

    String format(OpenGLMatrix transformationMatrix) {
        return transformationMatrix.formatAsTransform();
    }
}