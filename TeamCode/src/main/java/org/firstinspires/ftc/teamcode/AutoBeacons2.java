package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import java.util.ArrayList;

/**
 * Created by Mark on 2/18/2017.
 */

@Autonomous(name = "Auto Beacons", group = "default")
public class AutoBeacons2 extends OpMode {

    private ColorSensor colorSensor;
    //private OpticalDistanceSensor distanceSensor;
    private TouchSensor touchSensor;

    private DcMotor backLeft;
    private DcMotor backRight;
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private Servo servo;
    private ArrayList<DcMotor> motors = new ArrayList<>();

    private boolean first = true;
    private boolean pushed = false;
    private boolean found = false;
    private boolean push = false;

    //Initialization Routine
    public void init() {

        //Initializes Hardware
        backLeft = hardwareMap.dcMotor.get("backLeft");
        backRight = hardwareMap.dcMotor.get("backRight");
        frontLeft = hardwareMap.dcMotor.get("frontLeft");
        frontRight = hardwareMap.dcMotor.get("frontRight");
        colorSensor = hardwareMap.colorSensor.get("colorSensor");
        touchSensor = hardwareMap.touchSensor.get("touchSensor");
        //distanceSensor = hardwareMap.opticalDistanceSensor.get("distanceSensor");
        servo = hardwareMap.servo.get("servo");

        //Adds all motors to a list
        motors.add(backLeft);
        motors.add(backRight);
        motors.add(frontLeft);
        motors.add(frontRight);

        //Turns off color sensor light
        colorSensor.enableLed(false);

        //Makes sure color sensor is out of the way
        servo.setPosition(0.0);
    }

    public boolean check() {
        if (pushed && !found && !push) {
            return true;
        }
        else {
            return false;
        }
    }

    //Pushes Button
    public void push() {
        if (getRuntime() <= 1) {
            main.move(0, 0.2, motors);
        }
        else if (getRuntime() <= 2) {
            main.move(1, 0.2, motors);
        }
        else {
            return;
        }
    }

    public void loop() {

        //Resets timer on first run
        if (first) {
            resetStartTime();
            first = false;
        }

        //Acquires sensor data
        //double distance = distanceSensor.getLightDetected();
        boolean touch = touchSensor.isPressed();
        int r = colorSensor.red();
        int g = colorSensor.green();
        int b = colorSensor.blue();

        //Moves forward and finds the wall
        if (getRuntime() <= 1.3 && !pushed) {
            main.move(0, 1, motors);
        }
        else if (getRuntime() <= 1.75 && !pushed) {
            main.turn(0, 1, motors);
        }
        else if (getRuntime() <= 3.0 && !pushed) {
            main.move(0, 1, motors);
        }
        else if (!touch && !pushed) {
            main.move(0, 0.1, motors);
        }
        else {
            if (touch) {
                pushed = true;
                resetStartTime();
                main.move(1, 0.1, motors);
            }
            else if (getRuntime() <= 0.2 && check()) {
                main.move(1, 1, motors);
            }
            else if (getRuntime() <= 0.5 && check()) {
                main.turn(1, 1, motors);
            }
            else if (getRuntime() <= 2 && check()) {
                main.move(3, 0.1, motors);
            }
            else if (getRuntime() <= 2.1 && check()) {
                main.move(0, 0.1, motors);
            }
            else if (touch && check()) {
                main.move(1, 0.1, motors);
                found = true;
                resetStartTime();
            }
            else if (getRuntime() <= 0.2 && found && !push) {
                main.move(2, 1, motors);
            }
            else if (getRuntime() <= 0.5 && found && !push) {
                main.turn(1, 1, motors);
            }
            else if (found && !push) {
                if (r > g/2 && r > b && r > 13) {
                    servo.setPosition(0.0);
                    push = true;
                    resetStartTime();
                }
                else {
                    servo.setPosition(1.0);
                    main.move(2, 0.2, motors);
                }
            }
            else if (push) {
                push();
            }

        }

        //Sends telemetry for debugging
        telemetry.addData("Time", getRuntime());
        telemetry.addData("Pressed", touch);
        //telemetry.addData("Distance", distance);
        telemetry.addData("Red", r);
        telemetry.addData("Green", g);
        telemetry.addData("Blue", b);
        telemetry.addData("Servo", servo.getPosition());
        for (DcMotor m : motors) {
            telemetry.addData(m.getDeviceName(), m.getPower());
        }
    }
}
