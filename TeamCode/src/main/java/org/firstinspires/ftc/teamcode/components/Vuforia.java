package org.firstinspires.ftc.teamcode.components;

import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.robotcore.internal.vuforia.VuforiaLocalizerImpl;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;
import java.util.ArrayList;
import java.util.List;

public class Vuforia {

    public enum CameraChoice {
        PHONE_FRONT, PHONE_BACK, WEBCAM1, WEBCAM2;
    }

    private class VuforiaLocalizer extends VuforiaLocalizerImpl {
        public VuforiaLocalizer(Parameters parameters) {
            super(parameters);
        }

        public void close() {
            super.close();
        }
    }

    private static final String VUFORIA_KEY =
            "Ad0Srbr/////AAABmdpa0/j2K0DPhXQjE2Hyum9QUQXZO8uAVCNpwlogfxiVmEaSuqHoTMWcV9nLlQpEnh5bwTlQG+T35Vir8IpdrSdk7TctIqH3QBuJFdHsx5hlcn74xa7AiQSJgUD/n7JJ2zJ/Er5Hc+b+r616Jf1YU6RO63Ajk5+TFB9N3a85NjMD6eDm+C6f14647ELnmGC03poSOeczbX7hZpIEObtYdVyKZ2NQ/26xDfSwwJuyMgUHwWY6nl6mk0GMnIGvu0/HoGNgyR5EkUQWyx9XlmxSrldY7BIEVkiKmracvD7W9hEGZ2nPied6DTY5RFNuFX07io6+I59/d7291NXKVMDnFAqSt4a2JYsECv+j7b25S0mD";

    private static final float mmPerInch        = 25.4f;                    // constant for converting measurements from inches to millimeters
    private static final float mmTargetHeight   = (6) * mmPerInch;          // the height of the center of the target image above the floor

    private static final float halfField = 72 * mmPerInch;                  // constants for perimeter targets
    private static final float quadField  = 36 * mmPerInch;

    private OpenGLMatrix lastLocation = null; // class members
    private VuforiaLocalizer vuforia = null;
    private float phoneXRotate    = 0;
    private float phoneYRotate    = 0;
    private float phoneZRotate    = 0;
    public VuforiaTrackables targetsUltGoal;
    public VuforiaTrackables targetsField;
    private List<VuforiaTrackable> allTrackables;

    public Vuforia(HardwareMap hardwareMap, CameraChoice choice) {
        vuforia = setCamera(hardwareMap, choice);
    }

    public void close() {
        vuforia.close();
    }
    
    public Orientation getRobotHeading() {
        return Orientation.getOrientation(lastLocation, EXTRINSIC, XYZ, DEGREES);
    }

    public VectorF getRobotPosition() {
        return lastLocation.getTranslation();
    }

