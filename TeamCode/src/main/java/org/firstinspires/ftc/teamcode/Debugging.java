package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.hitechnic.HiTechnicNxtColorSensor;
import com.qualcomm.hardware.hitechnic.HiTechnicNxtLightSensor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import java.util.ArrayList;

/**
 * Created by robotics on 2/20/17.
 */
@TeleOp(name = "Debugging", group = "Debugging")
public class Debugging extends OpMode {

    private DcMotor backLeft;
    private DcMotor backRight;
    private DcMotor frontLeft;
    private DcMotor frontRight;

    private ColorSensor colorSensor;
    private OpticalDistanceSensor ods;
    private HiTechnicNxtLightSensor lightSensor;
    private Servo colorServo;
    private Servo odsServo;

    private boolean forward = true;

    private ArrayList<DcMotor> motors = new ArrayList<>();

    public void init() {

        //Initializes Hardware
        backLeft = hardwareMap.dcMotor.get("backLeft");
        backRight = hardwareMap.dcMotor.get("backRight");
        frontLeft = hardwareMap.dcMotor.get("frontLeft");
        frontRight = hardwareMap.dcMotor.get("frontRight");
        colorSensor = hardwareMap.colorSensor.get("colorSensor");
        ods = hardwareMap.opticalDistanceSensor.get("distanceSensor");
        colorServo = hardwareMap.servo.get("colorServo");
        odsServo = hardwareMap.servo.get("odsServo");
        lightSensor = (HiTechnicNxtLightSensor) hardwareMap.get("lightSensor");
        lightSensor.enableLed(true);

        motors.add(backLeft);
        motors.add(backRight);
        motors.add(frontLeft);
        motors.add(frontRight);

        for (DcMotor m : motors) {
            m.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            m.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }

        colorSensor.enableLed(false);

        colorServo.setPosition(0);
        odsServo.setPosition(0);

    }

    @Override
    public void loop() {
        double lsy = -gamepad1.left_stick_y;
        double lsx = -gamepad1.left_stick_x;
        double rsy = gamepad1.right_stick_y;
        double rsx = -gamepad1.right_stick_x;
        float lt = gamepad1.left_trigger;
        float rt = gamepad1.right_trigger;
        boolean start = gamepad1.start;
        boolean back = gamepad1.back;
        boolean lb = gamepad1.left_bumper;
        boolean rb = gamepad1.right_bumper;

        if (lb) {
            if (colorServo.getPosition() < 1) {
                colorServo.setPosition(colorServo.getPosition() + 0.025);
            }
        }

        if (rb) {
            if (colorServo.getPosition() > 0) {
                colorServo.setPosition(colorServo.getPosition() - 0.025);
            }
        }

        if (lt > 0) {
            if (odsServo.getPosition() < 1) {
                odsServo.setPosition(odsServo.getPosition() + 0.025);
            }
        }

        if (rt > 0) {
            if (odsServo.getPosition() > 0) {
                odsServo.setPosition(odsServo.getPosition() - 0.025);
            }
        }

        if (start) {
            for (DcMotor m : motors) {
                m.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            }
        }

        if (back) {
            for (DcMotor m : motors) {
                m.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            }
        }

        if (!forward) {
            lsy = gamepad1.right_stick_y;
            lsx = gamepad1.right_stick_x;
            rsy = -gamepad1.left_stick_y;
            rsx = gamepad1.left_stick_x;
        }

        boolean down = gamepad1.dpad_down;
        boolean up = gamepad1.dpad_up;

        //Changes heading
        if (up) {
            forward = true;
        }

        if (down) {
            forward = false;
        }

        //Checks to see if horizontal axes are opposing, creating a stall
        if (Math.abs(lsx + rsx) <= 0.15) {
            lsx = 0;
            rsx = 0;
        }


        //Assigns each motor power. I know, it's inneficient.
        ArrayList<Double> wheels = new ArrayList<Double>();

        double bl = lsy + lsx;
        double br = rsy + rsx;
        double fl = lsy - lsx;
        double fr = rsy - rsx;

        wheels.add(bl);
        wheels.add(br);
        wheels.add(fl);
        wheels.add(fr);

        //Creates deadzone
        for (double i : wheels) {
            if (Math.abs(i) <= 0.1) {
                i = 0;
            }
        }

        //Makes sure no motor power exceeds maximum
        for (double i : wheels) {
            if (i > 1) {
                i = 1;
            }
            if (i < -1) {
                i = -1;
            }
        }

        backLeft.setPower(wheels.get(0));
        backRight.setPower(wheels.get(1));
        frontLeft.setPower(wheels.get(2));
        frontRight.setPower(wheels.get(3));

        //Sends telemetry for debugging
        telemetry.addData("Time", getRuntime());
        telemetry.addData("Distance", ods.getLightDetected());
        telemetry.addData("Distance Raw", ods.getRawLightDetected());
        telemetry.addData("Red", colorSensor.red());
        telemetry.addData("Green", colorSensor.green());
        telemetry.addData("Blue", colorSensor.blue());
        telemetry.addData("Alpha", colorSensor.alpha());
        telemetry.addData("Color Servo", colorServo.getPosition());
        telemetry.addData("ODS Servo", odsServo.getPosition());
        telemetry.addData("Light", lightSensor.getLightDetected());
        telemetry.addData("Raw Light", lightSensor.getRawLightDetected());
        telemetry.addData("Raw Max", lightSensor.getRawLightDetectedMax());

        for (DcMotor m : motors) {
            telemetry.addData(m.getDeviceName(), m.getPower());
        }

        telemetry.addData("Zero", frontLeft.getZeroPowerBehavior());

    }

}
