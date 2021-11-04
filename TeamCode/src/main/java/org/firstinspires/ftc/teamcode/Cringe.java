package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;

@Autonomous(name = "cringe", group = "Concept")

public class Cringe extends LinearOpMode {
    private static final String TFOD_MODEL_ASSET = "UltimateGoal.tflite";
    private static final String LABEL_FIRST_ELEMENT = "Quad";
    private static final String LABEL_SECOND_ELEMENT = "Single";

    //Define motors
    //wheels
    private DcMotorEx front_left_motor;
    private DcMotorEx front_right_motor;
    private DcMotorEx back_left_motor;
    private DcMotorEx back_right_motor;
    //Appliances
    private DcMotorEx left_shooty_motor;
    private DcMotorEx weed_wacker;
    private DcMotorEx lift_claw;

    //Define servos
    private Servo conveyor;
    private Servo open_claw;
    private Servo inServo;

    //Other Garbage (just like us)
    int ring_number = 0;

    //Move forwards or backwards to a given target at a given power
    public void FDrive(double power, int target) throws InterruptedException{
        //Stops and resets encoders
        back_right_motor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        front_right_motor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        back_left_motor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        front_left_motor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);

        //Sets the target position
        back_right_motor.setTargetPosition(-target);
        front_right_motor.setTargetPosition(-target);
        back_left_motor.setTargetPosition(target);
        front_left_motor.setTargetPosition(target);

