package org.firstinspires.ftc.teamcode.components;

import com.qualcomm.hardware.BuildConfig;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

public class Tensorflow {

    public enum SquareState{
        BOX_A, BOX_B, BOX_C,
    }

    EnumMap<SquareState, Integer> boxCoordinates;

    private static final String TFOD_MODEL_ASSET = "UltimateGoal.tflite";
    private static final String LABEL_FIRST_ELEMENT = "Four"; //FourRings
    private static final String LABEL_SECOND_ELEMENT = "One"; //OneRing
    private static final String VUFORIA_KEY = BuildConfig.VUFORIA_KEY;
            //"Ad0Srbr/////AAABmdpa0/j2K0DPhXQjE2Hyum9QUQXZO8uAVCNpwlogfxiVmEaSuqHoTMWcV9nLlQpEnh5bwTlQG+T35Vir8IpdrSdk7TctIqH3QBuJFdHsx5hlcn74xa7AiQSJgUD/n7JJ2zJ/Er5Hc+b+r616Jf1YU6RO63Ajk5+TFB9N3a85NjMD6eDm+C6f14647ELnmGC03poSOeczbX7hZpIEObtYdVyKZ2NQ/26xDfSwwJuyMgUHwWY6nl6mk0GMnIGvu0/HoGNgyR5EkUQWyx9XlmxSrldY7BIEVkiKmracvD7W9hEGZ2nPied6DTY5RFNuFX07io6+I59/d7291NXKVMDnFAqSt4a2JYsECv+j7b25S0mD";;

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
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_FIRST_ELEMENT, LABEL_SECOND_ELEMENT); //loading models
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
        List<Recognition> toOperateOffOf = getInference();
        if (toOperateOffOf.size() == 1){
            int i = 0;
            for (Recognition recognitions : toOperateOffOf){
                telemetry.addData(String.format("label (%d)", i), recognitions.getLabel());
                telemetry.addData(String.format("  left,top (%d)", i), "%.03f , %.03f",
                        recognitions.getLeft(), recognitions.getTop());
                telemetry.addData(String.format("  right,bottom (%d)", i), "%.03f , %.03f",
                        recognitions.getRight(), recognitions.getBottom());
                if (recognitions.getConfidence() >= 0.4){
                    if (recognitions.getLabel().equals("Four")){
                        telemetry.addData("Returning SquareState", SquareState.BOX_A);
                        return SquareState.BOX_A;
                    }
                    else{
                        telemetry.addData("Returning SquareState", SquareState.BOX_B);
                        return SquareState.BOX_B;
                    }
                }
                telemetry.addData("Returning SquareState", SquareState.BOX_C);
                return SquareState.BOX_C;
            }
        }
        else{
            telemetry.addData("Returning SquareState", SquareState.BOX_C);
            return SquareState.BOX_C;
        }
        telemetry.addData("Oopsie", null);
        return null;
        /*ArrayList<Float> heights = new ArrayList<Float>();
        int length = getInference().size();
        for (Recognition recognitions : getInference()){
            if (recognitions.getConfidence() >= 0.4){
                heights.add((recognitions.getTop() - recognitions.getBottom()));
            }
        }
        if (heights.size() == 1){
            if (heights.get(0) > 0.4f && heights.get(0) < 0.5f){
                return SquareState.BOX_A;
            }
            if (heights.get(0) > 0.5f && heights.get(0) < 0.8f){
                return SquareState.BOX_B;
            }
            if (heights.get(0) > 1.0f){
                return SquareState.BOX_C;
            }
        }
        else{
            if (length == 0){
                return SquareState.BOX_A;
            }
            if (length == 1){
                return SquareState.BOX_B;
            }
            if (length == 2){
                return SquareState.BOX_B;
            }
            if (length == 3){
                return SquareState.BOX_C;
            }
            if (length == 4){
                return SquareState.BOX_C;
            }
        }
        return null;*/
    }

}