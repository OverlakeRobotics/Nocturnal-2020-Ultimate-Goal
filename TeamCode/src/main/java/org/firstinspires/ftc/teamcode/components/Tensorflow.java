package org.firstinspires.ftc.teamcode.components;

import android.util.Log;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.teamcode.helpers.TargetDropBox;

import java.util.EnumMap;
import java.util.List;

public class Tensorflow {

    private static final String TFOD_MODEL_ASSET = "UltimateGoal.tflite";
    private static final String LABEL_FIRST_ELEMENT = "Four"; //FourRings
    private static final String LABEL_SECOND_ELEMENT = "One"; //OneRing

    private TFObjectDetector tfod; //declaring objectDetector

    /**
     * Constructor for TensorFlow
     */
    public Tensorflow() {
        VuforiaSystem vuforiaSystem = VuforiaSystem.getInstance();
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(); //creating parameters
        tfodParameters.minResultConfidence = 0.3f; //minimumConfidenceNecessaryForActingOnDetection
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforiaSystem.getVuforiaLocalizer()); //create objectDetector
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_FIRST_ELEMENT, LABEL_SECOND_ELEMENT); //loading models
        tfod.activate(); //turnOn
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
            return tfod.getRecognitions(); //Returns the list of recognitions, but only if they are different than the last call to {@link #getUpdatedRecognitions()}.
        }
        return null;
    }

    /**
     * Activates TensorFlow
     */
    public void activate() {
        if (tfod != null) {
            tfod.activate();
        }

    }

    /**
     * Shuts down TensorFlow
     */
    public void shutdown() {
        if (tfod != null) {
            tfod.shutdown();
        }
    }

    /**
     * Returns the target region currently selected
     * @return the target region currently selected
     */
    public TargetDropBox getTargetRegion() {
        if (tfod == null) {
            return TargetDropBox.BOX_A;
        }
        List<Recognition> recognitionList = getInference();
        for (int i = 0; i < recognitionList.size(); i++) {
            Log.d("DETECTION", recognitionList.get(i).getLabel());
            Log.d("DETECTION", "Confidence: " + recognitionList.get(i).getConfidence());
        }
        if (recognitionList.size() == 1) {
            if (recognitionList.get(0).getLabel().equals("Four")) {
                return TargetDropBox.BOX_C;
            }
            if (recognitionList.get(0).getLabel().equals("One")) {
                return TargetDropBox.BOX_B;
            }
        }
        return TargetDropBox.BOX_A;
    }
}