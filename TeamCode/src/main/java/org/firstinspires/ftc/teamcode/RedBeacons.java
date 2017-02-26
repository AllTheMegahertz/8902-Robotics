package org.firstinspires.ftc.teamcode;

import android.media.MediaPlayer;

import com.qualcomm.hardware.hitechnic.HiTechnicNxtColorSensor;
import com.qualcomm.hardware.hitechnic.HiTechnicNxtLightSensor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.ArrayList;

/**
 * Created by Mark on 2/21/2017.
 */
@Autonomous(name = "Red Beacon", group = "default")
public class RedBeacons extends LinearOpMode {

    private ColorSensor colorSensor;
    private OpticalDistanceSensor ods;

    private Servo colorServo;
    private Servo odsServo;

    private DcMotor backLeft;
    private DcMotor backRight;
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private ArrayList<DcMotor> motors = new ArrayList<>();

    //Variables
    private boolean pushed = false;
    private boolean last = false;
    private int direction;
    private double runningTime = 0;

    //Sensor vars
    private int r;
    private int g;
    private int b;
    private double distance;

    private void retract() {
        colorServo.setPosition(1);
        odsServo.setPosition(1);
    }

    private void extend() {
        colorServo.setPosition(0.125);
        odsServo.setPosition(0.3);
    }

    //Checks using the ODS for a wall
    private boolean wall() {

        return ods.getLightDetected() >= 0.005;

    }

    //Checks for the correct beacon color
    private boolean color() {

        int r = colorSensor.red();
        int b = colorSensor.blue();

        return r >= 3 && r > b;

    }

    //Checks if the color sensor returns nothing
    private boolean noColor() {

        r = colorSensor.red();
        g = colorSensor.green();
        b = colorSensor.blue();

        return r < 4 && b < 4;

    }

    private void resetTime() {
        runningTime += getRuntime();
        resetStartTime();
    }

    //Pushes beacon
    private void push() {

        //Gets sensors out of the way
        retract();

        if (!pushed) {
            pushed = true;
            resetTime();
        }


        //Moves forward for 0.5 seconds
        main.move(0, 0.2, motors);
        sleep(500);

        //Moves back
        main.move(1, 0.2, motors);
        sleep(200);

        //Restores servos
        extend();

        //Moves back until wall no longer detected
        while (wall()) {
            sensors();
            main.move(1, 0.1, motors);
        }

        if (!last) {
            main.move(3, 1, motors);
            sleep(500);
            main.move(0, 0, motors);
        }
    }

    //Acquires Sensor data
    private void sensors() {

        distance = ods.getLightDetected();
        r = colorSensor.red();
        g = colorSensor.green();
        b = colorSensor.blue();


        telemetry.addData("distance", distance);
        telemetry.addData("Red", r);
        telemetry.addData("Green", g);
        telemetry.addData("Blue", b);

    }

    private void goLeftForBeacon() {


        main.move(2, 0.2, motors);
        sleep(100);

        if (color()) {
            push();
        }
        else {
            main.move(3, 0.2, motors);
            sleep(200);
            if (color()) {
                push();
            }
        }

    }

    private void goRightForBeacon() {

        while (!color()) {
            sensors();

            if (!wall()) {
                main.move(0, 0.1, motors);
            }
            else {
                main.move(3, 0.2, motors);
            }
        }

        push();

    }

    public void runOpMode() {

        //Initialization Routine

        //Initializes Motors
        backLeft = hardwareMap.dcMotor.get("backLeft");
        backRight = hardwareMap.dcMotor.get("backRight");
        frontLeft = hardwareMap.dcMotor.get("frontLeft");
        frontRight = hardwareMap.dcMotor.get("frontRight");



        for (DcMotor m : motors) {
            m.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            m.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }

        //Initializes Servos
        colorServo = hardwareMap.servo.get("colorServo");
        odsServo = hardwareMap.servo.get("odsServo");

        //Initializes Sensors
        colorSensor = hardwareMap.colorSensor.get("colorSensor");
        ods = hardwareMap.opticalDistanceSensor.get("distanceSensor");

        //Adds all motors to a list
        motors.add(backLeft);
        motors.add(backRight);
        motors.add(frontLeft);
        motors.add(frontRight);

        //Turns off color sensor light
        colorSensor.enableLed(false);

        //Makes sure the sensors are retracted
        retract();

        resetStartTime();
        waitForStart();

        //Running Code

        //Acquires Sensor data
        sensors();

        //Starts from starting position, finds wall
        main.move(0, 1, motors);
        sleep(1500);
        main.turn(0, 1, motors);
        sleep(450);
        main.move(0, 1, motors);
        sleep(450);
        //Extends ODS
        extend();
        sleep(250);

        while (!wall()) {
            sensors();
            main.move(0, 0.2, motors);
        }

        main.move(0, 0, motors);

        resetTime();
        //Wall found, moves left until beacon is found, and pushes if it is the right color
        /*
        while (noColor() && !color() && !pushed) {
            sensors();

            if (!wall()) {
                main.move(0, 0.1, motors);
            }
            else {
                if (getRuntime() <= 1.5) {
                    main.move(3, 0.1, motors);
                    direction = 1;
                }
                else {
                    main.move(2, 0.1, motors);
                    direction = 0;
                }
            }

            if (color()) {
                push();
            }

        }

        //If the beacon hasn't been pushed, but the robot has found the beacon
        if (!pushed) {
            while (!pushed) {
                sensors();

                if (color()) {
                    push();
                }

                //Continues moving in the direction that it was prior
                if (direction == 0 && !pushed) {
                    main.move(2, 0.1, motors);
                }
                if (direction == 1 && !pushed) {
                    main.move(3, 0.1, motors);
                }

            }
        }
        */

        goRightForBeacon();

        //Resets, and prepares to push the next beacon
        pushed = false;

        //Moves to the left, staying the correct distance from the wall, and presses the beacon when it is in front of the correct color
        goRightForBeacon();
        pushed = false;

        //Turns the robot to go for the next two beacons
        main.turn(1, 1, motors);
        sleep(450);
        main.turn(0, 0, motors);

        //Makes sure 10 seconds have passed
        while (getRuntime() + runningTime <= 10);

        //Squares up against the wall
        retract();
        main.move(0, 0.5, motors);
        sleep(1500);

        main.move(1, 1, motors);
        sleep(300);

        extend();

        while (wall()) {
            main.move(1, 0.2, motors);
        }

        //Stores current time to know how long the robot must travel to come back

        //Ready to continue. Goes for first beacon on the right.
        goRightForBeacon();
        pushed = false;

        last = true;

        //Goes for second beacon on the right
        goRightForBeacon();

        //Retracts servos

        //Backs up for 0.75 seconds
        main.move(1, 1, motors);
        sleep(750);

        //Turns to the left
        main.turn(0, 1, motors);
        sleep(450);

        //Moves forward for 1 second
        main.move(0, 1, motors);
        sleep(1000);

        //Moves left for 0.5 seconds
        main.move(2, 1, motors);

        //Moves backward for 0.4 seconds, pushing the ball off of the center goal and parks on goal
        main.move(1, 1, motors);
        sleep(400);

        //Stops
        main.move(0, 0, motors);
    }

}