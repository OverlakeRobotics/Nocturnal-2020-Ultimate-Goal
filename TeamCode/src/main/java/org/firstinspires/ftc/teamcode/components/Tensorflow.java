package org.firstinspires.ftc.teamcode.components;

import java.util.EnumMap;
import java.util.List;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.teamcode.BuildConfig;

public class Tensorflow {

    public enum SquareState{
        BOX_A, BOX_B, BOX_C,
    }

    EnumMap<SquareState, Integer> boxCoordinates;

    private static final String TFOD_MODEL_ASSET = "UltimateGoal.tflite";
    private static final String LABEL_FIRST_ELEMENT = "Four"; //FourRings
    private static final String LABEL_SECOND_ELEMENT = "One"; //OneRing
    private static final String VUFORIA_KEY = BuildConfig.NOCTURNAL_VUFORIA_KEY;

    private VuforiaLocalizer vuforia; //declaring VuforiaLocalizer - converts Vuforia Frame into AndroidBitMap
    private TFObjectDetector tfod; //declaring objectDetector

    public Tensorflow(CameraName name, int tfodMonitorId) { //name tfod id
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(); //creating parameters

        parameters.vuforiaLicenseKey = VUFORIA_KEY; //setting key

        parameters.cameraName = name; //setting name to name

        vuforia = ClassFactory.getInstance().createVuforia(parameters); //creating vuforia obj w/parameters

        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorId); //creating parameters
        tfodParameters.minResultConfidence = 0.3f; //minimumConfidenceNecessaryForActingOnOr'Accepting'Detection
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia); //create objectDetector
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_FIRST_ELEMENT, LABEL_SECOND_ELEMENT); //loading models
        tfod.activate(); //turnOn
    }

    public Tensorflow(VuforiaLocalizer.CameraDirection cameraDirection, int tfodMonitorId) { //name tfod id
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(); //creating parameters

        parameters.vuforiaLicenseKey = VUFORIA_KEY; //setting key

        parameters.cameraDirection = cameraDirection; //setting name to name

        initVuforia();

        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorId); //creating parameters
        tfodParameters.minimumConfidence = 0.3f; //minimumConfidenceNecessaryForActingOnDetection
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia); //create objectDetector
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_FIRST_ELEMENT, LABEL_SECOND_ELEMENT); //loading models
        tfod.activate(); //turnOn
        //Hi, this is Anish, and I'm teaching Vincent and Ethan, how git works.
    }

    public void initVuforia () {
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;

        vuforia = new VuforiaSystem.VuforiaLocalizer(parameters);
    }

    /** Method getInference()
     *
     * Returns a list of Tensoflow recognitions
     *
     * A recognition is, from my understanding, TensorFlow having identifying
     * an object as one of its labels - the thing it should be looking for.
     *
     * In a sense, this is sort of a 'picture' of all of the relevant
     * details or aspects of the picture. This is good so that we can
     * iterate through these Recognitions and find important information,
     * such as which label (or type) - amongst other things - these Recognitions are.
     *
     * Your garbage explanation brought to you by: @anishch
     */

    public List<Recognition> getInference() { //get "image" back, a bunch of Recognitions - check out instance variables
        if (tfod != null) {
            return tfod.getUpdatedRecognitions(); //Returns the list of recognitions, but only if they are different than the last call to {@link #getUpdatedRecognitions()}.
        }
        return null;
    }

    public void activate() {
        if (tfod != null){
            tfod.activate();
        }

    } //activate

    public void shutdown() {
        if (tfod != null){
            tfod.shutdown();
        }
    } //deactivate

    public SquareState getTargetRegion(){
        if (tfod == null){
            return null;
        }
        List<Recognition> recognitionList = getInference();
        if (recognitionList.size() == 1){
            int i = 0;
            if (recognitionList.get(0).getConfidence() >= 0.4){
                if (recognitionList.get(0).getLabel() == LABEL_FIRST_ELEMENT){
                    return SquareState.BOX_A;
                }
                else{
                    return SquareState.BOX_B;
                }
            }
        }
        else{
            return SquareState.BOX_C;
        }
        return null;
    }

}