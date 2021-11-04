package org.firstinspires.ftc.teamcode;

import com.qualcomm.ftccommon.SoundPlayer;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class Scream extends LinearOpMode {



    @Override
    public void runOpMode() {

        int soundID = hardwareMap.appContext.getResources().getIdentifier("scream.mp3", "raw",
                hardwareMap.appContext.getPackageName());

        waitForStart();

        SoundPlayer.getInstance().startPlaying(hardwareMap.appContext, soundID);

    }

}
