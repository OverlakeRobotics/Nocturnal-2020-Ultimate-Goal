package org.firstinspires.ftc.teamcode.tests;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "MotorEncoderTest", group = "Test")
public class MotorEncoderTest extends OpMode {
    private DcMotor slider;
    public void init() {
        slider = hardwareMap.get(DcMotor.class, "SLIDER_MOTOR");
        slider.setDirection(DcMotorSimple.Direction.REVERSE);
        slider.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }
    public void loop() {
        Log.d("Slider ", "Current Position: " + slider.getCurrentPosition());
    }
}
