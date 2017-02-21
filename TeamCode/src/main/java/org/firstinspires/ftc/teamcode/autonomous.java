package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;

import org.firstinspires.ftc.teamcode.main;

import java.util.ArrayList;

//Last revision - 01/10/2017 - Mark Vadeika

@Autonomous(name="Drive Forward 2.6s", group="Autonomous")

public class autonomous extends OpMode {

    private DcMotorController motorController;
    private DcMotor backLeft;
    private DcMotor backRight;
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private boolean wait = false;
    private boolean first = false;
    private ArrayList<DcMotor> motors = new ArrayList<>();


    @Override
    public void init() {
        backLeft = hardwareMap.dcMotor.get("backLeft");
        backRight = hardwareMap.dcMotor.get("backRight");
        frontLeft = hardwareMap.dcMotor.get("frontLeft");
        frontRight = hardwareMap.dcMotor.get("frontRight");

        motors.add(backLeft);
        motors.add(backRight);
        motors.add(frontLeft);
        motors.add(frontRight);
    }

    public void loop() {

        //Resets timer if it is the first time running
        if (!first) {
            first = true;
            resetStartTime();
        }

        //Waits for 10 seconds before starting
        if (!wait) {
            if (getRuntime() >= 10) {
                wait = true;
            }
            return;
        }

        //Runs motors for 2.6 seconds
        if (getRuntime() <= 12.6) {
            main.move(1, 1.0, motors);
        }

        //Stops motors
        else {
            main.move(1, 0.0, motors);
        }

    }
}