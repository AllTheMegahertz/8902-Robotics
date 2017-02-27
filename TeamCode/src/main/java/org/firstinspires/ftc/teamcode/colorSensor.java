package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.teamcode.main;

import java.util.ArrayList;


/**
 * Created by robotics on 11/17/16.
 */
@Disabled
@Autonomous(name = "Sensor: MR Color", group = "Autonomous")
public class colorSensor extends OpMode {

    private ColorSensor colorSensor;
    private Servo servo;
    private DcMotor backLeft;
    private DcMotor backRight;
    private DcMotor frontLeft;
    private DcMotor frontRight;
    ArrayList<DcMotor> motors = new ArrayList<>();

    boolean found = false;

    @Override
    public void init() {
        colorSensor = hardwareMap.colorSensor.get("colorSensor");
        colorSensor.enableLed(false);
        servo = hardwareMap.servo.get("servo");
        servo.setPosition(0.5);

        motors.add(backLeft);
        motors.add(backRight);
        motors.add(frontLeft);
        motors.add(frontRight);
    }

    @Override
    public void loop() {
        int r = colorSensor.red();
        int g = colorSensor.green();
        int b = colorSensor.blue();

        telemetry.addData("Red  ", colorSensor.red());
        telemetry.addData("Green", colorSensor.green());
        telemetry.addData("Blue ", colorSensor.blue());

        if (found) {
            resetStartTime();
            while (getRuntime() <= 1.5) {
                main.move(0, 1, motors);
            }
            main.move(0, 0, motors);
        }

        if (r > b && r > g/2 && r > 4 && b < 15) {
            found = true;
            servo.setPosition(1.0);
        }

    }

}