        //Runs the motors to the positions
        back_right_motor.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
        front_right_motor.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
        back_left_motor.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
        front_left_motor.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);

        //Start the motors
        back_right_motor.setPower(power);
        front_right_motor.setPower(power);
        back_left_motor.setPower(power);
        front_left_motor.setPower(power);

        //Defines the variables to see if the motors are running
        Boolean BRRunning = true;
        Boolean FRRunning = true;
        Boolean BLRunning = true;
        Boolean FLRunning = true;

        //Loop until all motors stopped
        while(BRRunning || FRRunning || BLRunning || FLRunning) {

            //Stops motors when they reach their respective targets
            if (Math.abs(back_right_motor.getCurrentPosition()) >= Math.abs(target) - 5) {
                back_right_motor.setPower(0);
                BRRunning = false;
            }
            if (Math.abs(front_right_motor.getCurrentPosition()) >= Math.abs(target) - 5) {
                front_right_motor.setPower(0);
                FRRunning = false;
            }
            if (Math.abs(back_left_motor.getCurrentPosition()) >= Math.abs(target) - 5) {
                back_left_motor.setPower(0);
                BLRunning = false;
            }
            if (Math.abs(front_left_motor.getCurrentPosition()) >= Math.abs(target) - 5) {
                front_left_motor.setPower(0);
                FLRunning = false;
            }

            // *sings* Telemetry!
            telemetry.addData("speed: ", power);
            telemetry.addData("target: ", target);
            telemetry.addData("BRRunning: ", BRRunning);
            telemetry.addData("FRRunning: ", FRRunning);
            telemetry.addData("BLRunning: ", BLRunning);
            telemetry.addData("FLRunning: ", FLRunning);
            telemetry.update();
        }

        //Give the motors time to rest
        sleep(100);
    }

    //Move left or right at a given power to a given target
    public void SDrive(double power, int target) throws InterruptedException{

        //Stops and resets encoders
        back_right_motor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        front_right_motor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        back_left_motor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        front_left_motor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);

        //Sets the target position
        back_right_motor.setTargetPosition(-target);
        front_right_motor.setTargetPosition(target);
        back_left_motor.setTargetPosition(-target);
        front_left_motor.setTargetPosition(target);

        //Runs the motors to the positions
        back_right_motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        front_right_motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        back_left_motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        front_left_motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        //Start the motors
        back_right_motor.setPower(power);
        front_right_motor.setPower(power);
        back_left_motor.setPower(power);
        front_left_motor.setPower(power);

        //Defines the variables to see if the motors are running
        Boolean BRRunning = true;
        Boolean FRRunning = true;
        Boolean BLRunning = true;
        Boolean FLRunning = true;

        //Loop until all motors stopped
        while(BRRunning || FRRunning || BLRunning || FLRunning) {

            //Stops motors when they reach their respective targets
            if (Math.abs(back_right_motor.getCurrentPosition()) >= Math.abs(target) - 5) {
                back_right_motor.setPower(0);
                BRRunning = false;
            }
            if (Math.abs(front_right_motor.getCurrentPosition()) >= Math.abs(target) - 5) {
                front_right_motor.setPower(0);
                FRRunning = false;
            }
            if (Math.abs(back_left_motor.getCurrentPosition()) >= Math.abs(target) - 5) {
                back_left_motor.setPower(0);
                BLRunning = false;
            }
            if (Math.abs(front_left_motor.getCurrentPosition()) >= Math.abs(target) - 5) {
                front_left_motor.setPower(0);
                FLRunning = false;
            }

            // *sings* Telemetryyyyyyy!
            telemetry.addData("speed: ", power);
            telemetry.addData("target: ", target);
            telemetry.addData("BRRunning: ", BRRunning);
            telemetry.addData("FRRunning: ", FRRunning);
            telemetry.addData("BLRunning: ", BLRunning);
            telemetry.addData("FLRunning: ", FLRunning);
            telemetry.addData("target:", target);
            telemetry.update();
        }

        sleep(100);
    }

    //Move forwards
    public void Turn(double power, int target) throws InterruptedException{
        //Stops and resets encoders
        back_right_motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        front_right_motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        back_left_motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        front_left_motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //Sets the target position
        back_right_motor.setTargetPosition(target);
        front_right_motor.setTargetPosition(target);
        back_left_motor.setTargetPosition(target);
        front_left_motor.setTargetPosition(target);

        //Runs the motors to the positions
        back_right_motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        front_right_motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        back_left_motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        front_left_motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        //Start the motors
        back_right_motor.setPower(power);
        front_right_motor.setPower(power);
        back_left_motor.setPower(power);
        front_left_motor.setPower(power);

        //Defines the variables to see if the motors are running
        Boolean BRRunning = true;
        Boolean FRRunning = true;
        Boolean BLRunning = true;
        Boolean FLRunning = true;

        //Loop until all motors stopped
        while(BRRunning || FRRunning || BLRunning || FLRunning) {

            //Stops motors when they reach their respective targets
            if (Math.abs(back_right_motor.getCurrentPosition()) >= Math.abs(target) - 5) {
                back_right_motor.setPower(0);
                BRRunning = false;
            }
            if (Math.abs(front_right_motor.getCurrentPosition()) >= Math.abs(target) - 5) {
                front_right_motor.setPower(0);
                FRRunning = false;
            }
            if (Math.abs(back_left_motor.getCurrentPosition()) >= Math.abs(target) - 5) {
                back_left_motor.setPower(0);
                BLRunning = false;
            }
            if (Math.abs(front_left_motor.getCurrentPosition()) >= Math.abs(target) - 5) {
                front_left_motor.setPower(0);
                FLRunning = false;
            }

            // *sings* Telemetryyyyyyy!
            telemetry.addData("speed: ", power);
            telemetry.addData("target: ", target);
            telemetry.addData("BRRunning: ", BRRunning);
            telemetry.addData("FRRunning: ", FRRunning);
            telemetry.addData("BLRunning: ", BLRunning);
            telemetry.addData("FLRunning: ", FLRunning);
            telemetry.addData("target:", target);
            telemetry.update();

        }

        sleep(100);

    }

    //Runs the flywheel
    public void PewPew(double velocity){
        left_shooty_motor.setVelocity(-velocity);
    }

    //Runs the conveyor
    public void Conveyor(double position){
        conveyor.setPosition(position);
    }

    //Runs the intake
    public void Intake(double power){
        weed_wacker.setPower(power);
    }

    //Raises or lowers the claw
    public void LiftClaw(double power, int time) {
        lift_claw.setPower(power);
        sleep(time);
        lift_claw.setPower(0);

    }

    //Opens or closes the claw
    public void OpenClaw(double position) {
        open_claw.setPosition(position);
    }

