package org.firstinspires.ftc.teamcode.components;

import android.util.Log;

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
import org.firstinspires.ftc.teamcode.helpers.Constants;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.YZX;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;
import static org.firstinspires.ftc.teamcode.helpers.Constants.mmPerInch;
import static org.firstinspires.ftc.teamcode.helpers.Constants.mmTargetHeight;

/** Teddy, trust my code, you blasphemer. */
public class VuforiaSystem {

    private VuforiaLocalizer vuforiaLocalizer;
    private OpenGLMatrix lastLocation; // class members
    public static VuforiaTrackables targetsUltGoal;
    public static VuforiaTrackable redAllianceTarget;
    private VuforiaTrackableDefaultListener listener;
    private static VuforiaSystem instance;
    private static final VuforiaLocalizer.CameraDirection CAMERA_DIRECTION = VuforiaLocalizer.CameraDirection.BACK;

    /**
     * @param webcamName to use for vuforia
     * @return instance of vuforia singleton
     */
    public static VuforiaSystem getInstance(WebcamName webcamName) {
        if (instance == null) instance = new VuforiaSystem(webcamName);
        return instance;
    }

    /**
     * @return instance of vuforia singleton
     */
    public static VuforiaSystem getInstance() {
        if (instance == null) instance = new VuforiaSystem(null);
        return instance;
    }

    /**
     * Constructor
     * @param webcamName to use for init
     */
    private VuforiaSystem(WebcamName webcamName) {
        initVuforiaLocalizer(webcamName);
        initUltsGoal(webcamName);
    }

    /**
     * Initializes the vuforia localizer
     * @param webcamName to use for init
     */
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

    /**
     * Initializes the UltsGoal
     * @param webcamName of the webcam to use
     */
    private void initUltsGoal(WebcamName webcamName) {
        // TODO most likely will need to end up establishing precise positions in the future
        // Next, translate the camera lens to where it is on the robot.
        // In this example, it is centered (left to right), but forward of the middle of the robot, and above ground level.
        float phoneXRotate = 0;
        float phoneYRotate = 0;
        float phoneZRotate = 0;

        //TODO fix these coordinates
        OpenGLMatrix robotFromCamera = OpenGLMatrix
                .translation(Constants.CAMERA_FORWARD_DISPLACEMENT, Constants.CAMERA_LEFT_DISPLACEMENT, Constants.CAMERA_VERTICAL_DISPLACEMENT)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, YZX, DEGREES, phoneYRotate, phoneZRotate, phoneXRotate));

        targetsUltGoal = vuforiaLocalizer.loadTrackablesFromAsset("UltimateGoal");

        redAllianceTarget = targetsUltGoal.get(2);
        redAllianceTarget.setName("Red Alliance");
        listener = ((VuforiaTrackableDefaultListener)redAllianceTarget.getListener());
        if (webcamName == null) {
            listener.setPhoneInformation(robotFromCamera, VuforiaLocalizer.CameraDirection.BACK);
        } else {
            listener.setCameraLocationOnRobot(webcamName, robotFromCamera);
        }

        redAllianceTarget.setLocation(OpenGLMatrix
                .translation(0, 0, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 180)));

        lastLocation = listener.getUpdatedRobotLocation();
    }

    /**
     * @return current vuforia localizer
     */
    public VuforiaLocalizer getVuforiaLocalizer() {
        return vuforiaLocalizer;
    }

    /**
     * Activates vuforia and sets up targetsUltGoal
     */
    public void activate() {
        targetsUltGoal.activate();
    }

    /**
     * Deactivates vuforia completely
     */
    public void deactivate() {
        targetsUltGoal.deactivate();
        instance = null;
    }

    /**
     * Index 0: Rotation of the target relative to the robot
     * Index 1: Vertical distance from target relative to the robot
     * @return x offset
     */
    public float getXOffset() {
        if (lastLocation != null) {
            Log.d("CALIBRATION", "X Offset == " + (lastLocation.getTranslation().get(0) - redAllianceTarget.getLocation().getTranslation().get(0)));
            return lastLocation.getTranslation().get(0) - redAllianceTarget.getLocation().getTranslation().get(0);
        }
        return Float.NaN;
    }

    /**
     *
     * @return y offset
     */
    public float getYOffset() {
        if (lastLocation != null) {
            Log.d("CALIBRATION", "Y Offset == " + (lastLocation.getTranslation().get(1) - redAllianceTarget.getLocation().getTranslation().get(1)));
            return lastLocation.getTranslation().get(1) - redAllianceTarget.getLocation().getTranslation().get(1);
        }
        return Float.NaN;
    }

    /**
     *
     * @return z offset
     */
    public float getZOffset() {
        if (lastLocation != null) {
            Log.d("CALIBRATION", "Z Offset == " + (lastLocation.getTranslation().get(2) - redAllianceTarget.getLocation().getTranslation().get(2)));
            return lastLocation.getTranslation().get(2) - redAllianceTarget.getLocation().getTranslation().get(2);
        }
        return Float.NaN;
    }

    /**
     *
     * @return vector of the robot's last location
     */
    public VectorF vector() {
        if (listener.isVisible()) {
            return lastLocation.getTranslation();
        }
        return null;
    }

    /**
     * Updates the robot's current position if there is a new position
     */
    public void updateLocation() {
        OpenGLMatrix proposedPosition = listener.getUpdatedRobotLocation();
        if (proposedPosition != null) {
            lastLocation = proposedPosition;
        }
    }
}