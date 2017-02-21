package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.main;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by Mark on 2/21/2017.
 */
@Autonomous(name = "Blue Beacon", group = "default")
public class BlueBeacons extends LinearOpMode {

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
    private int direction;
    private double runningTime = 0;

    //Sensor vars
    private int r;
    private int g;
    private int b;
    private double distance;

    //Checks using the ODS for a wall
    public boolean wall() {

        if (ods.getLightDetected() >= 0.05) {
            return true;
        }
        else {
            return false;
        }

    }

    //Checks for the correct beacon color
    public boolean color() {

        int r = colorSensor.red();
        int b = colorSensor.blue();

        if (b >= 2 && b > r) {
            return true;
        }
        else {
            return false;
        }

    }

    //Checks if the color sensor returns nothing
    public boolean noColor() {

        r = colorSensor.red();
        g = colorSensor.green();
        b = colorSensor.blue();

        if (r + g + b == 0) {
            return true;
        }
        else {
            return false;
        }

    }

    public void resetTime() {
        runningTime += getRuntime();
        resetStartTime();
    }

    //Pushes beacon
    public void push() {

        sensors();

        if (!pushed) {
            pushed = true;
            resetTime();
        }

        //Gets sensors out of the way
        odsServo.setPosition(0.5);
        colorServo.setPosition(0.5);

        //Moves forward for 0.5 seconds
        main.move(0, 0.1, motors);
        sleep(500);

        //Moves back until wall no longer detected
        while (wall()) {
            sensors();
            main.move(1, 0.1, motors);
        }

        //Moves forward until wall detected, plus 0.1 seconds
        while (!wall()) {
            sensors();
            main.move(0, 0.1, motors);
        }

        //Restores servos
        odsServo.setPosition(1.0);
        colorServo.setPosition(1.0);

        sleep(100);
        main.move(0, 0, motors);


    }

    //Acquires Sensor data
    public void sensors() {

        distance = ods.getLightDetected();
        r = colorSensor.red();
        g = colorSensor.green();
        b = colorSensor.blue();

    }

    public void goLeftForBeacon() {

        while (!color()) {
            sensors();

            if (color()) {
                push();
            }

            main.move(2, 0.1, motors);
        }

    }

    public void runOpMode() {

        //Initialization Routine

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

        //Adds all motors to a list
        motors.add(backLeft);
        motors.add(backRight);
        motors.add(frontLeft);
        motors.add(frontRight);

        //Turns off color sensor light
        colorSensor.enableLed(false);

        //Makes sure the sensors are retracted
        colorServo.setPosition(0.5);
        odsServo.setPosition(0.5);

        resetTime();
        waitForStart();

        //Running Code

        //Acquires Sensor data
        sensors();

        //Starts from starting position, finds wall
        main.move(0, 1, motors);
        sleep(1300);
        main.turn(1, 1, motors);
        sleep(475);
        main.move(0, 1, motors);
        sleep(450);
        //Extends ODS
        odsServo.setPosition(1.0);

        while (!wall()) {
            sensors();
            main.move(0, 0.1, motors);
        }

        //Extends color sensor
        colorServo.setPosition(1.0);

        resetTime();
        //Wall found, moves left until beacon is found, and pushes if it is the right color

        while (!color() && noColor()) {
            sensors();

            if (color()) {
                push();
            }
            else if (!wall()) {
                main.move(0, 0.1, motors);
            }
            else {
                if (getRuntime() < 1) {
                    main.move(2, 0.1, motors);
                    direction = 0;
                }
                else {
                    main.move(3, 0.1, motors);
                    direction = 1;
                }
            }
        }

        //If the beacon hasn't been pushed, but the robot has found the beacon
        if (!pushed) {
            while (!color()) {
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

        //Resets, and prepares to push the next beacon
        pushed = false;
        main.move(2, 0.5, motors);
        sleep(300);

        //Moves to the left, staying the correct distance from the wall, and presses the beacon when it is in front of the correct color
        goLeftForBeacon();
        pushed = false;

        //Turns the robot to go for the next two beacons
        main.turn(0, 1, motors);
        sleep(475);
        main.turn(0, 0, motors);

        //Makes sure 10 seconds have passed
        while (getRuntime() + runningTime <= 10) {
            return;
        }

        //Stores current time to know how long the robot must travel to come back
        double time = getRuntime();

        //Ready to continue. Goes for first beacon on the right.
        goLeftForBeacon();
        pushed = false;

        //Goes for second beacon on the right
        goLeftForBeacon();

        double time2 = (getRuntime() - time) * 100;

        long timeL = (long) time2;

        //Comes back to where robot was before previous two beacons
        main.move(3, 1, motors);
        sleep(timeL);

        //Moves back to near the first beacon
        main.move(1, 1, motors);
        sleep((long) (time2*0.75));

        //Move in front of the center goal
        main.move(2, 1, motors);
        sleep((long) (time2/2));

        //Move the ball and park on the goal
        odsServo.setPosition(0.5);
        colorServo.setPosition(0.5);
        main.move(0, 1, motors);
        sleep(500);
        main.move(0, 0, motors);

    }

}