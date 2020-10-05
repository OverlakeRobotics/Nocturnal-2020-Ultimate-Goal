package org.firstinspires.ftc.teamcode.components;

import java.util.List;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;

public class Tensorflow {
    private static final String TFOD_MODEL_ASSET = "UltimateGoal.tflite";
    private static final String LABEL_FIRST_ELEMENT = "Zero"; //ZeroRings
    private static final String LABEL_SECOND_ELEMENT = "One"; //OneRing
    private static final String LABEL_THIRD_ELEMENT = "Four"; //FourRing
    private static final String VUFORIA_KEY = "Ad0Srbr/////AAABmdpa0/j2K0DPhXQjE2Hyum9QUQXZO8uAVCNpwlogfxiVmEaSuqHoTMWcV9nLlQpEnh5bwTlQG+T35Vir8IpdrSdk7TctIqH3QBuJFdHsx5hlcn74xa7AiQSJgUD/n7JJ2zJ/Er5Hc+b+r616Jf1YU6RO63Ajk5+TFB9N3a85NjMD6eDm+C6f14647ELnmGC03poSOeczbX7hZpIEObtYdVyKZ2NQ/26xDfSwwJuyMgUHwWY6nl6mk0GMnIGvu0/HoGNgyR5EkUQWyx9XlmxSrldY7BIEVkiKmracvD7W9hEGZ2nPied6DTY5RFNuFX07io6+I59/d7291NXKVMDnFAqSt4a2JYsECv+j7b25S0mD";;

    private VuforiaLocalizer vuforia; //declaring VuforiaLocalizer - converts Vuforia Frame into AndroidBitMap
    private TFObjectDetector tfod; //declaring objectDetector

    public Tensorflow(WebcamName name, int tfodMonitorId) { //name tfod id
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(); //creating parameters

        parameters.vuforiaLicenseKey = VUFORIA_KEY; //setting key

        parameters.cameraName = name; //setting name to name

        vuforia = ClassFactory.getInstance().createVuforia(parameters); //creating vuforia obj w/parameters

        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorId); //creating parameters
        tfodParameters.minResultConfidence = 0.3f; //minimumConfidenceNecessaryForActingOnDetection
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia); //create objectDetector
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_FIRST_ELEMENT, LABEL_SECOND_ELEMENT, LABEL_THIRD_ELEMENT); //loading models
        tfod.activate(); //turnOn
        //Hi, this is Anish, and I'm teaching Vincent and Ethan, how git works.
    }

    public List<Recognition> getInference() { //get "image" back, a bunch of Recognitions - check out instance variables
        if (tfod != null) {
            return tfod.getUpdatedRecognitions(); //Returns the list of recognitions, but only if they are different than the last call to {@link #getUpdatedRecognitions()}.
        }
        return null;
    }

    public void activate() {
        tfod.activate();
    } //activate

    public void shutdown() {
        tfod.shutdown();
    } //deactivate
}