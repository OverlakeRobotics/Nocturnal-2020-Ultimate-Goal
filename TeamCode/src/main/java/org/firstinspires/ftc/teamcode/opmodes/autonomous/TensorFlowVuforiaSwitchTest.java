package org.firstinspires.ftc.teamcode.opmodes.autonomous;


import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.teamcode.components.Tensorflow;
import org.firstinspires.ftc.teamcode.components.VuforiaSystem;

import java.util.List;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.YZX;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;

@TeleOp(name = "TensorFlowVuforiaSwitch", group = "TensorFlow")
public class TensorFlowVuforiaSwitchTest extends LinearOpMode {
    public enum SquareState{
        BOX_A, BOX_B, BOX_C,
    }


    private static final float mmPerInch        = 25.4f;                    // constant for converting measurements from inches to millimeters
    private static final float mmTargetHeight   = (6) * mmPerInch;          // the height of the center of the target image above the floor

    private static final float halfField = 72 * mmPerInch;                  // constants for perimeter targets
    private static final float quadField  = 36 * mmPerInch;

    private OpenGLMatrix redLocation;
    private OpenGLMatrix blueLocation;

    private boolean x = false;

    private static final String TFOD_MODEL_ASSET = "UltimateGoal.tflite";
    private static final String LABEL_FIRST_ELEMENT = "Quad";
    private static final String LABEL_SECOND_ELEMENT = "Single";

    /*
     * IMPORTANT: You need to obtain your own license key to use Vuforia. The string below with which
     * 'parameters.vuforiaLicenseKey' is initialized is for illustration only, and will not function.
     * A Vuforia 'Development' license key, can be obtained free of charge from the Vuforia developer
     * web site at https://developer.vuforia.com/license-manager.
     *
     * Vuforia license keys are always 380 characters long, and look as if they contain mostly
     * random data. As an example, here is a example of a fragment of a valid key:
     *      ... yIgIzTqZ4mWjk9wd3cZO9T1axEqzuhxoGlfOOI2dRzKS4T0hQ8kT ...
     * Once you've obtained a license key, copy the string from the Vuforia web site
     * and paste it in to your code on the next line, between the double quotes.
     */
    private static final String VUFORIA_KEY = "Ad0Srbr/////AAABmdpa0/j2K0DPhXQjE2Hyum9QUQXZO8uAVCNpwlogfxiVmEaSuqHoTMWcV9nLlQpEnh5bwTlQG+T35Vir8IpdrSdk7TctIqH3QBuJFdHsx5hlcn74xa7AiQSJgUD/n7JJ2zJ/Er5Hc+b+r616Jf1YU6RO63Ajk5+TFB9N3a85NjMD6eDm+C6f14647ELnmGC03poSOeczbX7hZpIEObtYdVyKZ2NQ/26xDfSwwJuyMgUHwWY6nl6mk0GMnIGvu0/HoGNgyR5EkUQWyx9XlmxSrldY7BIEVkiKmracvD7W9hEGZ2nPied6DTY5RFNuFX07io6+I59/d7291NXKVMDnFAqSt4a2JYsECv+j7b25S0mD";

    private VuforiaSystem.VuforiaLocalizer vuforiaLocalizer;
    private Tensorflow tfod;

    @Override
    public void runOpMode() {
        // The TFObjectDetector uses the camera frames from the VuforiaLocalizer, so we create that
        // first.
        tfod = new Tensorflow(VuforiaLocalizer.CameraDirection.BACK, hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName()));

        vuforiaLocalizer = tfod.getLocalizer();
        //initVuforiaLocalizer();
        //initTfod();

        /**
         * Activate TensorFlow Object Detection before we wait for the start command.
         * Do it here so that the Camera Stream window will have the TensorFlow annotations visible.
         **/
        if (tfod != null) {
            tfod.activate();

            // The TensorFlow software will scale the input images from the camera to a lower resolution.
            // This can result in lower detection accuracy at longer distances (> 55cm or 22").
            // If your target is at distance greater than 50 cm (20") you can adjust the magnification value
            // to artificially zoom in to the center of image.  For best results, the "aspectRatio" argument
            // should be set to the value of the images used to create the TensorFlow Object Detection model
            // (typically 1.78 or 16/9).

            // Uncomment the following line if you want to adjust the magnification and/or the aspect ratio of the input images.
            //tfod.setZoom(2.5, 1.78);
        }

        /** Wait for the game to begin */
        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();
        waitForStart();

