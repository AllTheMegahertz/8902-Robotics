package org.firstinspires.ftc.teamcode;

import android.media.MediaPlayer;

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
    private HiTechnicNxtLightSensor lightSensor;

    private Servo colorServo;
    private Servo odsServo;

    private DcMotor backLeft;
    private DcMotor backRight;
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private ArrayList<DcMotor> motors = new ArrayList<>();

    private MediaPlayer moan;
    private MediaPlayer guy;

    //Variables
    private boolean pushed = false;
    private int direction;
    private double runningTime = 0;

    //Sensor vars
    private int r;
    private int g;
    private int b;
    private double distance;

    //Checks using the ODS for a wall
    private boolean wall() {

        return ods.getLightDetected() >= 0.002;

    }

    //Checks for the correct beacon color
    private boolean color() {

        int r = colorSensor.red();
        int b = colorSensor.blue();

        return r >= 5 && r > b;

    }

    //Checks if the color sensor returns nothing
    private boolean noColor() {

        r = colorSensor.red();
        g = colorSensor.green();
        b = colorSensor.blue();

        return r < 5 && b < 5;

    }

    private void resetTime() {
        runningTime += getRuntime();
        resetStartTime();
    }

    //Pushes beacon
    private void push() {

        //Gets sensors out of the way
        odsServo.setPosition(1);
        colorServo.setPosition(1);

        moan.start();

        if (!pushed) {
            pushed = true;
            resetTime();
        }

        sleep(300);

        //Moves forward for 0.5 seconds
        main.move(0, 0.2, motors);
        sleep(500);

        //Moves back
        main.move(1, 0.2, motors);
        sleep(200);

        //Restores servos
        odsServo.setPosition(0.3);
        colorServo.setPosition(0.125);

        //Moves back until wall no longer detected
        while (wall()) {
            sensors();
            main.move(1, 0.1, motors);
        }

        sleep(100);
        main.move(0, 0, motors);


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

    //Uses light sensor to find tape on the ground
    private boolean light() {

        return lightSensor.getLightDetected() >= 0.3;

    }

    private void goLeftForBeacon() {

        while (!light()) {
            sensors();
            main.move(2, 0.2, motors);
        }

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

        while (!light()) {
            sensors();
            main.move(3, 0.2, motors);
        }

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

    public void runOpMode() {

        //Initialization Routine
        moan = MediaPlayer.create(hardwareMap.appContext, R.raw.moan);
        guy = MediaPlayer.create(hardwareMap.appContext, R.raw.guy);

        //Initializes Motors
        backLeft = hardwareMap.dcMotor.get("backLeft");
        backRight = hardwareMap.dcMotor.get("backRight");
        frontLeft = hardwareMap.dcMotor.get("frontLeft");
        frontRight = hardwareMap.dcMotor.get("frontRight");

        //Initializes Servos
        colorServo = hardwareMap.servo.get("colorServo");
        odsServo = hardwareMap.servo.get("odsServo");

        //Initializes Sensors
        colorSensor = hardwareMap.colorSensor.get("colorSensor");
        ods = hardwareMap.opticalDistanceSensor.get("distanceSensor");
        lightSensor = (HiTechnicNxtLightSensor) hardwareMap.get("lightSensor");

        //Adds all motors to a list
        motors.add(backLeft);
        motors.add(backRight);
        motors.add(frontLeft);
        motors.add(frontRight);

        //Turns off color sensor light
        colorSensor.enableLed(false);

        //Makes sure the sensors are retracted
        colorServo.setPosition(1);
        odsServo.setPosition(1);

        resetStartTime();
        waitForStart();

        //Running Code

        //Acquires Sensor data
        sensors();

        //Starts from starting position, finds wall
        main.move(0, 1, motors);
        sleep(1300);
        main.turn(0, 1, motors);
        sleep(450);
        main.move(0, 1, motors);
        sleep(450);
        //Extends ODS
        odsServo.setPosition(0.3);
        sleep(250);

        while (!wall()) {
            sensors();
            main.move(0, 0.2, motors);
        }

        main.move(0, 0, motors);

        //Extends color sensor
        colorServo.setPosition(0.125);
        sleep(100);

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

        goLeftForBeacon();

        //Resets, and prepares to push the next beacon
        pushed = false;
        main.move(1, 0.5, motors);
        sleep(300);

        //Moves to the left, staying the correct distance from the wall, and presses the beacon when it is in front of the correct color
        goRightForBeacon();
        pushed = false;

        //Turns the robot to go for the next two beacons
        guy.start();
        main.turn(0, 1, motors);
        sleep(450);
        main.turn(0, 0, motors);

        while (!wall()) {

            main.move(0, 0.2, motors);

        }

        main.move(0, 0, motors);

        //Makes sure 10 seconds have passed
        while (getRuntime() + runningTime <= 10);

        //Stores current time to know how long the robot must travel to come back
        double time = getRuntime();

        //Ready to continue. Goes for first beacon on the right.
        goRightForBeacon();
        pushed = false;

        //Goes for second beacon on the right
        goRightForBeacon();

        double time2 = (getRuntime() - time) * 100;

        long timeL = (long) time2;

        //Comes back to where robot was before previous two beacons
        main.move(2, 1, motors);
        sleep(timeL);

        //Moves back to near the first beacon
        main.move(1, 1, motors);
        sleep((long) (time2*0.75));

        //Move in front of the center goal
        main.move(3, 1, motors);
        sleep((long) (time2/2));

        //Move the ball and park on the goal
        odsServo.setPosition(1);
        colorServo.setPosition(1);
        main.move(0, 1, motors);
        sleep(500);
        main.move(0, 0, motors);
    }

}