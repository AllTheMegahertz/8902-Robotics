package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IrSeekerSensor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.main;

import java.util.ArrayList;

/**
 * Created by Mark on 2/18/2017.
 */


@Autonomous(name = "Push dat bekun", group = "default")
public class AutoBeacons extends OpMode {

    private ColorSensor colorSensor;
    private IrSeekerSensor irSensor;
    private OpticalDistanceSensor distanceSensor;

    private DcMotor backLeft;
    private DcMotor backRight;
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private Servo servo;
    private ArrayList<DcMotor> motors = new ArrayList<>();

    boolean reset = true;

    //Initialization Routine
    public void init() {

        //Initializes Hardware
        backLeft = hardwareMap.dcMotor.get("backLeft");
        backRight = hardwareMap.dcMotor.get("backRight");
        frontLeft = hardwareMap.dcMotor.get("frontLeft");
        frontRight = hardwareMap.dcMotor.get("frontRight");
        colorSensor = hardwareMap.colorSensor.get("colorSensor");
        irSensor = hardwareMap.irSeekerSensor.get("irSensor");
        distanceSensor = hardwareMap.opticalDistanceSensor.get("distanceSensor");
        servo = hardwareMap.servo.get("servo");

        //Adds all motors to a list
        motors.add(backLeft);
        motors.add(backRight);
        motors.add(frontLeft);
        motors.add(frontRight);

        //Makes sure color sensor is out of the way
        servo.setPosition(0.0);
    }

    //Turns robot at given speed and direction
    public void turn(int d, double p, ArrayList<DcMotor> m) {

        if (d == 0) {
            d = -1;
        }

        m.get(0).setPower(p*d);
        m.get(1).setPower(p*d);
        m.get(2).setPower(p*d);
        m.get(3).setPower(p*d);
    }

    public void push(int r, int g, int b) {

        //Checks to see if the color sensor is extended, and that it sees red
        if (servo.getPosition() >= 0.55 && servo.getPosition() <= 0.65 && r > g && r > b && r > 15) {
            servo.setPosition(0.0);
            resetStartTime();
            while (getRuntime() > 1.5) {
                main.move(0, 0.2, motors);
            }
            main.move(0, 0, motors);
        }
    }

    public void loop() {

        if (reset) {
            resetStartTime();
            reset = false;
        }

        double time = getRuntime();

        if (time <= 2.4) {
            main.move(0, 1, motors);
        }

        if (time >= 2.5 && time <= 3.0) {
            turn(0, 1, motors);
        }

        if (time >= 3.3 && time <= 5.0) {
            main.move(0, 0.2, motors);
        }
        if (time > 5.0) {
            main.move(0, 0, motors);
        }

        //Acquires sensor data
        double angle = irSensor.getAngle();
        double strength = irSensor.getStrength();
        double distance = distanceSensor.getLightDetected();
        int r = colorSensor.red();
        int g = colorSensor.green();
        int b = colorSensor.blue();

        /*
        //Turns the robot to face the IR source, and moves forward when it is facing it
        if (angle >= 5 && angle < 180) {
            turn(1, 0.2, motors);
        }
        else if (angle < 354 && angle >= 180) {
            turn(0, 0.2, motors);
        }
        else if (distance == 500) {
            servo.setPosition(1.0);
            push(r, g, b);
        }
        else {
            main.move(0, 1, motors);
        }
        */

        //Sends telemetry for debugging
        telemetry.addData("IR Angle", angle);
        telemetry.addData("IR Strength", strength);
        telemetry.addData("IR Threshold", irSensor.getSignalDetectedThreshold());
        telemetry.addData("Distance", distance);
        telemetry.addData("Red", r);
        telemetry.addData("Green", g);
        telemetry.addData("Blue", b);
        telemetry.addData("Servo", servo.getPosition());
    }
}