        Enum Result;
        ElapsedTime y = new ElapsedTime();
        if (opModeIsActive()) {
            while (opModeIsActive()) {
                Result = getTargetRegion();
                telemetry.addData ("Square State", Result.name());
                telemetry.update();
                if (y.milliseconds() > 15000){
                    switchVuforiaTask();
                    break;
                }
            }
        }


        if (x) {
            VuforiaSystem vuforia = new VuforiaSystem(vuforiaLocalizer, hardwareMap, VuforiaSystem.CameraChoice.PHONE_BACK);
            OpenGLMatrix lastLocation = new OpenGLMatrix();


            System.out.println("MESSAGE: activating vuforia");
            vuforia.targetsUltGoal.activate();
            System.out.println("MESSAGE: vuforia successfully activated");

            vuforia.targetsUltGoal.activate();

            while (opModeIsActive()) {
                int i = 0;
                for (VuforiaTrackable trackable : VuforiaSystem.getTrackables()) {
                    telemetry.addData(trackable.getName(), ((VuforiaTrackableDefaultListener) trackable.getListener()).isVisible() ? "Visible" : "Not Visible");    //
                    OpenGLMatrix robotLocationTransform = ((VuforiaTrackableDefaultListener) trackable.getListener()).getUpdatedRobotLocation();
                    if (robotLocationTransform != null) {
                        lastLocation = robotLocationTransform;
                    }
                    if (((VuforiaTrackableDefaultListener) trackable.getListener()).isVisible()) {
                        telemetry.addData("Offset", vuforia.getXOffset(vuforia.targetsUltGoal.get(i), lastLocation) + ", " + vuforia.getYOffset(vuforia.targetsUltGoal.get(i), lastLocation) + ", " + vuforia.getZOffset(vuforia.targetsUltGoal.get(i), lastLocation));
                    }
                    i++;
                }

                if (lastLocation.getData() != null) {
                    telemetry.addData("Pos", format(lastLocation));
                } else {
                    telemetry.addData("Pos", "Unknown");
                }
                telemetry.update();
            }
        }


        if (tfod != null) {
            tfod.shutdown();
        }
    }

    /**
     * Initialize the Vuforia localization engine.
     */
    private void initVuforiaLocalizer() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;

        //  Instantiate the Vuforia engine
        Log.d("Here", "initVuforia: 1");
        vuforiaLocalizer = new VuforiaSystem.VuforiaLocalizer(parameters);
        // Loading trackables is not necessary for the TensorFlow Object Detection engine.
    }


    public List<Recognition> getInference() { //get "image" back, a bunch of Recognitions - check out instance variables
        if (tfod != null) {
            return tfod.getInference();
            //return tfod.getUpdatedRecognitions(); //Returns the list of recognitions, but only if they are different than the last call to {@link #getUpdatedRecognitions()}.
        }
        return null;
    }

    public Enum getTargetRegion() {

        List<Recognition> toOperateOffOf = getInference();
        if (toOperateOffOf != null && toOperateOffOf.size() == 1) {
            for (Recognition recognitions : toOperateOffOf) {
                telemetry.addData("label", recognitions.getLabel());
                telemetry.addData("  left,top", "%.03f , %.03f",
                        recognitions.getLeft(), recognitions.getTop());
                telemetry.addData("  right,bottom", "%.03f , %.03f",
                        recognitions.getRight(), recognitions.getBottom());

                if (recognitions.getConfidence() >= 0.4) {
                    if (recognitions.getLabel().equals("Four")) {
                        telemetry.addData("Returning SquareState", Tensorflow.SquareState.BOX_A);
                        return Tensorflow.SquareState.BOX_A;
                    } else {
                        telemetry.addData("Returning SquareState", Tensorflow.SquareState.BOX_B);
                        return Tensorflow.SquareState.BOX_B;
                    }
                }
                telemetry.addData("Returning SquareState", Tensorflow.SquareState.BOX_C);
                return Tensorflow.SquareState.BOX_C;

            }
        } else {
            telemetry.addData("NULL : Returning SquareState", Tensorflow.SquareState.BOX_C);
            return Tensorflow.SquareState.BOX_C;
        }
        telemetry.addData("Oopsie", null);
        return null;
    }

    public void switchVuforiaTask() {
        System.out.println("MESSAGE: shutting down tensorflow");
        tfod.shutdown();
        System.out.println("MESSAGE: tensorflow successfully shut down");
        tfod.shutdown();
        x = true;
    }

    String format(OpenGLMatrix transformationMatrix) {
        return transformationMatrix.formatAsTransform();
    }
}
