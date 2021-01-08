package org.firstinspires.ftc.teamcode.components;

import android.util.Log;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.robotcore.internal.vuforia.VuforiaLocalizerImpl;
import org.firstinspires.ftc.teamcode.BuildConfig;
import org.firstinspires.ftc.teamcode.Constants;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.YZX;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;
import java.util.ArrayList;
import java.util.List;

/** Teddy, trust my code, you blasphemer. */
public class VuforiaSystem {

    public enum CameraChoice {
        PHONE_BACK, WEBCAM1
    }

    private static final float mmPerInch = 25.4f;                    // constant for converting measurements from inches to millimeters
    private static final float mmTargetHeight = (6) * mmPerInch;          // the height of the center of the target image above the floor

    private static final float halfField = 72 * mmPerInch;                  // constants for perimeter targets
    private static final float quadField = 36 * mmPerInch;

    private static VuforiaLocalizer vuforiaLocalizer;
    private static CameraChoice currentCameraChoice;
    private static String currentTag;
    private OpenGLMatrix lastLocation = null; // class members
    public static VuforiaTrackables targetsUltGoal;
    private static ArrayList<VuforiaTrackable> allTrackables;


    public static List<VuforiaTrackable> getTrackables() {
        return allTrackables;
    }

    public static VuforiaLocalizer getVuforiaLocalizer(HardwareMap hardwareMap, CameraChoice cameraChoice, String tag) {
        if (currentTag == null || !currentTag.equals(tag)) {
            currentTag = tag;
        } else if (cameraChoice.equals(currentCameraChoice) && vuforiaLocalizer != null) {
            return vuforiaLocalizer;
        }
        currentCameraChoice = cameraChoice;

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        parameters.vuforiaLicenseKey = BuildConfig.NOCTURNAL_VUFORIA_KEY;
        parameters.useExtendedTracking = true;
        parameters.cameraMonitorFeedback = VuforiaLocalizer.Parameters.CameraMonitorFeedback.NONE;


        switch (cameraChoice) {
            case PHONE_BACK:
                parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
                break;
            case WEBCAM1:
                parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");
                break;
        }

        if (tag.equals(Constants.TENSORFLOW)) {
            vuforiaLocalizer = ClassFactory.getInstance().createVuforia(parameters);
        }
        return vuforiaLocalizer;
    }

    public VuforiaSystem(HardwareMap hardwareMap) {

        Log.d("Debug", "After setViewParent() called");
        initUltsGoal(org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.BACK, vuforiaLocalizer);
        activate();
    }

    private static void initUltsGoal(VuforiaLocalizer.CameraDirection cameraDirection, VuforiaLocalizer vuforiaLocalizer) {
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

        VuforiaTrackable blueTowerGoalTarget = targetsUltGoal.get(0);
        blueTowerGoalTarget.setName("Blue Tower Goal");
        VuforiaTrackable redTowerGoalTarget = targetsUltGoal.get(1);
        redTowerGoalTarget.setName("Red Tower Goal");

        // For convenience, gather together all the trackable objects in one easily-iterable collection */
        allTrackables = new ArrayList<>();
        allTrackables.addAll(targetsUltGoal);

        /**  Let all the trackable listeners know where the phone is.  */
        for (VuforiaTrackable trackable : allTrackables) {
            ((VuforiaTrackableDefaultListener) trackable.getListener()).setPhoneInformation(robotFromCamera, cameraDirection);
        }

        // The tower goal targets are located a quarter field length from the ends of the back perimeter wall.
        blueTowerGoalTarget.setLocation(OpenGLMatrix
                .translation(halfField, quadField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0 , -90)));
        redTowerGoalTarget.setLocation(OpenGLMatrix
                .translation(halfField, -quadField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, -90)));
    }

    public boolean isTargetVisible(VuforiaTrackable targetTrackable) {
        if (((VuforiaTrackableDefaultListener)targetTrackable.getListener()).isVisible()) {
            OpenGLMatrix robotLocationTransform = ((VuforiaTrackableDefaultListener)targetTrackable.getListener()).getUpdatedRobotLocation();
            if (robotLocationTransform != null) {
                lastLocation = robotLocationTransform;
            }
            return true;
        }
        return false;
    }

    public boolean isAnyTargetVisible() {
        for (VuforiaTrackable trackable : allTrackables) {
            if (isTargetVisible(trackable)) {
                return true;
            }
        }
        return false;
    }

    public void activate() {
        targetsUltGoal.activate();
    }

    public void disable() {
        targetsUltGoal.deactivate();
        vuforiaLocalizer = null;
    }

    /**
     * Index 0: Rotation of the target relative to the robot
     * Index 1: Vertical distance from target relative to the robot]
     */
    public float getXOffset(VuforiaTrackable trackable, OpenGLMatrix lastLocation) {
        VuforiaTrackableDefaultListener listener = ((VuforiaTrackableDefaultListener)trackable.getListener());
        if (listener.isVisible()) {
            return lastLocation.getTranslation().get(0) - trackable.getLocation().getTranslation().get(0);
        }
        return Float.NaN;
    }

    public float getYOffset(VuforiaTrackable trackable, OpenGLMatrix lastLocation) {
        VuforiaTrackableDefaultListener listener = ((VuforiaTrackableDefaultListener)trackable.getListener());
        if (listener.isVisible()) {
            return lastLocation.getTranslation().get(1) - trackable.getLocation().getTranslation().get(1);
        }
        return Float.NaN;
    }

    public float getZOffset(VuforiaTrackable trackable, OpenGLMatrix lastLocation) {
        VuforiaTrackableDefaultListener listener = ((VuforiaTrackableDefaultListener)trackable.getListener());
        if (listener.isVisible()) {
            return lastLocation.getTranslation().get(2) - trackable.getLocation().getTranslation().get(2);
        }
        return Float.NaN;
    }
}