package org.firstinspires.ftc.teamcode.opmodes.autonomous;

//import com.acmerobotics.roadrunner.geometry.Pose2d;
//import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
//import org.firstinspires.ftc.teamcode.components.RoadRunnerDriveSystem;
import org.firstinspires.ftc.teamcode.components.VuforiaSystem;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;

@Autonomous
public class VuforiaTest extends LinearOpMode {

    private static final float mmPerInch        = 25.4f;                    // constant for converting measurements from inches to millimeters
    private static final float mmTargetHeight   = (6) * mmPerInch;          // the height of the center of the target image above the floor

    private static final float halfField = 72 * mmPerInch;                  // constants for perimeter targets
    private static final float quadField  = 36 * mmPerInch;

    @Override
    public void runOpMode() throws InterruptedException, NullPointerException {

        VuforiaSystem vuforia = new VuforiaSystem(hardwareMap, VuforiaSystem.CameraChoice.PHONE_BACK);
        OpenGLMatrix lastLocation = new OpenGLMatrix();

        vuforia.targetsUltGoal.activate();

        while (opModeIsActive()) {

            for (VuforiaTrackable trackable : VuforiaSystem.getTrackables()) {
                telemetry.addData(trackable.getName(), ((VuforiaTrackableDefaultListener)trackable.getListener()).isVisible() ? "Visible" : "Not Visible");    //

                OpenGLMatrix robotLocationTransform = ((VuforiaTrackableDefaultListener)trackable.getListener()).getUpdatedRobotLocation();
                if (robotLocationTransform != null) {
                    lastLocation = robotLocationTransform;
                }
            }

            if (lastLocation.getData() != null) {
                VectorF translation = lastLocation.getTranslation();
                telemetry.addData("Pos (in)", "{X, Y, Z} = %.1f, %.1f, %.1f",
                        translation.get(0) / mmPerInch, translation.get(1) / mmPerInch, translation.get(2) / mmPerInch);
                Orientation rotation = Orientation.getOrientation(lastLocation, EXTRINSIC, XYZ, DEGREES);
                telemetry.addData("Rot (deg)", "{Roll, Pitch, Heading} = %.0f, %.0f, %.0f", rotation.firstAngle, rotation.secondAngle, rotation.thirdAngle);
            } else {
                telemetry.addData("Pos (in)", "Unknown");
                telemetry.addData("Rot (deg)", "Unknown");
            }
            telemetry.addData("Offset", vuforia.getXOffset(vuforia.targetsUltGoal.get(2), lastLocation) + ", " + vuforia.getYOffset(vuforia.targetsUltGoal.get(2), lastLocation));
            telemetry.update();
        }
    }

    String format(OpenGLMatrix transformationMatrix) {
        return transformationMatrix.formatAsTransform();
    }
}