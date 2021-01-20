package org.firstinspires.ftc.teamcode.components;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.teamcode.BuildConfig;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.YZX;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;

/** Teddy, trust my code, you blasphemer. */
public class VuforiaSystem {

    public enum CameraChoice {
        PHONE_BACK, WEBCAM1
    }

    private static final float mmPerInch = 25.4f;                    // constant for converting measurements from inches to millimeters
    private static final float mmTargetHeight = (6) * mmPerInch;          // the height of the center of the target image above the floor

    private static final float halfField = 72 * mmPerInch;                  // constants for perimeter targets
    private static final float quadField = 36 * mmPerInch;

    private VuforiaLocalizer vuforiaLocalizer;
    private OpenGLMatrix lastLocation = null; // class members
    public static VuforiaTrackables targetsUltGoal;
    public static VuforiaTrackable redAllianceTarget ;
    private static VuforiaSystem instance;
    private static final VuforiaLocalizer.CameraDirection CAMERA_DIRECTION = VuforiaLocalizer.CameraDirection.BACK;

    public static VuforiaSystem getInstance(WebcamName webcamName) {
        if (instance == null) instance = new VuforiaSystem(webcamName);
        return instance;
    }

    public static VuforiaSystem getInstance() {
        if (instance == null) instance = new VuforiaSystem(null);
        return instance;
    }

    private VuforiaSystem(WebcamName webcamName) {
        initVuforiaLocalizer(webcamName);
        initUltsGoal(webcamName);
    }

    private void initVuforiaLocalizer(WebcamName webcamName) {
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();
        parameters.vuforiaLicenseKey = BuildConfig.NOCTURNAL_VUFORIA_KEY;
        parameters.useExtendedTracking = true;
        if (webcamName != null) {
            parameters.cameraName = webcamName;
        } else {
            parameters.cameraDirection = CAMERA_DIRECTION;
        }

        vuforiaLocalizer = ClassFactory.getInstance().createVuforia(parameters);
    }

    private void initUltsGoal(WebcamName webcamName) {
        // TODO most likely will need to end up establishing precise positions in the future
        // Next, translate the camera lens to where it is on the robot.
        // In this example, it is centered (left to right), but forward of the middle of the robot, and above ground level.
        final float CAMERA_FORWARD_DISPLACEMENT = 4.0f * mmPerInch;   // eg: Camera is 4 Inches in front of robot-center
        final float CAMERA_VERTICAL_DISPLACEMENT = 8.0f * mmPerInch;   // eg: Camera is 8 Inches above ground
        final float CAMERA_LEFT_DISPLACEMENT = 0;     // eg: Camera is ON the robot's center line
        float phoneXRotate = 0;
        float phoneYRotate = 0;
        float phoneZRotate = 0;

        OpenGLMatrix robotFromCamera = OpenGLMatrix
                .translation(CAMERA_FORWARD_DISPLACEMENT, CAMERA_LEFT_DISPLACEMENT, CAMERA_VERTICAL_DISPLACEMENT)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, YZX, DEGREES, phoneYRotate, phoneZRotate, phoneXRotate));

        targetsUltGoal = vuforiaLocalizer.loadTrackablesFromAsset("UltimateGoal");

        redAllianceTarget = targetsUltGoal.get(2);
        redAllianceTarget.setName("Red Alliance");
        if (webcamName == null) {
            ((VuforiaTrackableDefaultListener) redAllianceTarget.getListener()).setPhoneInformation(robotFromCamera, VuforiaLocalizer.CameraDirection.BACK);
        } else {
            ((VuforiaTrackableDefaultListener) redAllianceTarget.getListener()).setCameraLocationOnRobot(webcamName, robotFromCamera);
        }

        redAllianceTarget.setLocation(OpenGLMatrix
                .translation(0, -halfField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 180)));
    }

    public VuforiaLocalizer getVuforiaLocalizer() {
        return vuforiaLocalizer;
    }

    public void activate() {
        targetsUltGoal.activate();
    }

    public void disable() {
        targetsUltGoal.deactivate();
        instance = null;
    }

    /**
     * Index 0: Rotation of the target relative to the robot
     * Index 1: Vertical distance from target relative to the robot]
     */
    public float getXOffset() {
        VuforiaTrackableDefaultListener listener = ((VuforiaTrackableDefaultListener)redAllianceTarget.getListener());
        if (listener.isVisible()) {
            return lastLocation.getTranslation().get(0) - redAllianceTarget.getLocation().getTranslation().get(0);
        }
        return Float.NaN;
    }

    public float getYOffset() {
        VuforiaTrackableDefaultListener listener = ((VuforiaTrackableDefaultListener)redAllianceTarget.getListener());
        if (listener.isVisible()) {
            return lastLocation.getTranslation().get(1) - redAllianceTarget.getLocation().getTranslation().get(1);
        }
        return Float.NaN;
    }

    public float getZOffset() {
        VuforiaTrackableDefaultListener listener = ((VuforiaTrackableDefaultListener)redAllianceTarget.getListener());
        if (listener.isVisible()) {
            return lastLocation.getTranslation().get(2) - redAllianceTarget.getLocation().getTranslation().get(2);
        }
        return Float.NaN;
    }

    public VectorF vector () {
        OpenGLMatrix robotLocationTransform = ((VuforiaTrackableDefaultListener)redAllianceTarget.getListener()).getUpdatedRobotLocation();
        if (robotLocationTransform != null) {
            lastLocation = robotLocationTransform;
        }
        if (lastLocation == null) {
            return null;
        }
        return lastLocation.getTranslation();
    }
}