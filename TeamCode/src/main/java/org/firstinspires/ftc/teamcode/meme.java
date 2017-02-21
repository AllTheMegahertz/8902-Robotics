/*
Copyright (c) 2016 Robert Atkinson

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Robert Atkinson nor the names of his contributors may be used to
endorse or promote products derived from this software without specific prior
written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESSFOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.firstinspires.ftc.teamcode;

import android.media.MediaPlayer;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;

import java.util.ArrayList;

//Latest Revision - 01/18/2017 - Mark Vadeika

@TeleOp(name="Meme Mode", group="K9bot")

public class meme extends OpMode {

    private DcMotorController motorController;
    private DcMotor backLeft;
    private DcMotor backRight;
    private DcMotor frontLeft;
    private DcMotor frontRight;

    MediaPlayer moan;
    MediaPlayer guy;
    MediaPlayer song;

    boolean forward = true;

    ArrayList<MediaPlayer> sounds = new ArrayList();

    @Override
    public void init() {
        backLeft = hardwareMap.dcMotor.get("backLeft");
        backRight = hardwareMap.dcMotor.get("backRight");
        frontLeft = hardwareMap.dcMotor.get("frontLeft");
        frontRight = hardwareMap.dcMotor.get("frontRight");

        moan = MediaPlayer.create(hardwareMap.appContext, R.raw.moan);
        guy = MediaPlayer.create(hardwareMap.appContext, R.raw.guy);
        song = MediaPlayer.create(hardwareMap.appContext, R.raw.song);

        sounds.add(moan);
        sounds.add(guy);
        sounds.add(song);
    }

    HardwareK9bot robot = new HardwareK9bot();



    public meme() {
        super();
    }

    @Override
    public void loop() {

        double lsy = -gamepad1.left_stick_y;
        double lsx = -gamepad1.left_stick_x;
        double rsy = gamepad1.right_stick_y;
        double rsx = -gamepad1.right_stick_x;

        if (!forward) {
            lsy = gamepad1.right_stick_y;
            lsx = gamepad1.right_stick_x;
            rsy = -gamepad1.left_stick_y;
            rsx = gamepad1.left_stick_x;
        }

        boolean a = gamepad1.a;
        boolean x = gamepad1.x;
        boolean home = gamepad1.guide;
        boolean start = gamepad1.start;
        boolean down = gamepad1.dpad_down;
        boolean up = gamepad1.dpad_up;

        //Changes heading
        if (up) {
            forward = true;
        }

        if (down) {
            forward = false;
        }

        //Plays sounds
        if (a && !start) {
            moan.start();
        }

        if (x && !start) {
            guy.start();
        }

        if (home && !start) {
            song.start();
        }

        //Stops all sounds
        if (start) {
            for (MediaPlayer i : sounds) {
                i.stop();
                song = MediaPlayer.create(hardwareMap.appContext, R.raw.song);
                guy = MediaPlayer.create(hardwareMap.appContext, R.raw.guy);
                moan = MediaPlayer.create(hardwareMap.appContext, R.raw.moan);
            }
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

    }
}
