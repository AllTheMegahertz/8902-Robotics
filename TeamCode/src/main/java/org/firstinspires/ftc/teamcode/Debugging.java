package org.firstinspires.ftc.teamcode;

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
    private TouchSensor touchSensor;
    private OpticalDistanceSensor ods;
    private Servo servo;

    private boolean forward = true;

    private ArrayList<DcMotor> motors = new ArrayList<>();

    public void init() {

        //Initializes Hardware
        backLeft = hardwareMap.dcMotor.get("backLeft");
        backRight = hardwareMap.dcMotor.get("backRight");
        frontLeft = hardwareMap.dcMotor.get("frontLeft");
        frontRight = hardwareMap.dcMotor.get("frontRight");
        colorSensor = hardwareMap.colorSensor.get("colorSensor");
        touchSensor = hardwareMap.touchSensor.get("touchSensor");
        ods = hardwareMap.opticalDistanceSensor.get("distanceSensor");
        servo = hardwareMap.servo.get("servo");

        motors.add(backLeft);
        motors.add(backRight);
        motors.add(frontLeft);
        motors.add(frontRight);

        colorSensor.enableLed(false);

        servo.setPosition(0);

    }

    @Override
    public void loop() {
        double lsy = -gamepad1.left_stick_y;
        double lsx = -gamepad1.left_stick_x;
        double rsy = gamepad1.right_stick_y;
        double rsx = -gamepad1.right_stick_x;
        boolean start = gamepad1.start;
        boolean back = gamepad1.back;
        boolean lb = gamepad1.left_bumper;
        boolean rb = gamepad1.right_bumper;

        if (lb) {
            if (servo.getPosition() < 1) {
                servo.setPosition(servo.getPosition() + 0.05);
            }
        }

        if (rb) {
            if (servo.getPosition() > 0) {
                servo.setPosition(servo.getPosition() - 0.05);
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
        telemetry.addData("Pressed", touchSensor.isPressed());
        telemetry.addData("Distance", ods.getLightDetected());
        telemetry.addData("Red", colorSensor.red());
        telemetry.addData("Green", colorSensor.green());
        telemetry.addData("Blue", colorSensor.blue());
        telemetry.addData("Servo", servo.getPosition());

        for (DcMotor m : motors) {
            telemetry.addData(m.getDeviceName(), m.getPower());
        }

        telemetry.addData("Zero", frontLeft.getZeroPowerBehavior());

    }

}
