package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;

import java.util.ArrayList;

/**
 * Created by robotics on 2/17/17.
 */

public class main {

    //Turns robot at given speed and direction
    public static void turn(int d, double p, ArrayList<DcMotor> m) {

        if (d == 0) {
            d = -1;
        }

        m.get(0).setPower(p*d);
        m.get(1).setPower(p*d);
        m.get(2).setPower(p*d);
        m.get(3).setPower(p*d);
    }

    //Moves the robot at a given speed and direction
    public static void move(int d, double p, ArrayList<DcMotor> m) {

        if (d == 0) {
            d = -1;
        }

        //Goes Left
        if (d == 2) {
            d = -1;

            m.get(0).setPower(-p*d);
            m.get(1).setPower(-p*d);
            m.get(2).setPower(p*d);
            m.get(3).setPower(p*d);

        }

        //Goes Right
        else if (d == 3) {
            d = -1;

            m.get(0).setPower(p*d);
            m.get(1).setPower(p*d);
            m.get(2).setPower(-p*d);
            m.get(3).setPower(-p*d);

        }

        else {

            m.get(0).setPower(-p * d);
            m.get(1).setPower(p * d);
            m.get(2).setPower(-p * d);
            m.get(3).setPower(p * d);
        }

    }

}