//#################WARNING:EVERYTHING BELOW THIS POINT IS DEATH#############################3

    private static final String VUFORIA_KEY =
            "AbskhHb/////AAABmb8nKWBiYUJ9oEFmxQL9H2kC6M9FzPa1acXUaS/H5wRkeNbpNVBJjDfcrhlTV2SIGc/lxBOtq9X7doE2acyeVOPg4sP69PQQmDVQH5h62IwL8x7BS/udilLU7MyX3KEoaFN+eR1o4FKBspsYrIXA/Oth+TUyrXuAcc6bKSSblICUpDXCeUbj17KrhghgcgxU6wzl84lCDoz6IJ9egO+CG4HlsBhC/YAo0zzi82/BIUMjBLgFMc63fc6eGTGiqjCfrQPtRWHdj2sXHtsjZr9/BpLDvFwFK36vSYkRoSZCZ38Fr+g3nkdep25+oEsmx30IkTYvQVMFZKpK3WWMYUWjWgEzOSvhh+3BOg+3UoxBJSNk";

    private VuforiaLocalizer vuforia;

    private TFObjectDetector tfod;

    @Override
    public void runOpMode() throws InterruptedException {
        // The TFObjectDetector uses the camera frames from the VuforiaLocalizer, so we create that
        // first.
        initVuforia();

        // Hardware mapping
        //Wheels
        front_left_motor = hardwareMap.get(DcMotorEx.class, "front_left_motor");
        front_right_motor = hardwareMap.get(DcMotorEx.class, "front_right_motor");
        back_left_motor = hardwareMap.get(DcMotorEx.class, "back_left_motor");
        back_right_motor = hardwareMap.get(DcMotorEx.class, "back_right_motor");
        //Other motors
        left_shooty_motor = hardwareMap.get(DcMotorEx.class, "left_shooty_motor");
        weed_wacker = hardwareMap.get(DcMotorEx.class, "weed_wacker");
        conveyor = hardwareMap.get(Servo.class, "conveyor");
        lift_claw = hardwareMap.get(DcMotorEx.class, "lift_claw");
        //Servos
        open_claw = hardwareMap.get(Servo.class, "open_claw");
        inServo = hardwareMap.get(Servo.class, "inServo");

        if (tfod != null) {
            tfod.activate();

        }

        // Wait for the game to begin
        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {
            int x = 0;
            while (x < 4000) {
                x++;
                if (tfod != null) {
                    // getUpdatedRecognitions() will return null if no new information is available since
                    // the last time that call was made.
                    List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                    if (updatedRecognitions != null) {
                        telemetry.addData("# Object Detected", updatedRecognitions.size());

                        // step through the list of recognitions and display boundary info.
                        int i = 0;
                        for (Recognition recognition : updatedRecognitions) {
                            telemetry.addData(String.format("label (%d)", i), recognition.getLabel());
                            telemetry.addData(String.format("  left,top (%d)", i), "%.03f , %.03f",
                                    recognition.getLeft(), recognition.getTop());
                            telemetry.addData(String.format("  right,bottom (%d)", i), "%.03f , %.03f",
                                    recognition.getRight(), recognition.getBottom());

                            if (recognition.getLabel().equals(LABEL_FIRST_ELEMENT)) {
                                ring_number = 4;
                                tfod.shutdown();
                            }

                            else if (recognition.getLabel().equals(LABEL_SECOND_ELEMENT)) {
                                ring_number = 1;
                                tfod.shutdown();
                            }

                            else {
                                ring_number = 0;
                            }
                            telemetry.addData("Ring number: ", ring_number);
                            telemetry.update();
                        }
                    }
                }
            }

            if (tfod != null) {
                tfod.shutdown();
            }

            LiftClaw(0.5, 2000);
            sleep(1000);
            OpenClaw(1);
            sleep(1000);
            FDrive(1, 1400);
            Turn(1, -500);
            FDrive(1, 2600);
            Turn(1, 600);
            FDrive(1, 200);
            SDrive(1, -400);
            Turn(1, -175);
            PewPew(1450);
            OpenClaw(1);
            sleep(4000);
            Conveyor(1);
            sleep(1200);
            Turn(1, 125);
            sleep(1200);
            Turn(1, 125);
            sleep(1200);
            PewPew(0);

            if (ring_number == 4) {
                //crappy quad ring code goes here
                Turn(1, 350);
                FDrive(1, 4800);
                Turn(1, 300);
                sleep(1000);
                OpenClaw(0);
                sleep(1000);
                LiftClaw(-0.5, 750);
                sleep(750);
                FDrive(1, -4500);
            }

            else if (ring_number == 1) {
                //crappy single ring code goes here
                Turn(1, 250);
                FDrive(1, 2500);
                sleep(1000);
                OpenClaw(0);
                sleep(1000);
                LiftClaw(-0.5, 750);
                sleep(750);
                FDrive(1, -2000);
            }

            else {
                //Crappy no ring code goes here
                Turn(1, 900);
                FDrive(1, 2500);
                sleep(1000);
                OpenClaw(0);
                sleep(1000);
                LiftClaw(-0.5, 750);
                SDrive(1, 300);
                Turn(1, 300);
                FDrive(1, -2000);
            }

            telemetry.addData("Ring number: ", ring_number);
            telemetry.update();
        }

    }

    private void initVuforia() {

        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the TensorFlow Object Detection engine.
    }
}