    public boolean isTargetVisible(VuforiaTrackable targetTrackable) {
        for (VuforiaTrackable trackable : allTrackables) {
            if (((VuforiaTrackableDefaultListener)trackable.getListener()).isVisible()) {
                if (trackable.getName().equals(targetTrackable.getName())) {
                    OpenGLMatrix robotLocationTransform = ((VuforiaTrackableDefaultListener)trackable.getListener()).getUpdatedRobotLocation();
                    if (robotLocationTransform != null) {
                        lastLocation = robotLocationTransform;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isAnyTargetVisible() {
        for (VuforiaTrackable trackable : allTrackables) {
            if (((VuforiaTrackableDefaultListener)trackable.getListener()).isVisible()) {
                OpenGLMatrix robotLocationTransform = ((VuforiaTrackableDefaultListener)trackable.getListener()).getUpdatedRobotLocation();
                if (robotLocationTransform != null) {
                    lastLocation = robotLocationTransform;
                }
                return true;
            }
        }
        return false;
    }

    public void activate() {
        targetsUltGoal.activate();
        targetsField.activate();
    }

    public void disable() {
        targetsUltGoal.deactivate();
        targetsField.deactivate();
    }

    public VuforiaLocalizer setCamera(HardwareMap hardwareMap, CameraChoice cameraChoice) {
        if (vuforia != null)
            vuforia.close();
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());

        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        parameters.useExtendedTracking = false;

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        switch (cameraChoice) {
            case PHONE_FRONT:
                parameters.cameraDirection = VuforiaLocalizer.CameraDirection.FRONT;
                break;
            case PHONE_BACK:
                parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
                break;
            case WEBCAM1:
                parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");
                break;
            case WEBCAM2:
                parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 2");
                break;
        }
        vuforia = new VuforiaLocalizer(parameters);
        initializeTrackables(vuforia);
        return vuforia;
    }

    private void initializeTrackables(VuforiaLocalizer vuforia) {
        targetsUltGoal = vuforia.loadTrackablesFromAsset("UltimateGoal");
        targetsField = vuforia.loadTrackablesFromAsset("Skystone");

        VuforiaTrackable blueTowerGoalTarget = targetsUltGoal.get(0);
        blueTowerGoalTarget.setName("Blue Tower Goal");
        VuforiaTrackable redTowerGoalTarget = targetsUltGoal.get(1);
        redTowerGoalTarget.setName("Red Tower Goal");
        VuforiaTrackable redAllianceTarget = targetsUltGoal.get(2);
        redAllianceTarget.setName("Red Alliance");
        VuforiaTrackable blueAllianceTarget = targetsUltGoal.get(3);
        blueAllianceTarget.setName("Blue Alliance");
        VuforiaTrackable frontWallTarget = targetsUltGoal.get(4);
        frontWallTarget.setName("Front Wall");
        VuforiaTrackable red1 = targetsField.get(5);
        red1.setName("Red Perimeter 1");
        VuforiaTrackable red2 = targetsField.get(6);
        red2.setName("Red Perimeter 2");
        VuforiaTrackable front1 = targetsField.get(7);
        front1.setName("Front Perimeter 1");
        VuforiaTrackable front2 = targetsField.get(8);
        front2.setName("Front Perimeter 2");
        VuforiaTrackable blue1 = targetsField.get(9);
        blue1.setName("Blue Perimeter 1");
        VuforiaTrackable blue2 = targetsField.get(10);
        blue2.setName("Blue Perimeter 2");
        VuforiaTrackable rear1 = targetsField.get(11);
        rear1.setName("Rear Perimeter 1");
        VuforiaTrackable rear2 = targetsField.get(12);
        rear2.setName("Rear Perimeter 2");

        // For convenience, gather together all the trackable objects in one easily-iterable collection */
        allTrackables = new ArrayList<VuforiaTrackable>();
        allTrackables.addAll(targetsUltGoal);

        // Set the position of the Stone Target.  Since it's not fixed in position, assume it's at the field origin.
        // Rotated it to to face forward, and raised it to sit on the ground correctly.
        // This can be used for generic target-centric approach algorithms

        //TODO: Update these things.

        /*
        target.setLocation(OpenGLMatrix
                .translation(X position, Y position, Z position) // Actual location on the field with coordinates
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, X rotation, Y rotation, Z rotation))); // Rotation
        */
        redAllianceTarget.setLocation(OpenGLMatrix
                .translation(0, -halfField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 180)));

        blueAllianceTarget.setLocation(OpenGLMatrix
                .translation(0, halfField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 0)));
        frontWallTarget.setLocation(OpenGLMatrix
                .translation(-halfField, 0, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0 , 90)));

        // The tower goal targets are located a quarter field length from the ends of the back perimeter wall.
        blueTowerGoalTarget.setLocation(OpenGLMatrix
                .translation(halfField, quadField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0 , -90)));
        redTowerGoalTarget.setLocation(OpenGLMatrix
                .translation(halfField, -quadField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, -90)));

        //Set the position of the perimeter targets with relation to origin (center of field)
        red1.setLocation(OpenGLMatrix
                .translation(quadField, -halfField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 180)));

        red2.setLocation(OpenGLMatrix
                .translation(-quadField, -halfField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 180)));

        front1.setLocation(OpenGLMatrix
                .translation(-halfField, -quadField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0 , 90)));

        front2.setLocation(OpenGLMatrix
                .translation(-halfField, quadField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 90)));

        blue1.setLocation(OpenGLMatrix
                .translation(-quadField, halfField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 0)));

        blue2.setLocation(OpenGLMatrix
                .translation(quadField, halfField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 0)));

        rear1.setLocation(OpenGLMatrix
                .translation(halfField, quadField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0 , -90)));

        rear2.setLocation(OpenGLMatrix
                .translation(halfField, -quadField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, -90)));

        // TODO most likely will need to end up establishing precise positions in the future
        /**  Let all the trackable listeners know where the phone is.  */
        final float CAMERA_FORWARD_DISPLACEMENT  = 4.0f * mmPerInch;   // eg: Camera is 4 Inches in front of robot center
        final float CAMERA_VERTICAL_DISPLACEMENT = 8.0f * mmPerInch;   // eg: Camera is 8 Inches above ground
        final float CAMERA_LEFT_DISPLACEMENT     = 0;     // eg: Camera is ON the robot's center line

//        OpenGLMatrix robotFromCamera = OpenGLMatrix
//                .translation(CAMERA_FORWARD_DISPLACEMENT, CAMERA_LEFT_DISPLACEMENT, CAMERA_VERTICAL_DISPLACEMENT)
//                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, YZX, DEGREES, phoneYRotate, phoneZRotate, phoneXRotate));
//
//        for (VuforiaTrackable trackable : allTrackables) {
//            ((VuforiaTrackableDefaultListener) trackable.getListener()).setPhoneInformation(robotFromCamera, parameters.cameraDirection);
//        }

        targetsUltGoal.activate();
        targetsField.activate();
    }
}
