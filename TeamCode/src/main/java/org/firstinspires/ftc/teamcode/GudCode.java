package org.firstinspires.ftc.teamcode;

import android.icu.util.IslamicCalendar;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.configuration.ServoFlavor;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;

@TeleOp(name = "GudCode", group = "Stuff")

public class GudCode extends LinearOpMode {
    private static final String TFOD_MODEL_ASSET = "UltimateGoal.tflite";
    private static final String LABEL_FIRST_ELEMENT = "Quad";
    private static final String LABEL_SECOND_ELEMENT = "Single";

    // Chassis motors
    private DcMotorEx bob;
    private DcMotorEx dylan;
    private DcMotorEx larry;
    private DcMotorEx jerry;

    // Arm motors/servos
    private DcMotorEx barry;
    private Servo garry;

    // Other stuff
    private double leftJoy_y;
    private double rightJoy_y;
    private double leftJoy_x;
    private double rightJoy_x;

    private boolean y_flag;

    private enum ClawPosition
    {
        UP, DOWN, START
    }

    ClawPosition currentPosition = ClawPosition.START;

//#################WARNING:EVERYTHING BELOW THIS POINT IS DEATH #####3

    private static final String VUFORIA_KEY =
            "AbskhHb/////AAABmb8nKWBiYUJ9oEFmxQL9H2kC6M9FzPa1acXUaS/H5wRkeNbpNVBJjDfcrhlTV2SIGc/lxBOtq9X7doE2acyeVOPg4sP69PQQmDVQH5h62IwL8x7BS/udilLU7MyX3KEoaFN+eR1o4FKBspsYrIXA/Oth+TUyrXuAcc6bKSSblICUpDXCeUbj17KrhghgcgxU6wzl84lCDoz6IJ9egO+CG4HlsBhC/YAo0zzi82/BIUMjBLgFMc63fc6eGTGiqjCfrQPtRWHdj2sXHtsjZr9/BpLDvFwFK36vSYkRoSZCZ38Fr+g3nkdep25+oEsmx30IkTYvQVMFZKpK3WWMYUWjWgEzOSvhh+3BOg+3UoxBJSNk";

    private VuforiaLocalizer vuforia;

    private TFObjectDetector tfod;

    @Override
    public void runOpMode() throws InterruptedException {
        // The TFObjectDetector uses the camera frames from the VuforiaLocalizer, so we create that
        // first.
        //initVuforia();
        //initTfod();

        // Hardware mapping
        bob = hardwareMap.get(DcMotorEx.class, "front_left_motor");
        dylan = hardwareMap.get(DcMotorEx.class, "front_right_motor");
        larry = hardwareMap.get(DcMotorEx.class, "back_left_motor");
        jerry = hardwareMap.get(DcMotorEx.class, "back_right_motor");
        barry = hardwareMap.get(DcMotorEx.class, "swing_arm_motor");
        garry = hardwareMap.get(Servo.class, "wrist_joint");

        /*
        if (tfod != null) {
            tfod.activate();

            tfod.setZoom(2, 16.0/9.0);
        }
        */

        // Wait for the game to begin
        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();

        dylan.setDirection((DcMotorSimple.Direction.REVERSE));
        jerry.setDirection(DcMotorSimple.Direction.REVERSE);
        bob.setDirection(DcMotorSimple.Direction.FORWARD);
        larry.setDirection(DcMotorSimple.Direction.FORWARD);

        barry.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        waitForStart();

        /*
        if (opModeIsActive()) {
            for(int i=0; i < 4000; i++)
            {
                if (tfod != null) {
                    // getUpdatedRecognitions() will return null if no new information is available since
                    // the last time that call was made.
                    List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                    if (updatedRecognitions != null) {
                        telemetry.addData("# Object Detected", updatedRecognitions.size());

                        // step through the list of recognitions and display boundary info.
                        i = 0;
                        for (Recognition recognition : updatedRecognitions) {
                            telemetry.addData(String.format("label (%d)", i), recognition.getLabel());
                            telemetry.addData(String.format("  left,top (%d)", i), "%.03f , %.03f",
                                    recognition.getLeft(), recognition.getTop());
                            telemetry.addData(String.format("  right,bottom (%d)", i), "%.03f , %.03f",
                                    recognition.getRight(), recognition.getBottom());

                            if (recognition.getLabel().equals(LABEL_FIRST_ELEMENT)) {
                                tfod.shutdown();
                            }

                            else if (recognition.getLabel().equals(LABEL_SECOND_ELEMENT)) {
                                tfod.shutdown();
                            }

                            else {
                            }
                            telemetry.update();
                        }
                    }
                }
            }

            if (tfod != null) {
                tfod.shutdown();
            }
        }
        */

        while (opModeIsActive())
        {
            double rx = this.gamepad1.right_stick_x;
            double ry = -this.gamepad1.right_stick_y;
            double lx = this.gamepad1.left_stick_x;
            double ly = -this.gamepad1.left_stick_y;

            // Calculate point on the circumference of the circle to use as the joystick location
            rightJoy_x = rx / Math.sqrt(((Math.pow(rx, 2)) + (Math.pow(ry, 2))));
            rightJoy_y = ry / Math.sqrt(((Math.pow(rx, 2)) + (Math.pow(ry, 2))));
            leftJoy_x = lx / Math.sqrt(((Math.pow(lx, 2)) + (Math.pow(ly, 2))));
            leftJoy_y = ly / Math.sqrt(((Math.pow(lx, 2)) + (Math.pow(ly, 2))));

            telemetry.addData("Right stick X", rightJoy_x);
            telemetry.addData("Right stick Y", rightJoy_y);

            telemetry.addData("Left stick X", leftJoy_x);
            telemetry.addData("Left stick Y", leftJoy_y);

            TurnPlaces(leftJoy_x, leftJoy_y, calculatedSpeed());

            drivePlaces(calculatedDirection(rightJoy_x, rightJoy_y), calculatedSpeed());


            if (this.gamepad1.y && y_flag)
            {
                if (currentPosition == ClawPosition.START)
                {
                    currentPosition = ClawPosition.DOWN;
                }
                else if (currentPosition == ClawPosition.DOWN)
                {
                    currentPosition = ClawPosition.UP;
                }
                else if (currentPosition == ClawPosition.UP)
                {
                    currentPosition = ClawPosition.DOWN;
                }
                MoveClaw(currentPosition);
                y_flag = false;
            }

            else if (!this.gamepad1.y && !y_flag)
            {
                y_flag = true;
            }
            telemetry.addData("Claw position", currentPosition);

            telemetry.update();
        }

    }

    private void drivePlaces (String direction, double speed)
    {
        switch (direction)
        {
            case "STOP":
                // Stop
                dylan.setPower(0);
                jerry.setPower(0);
                bob.setPower(0);
                larry.setPower(0);
                break;
            case "FORWARD":
                // Drive forward
                dylan.setPower(speed);
                jerry.setPower(speed);
                bob.setPower(speed);
                larry.setPower(speed);
                break;
            case "FORWARD/RIGHT":
                // Drive forward/right
                dylan.setPower(speed);
                jerry.setPower(0);
                bob.setPower(0);
                larry.setPower(speed);
                break;
            case "RIGHT":
                // Drive right
                dylan.setPower(speed);
                jerry.setPower(-speed);
                bob.setPower(-speed);
                larry.setPower(speed);
                break;
            case "BACKWARD/RIGHT":
                // Drive backward/right
                dylan.setPower(0);
                jerry.setPower(-speed);
                bob.setPower(-speed);
                larry.setPower(0);
                break;
            case "BACKWARD":
                // Drive backward
                dylan.setPower(-speed);
                jerry.setPower(-speed);
                bob.setPower(-speed);
                larry.setPower(-speed);
                break;
            case "BACKWARD/LEFT":
                // Drive backward/left
                dylan.setPower(-speed);
                jerry.setPower(0);
                bob.setPower(0);
                larry.setPower(-speed);
                break;
            case "LEFT":
                // Drive left
                dylan.setPower(-speed);
                jerry.setPower(speed);
                bob.setPower(speed);
                larry.setPower(-speed);
                break;
            case "FORWARD/LEFT":
                // Drive forward/left
                dylan.setPower(0);
                jerry.setPower(speed);
                bob.setPower(speed);
                larry.setPower(0);
                break;
        }
    }

    private void TurnPlaces (double leftJoy_x, double leftJoy_y, double speed)
    {
        if (leftJoy_y < 0)
        {
            speed = -speed;
        }

        telemetry.addData("Speed: ", speed);

        if (leftJoy_x > 0)
        {
            dylan.setPower(speed * Math.abs(leftJoy_y));
            jerry.setPower(speed * Math.abs(leftJoy_y));
            bob.setPower(speed);
            larry.setPower(speed);
        }
        else if (leftJoy_x < 0)
        {
            dylan.setPower(speed);
            jerry.setPower(speed);
            bob.setPower(speed * Math.abs(leftJoy_y));
            larry.setPower(speed * Math.abs(leftJoy_y));
        }
    }

    private String calculatedDirection (double rightJoy_x, double rightJoy_y)
    {
        String directionToTravel;
        
        if (rightJoy_x > Math.cos(1.96) && rightJoy_x < Math.cos(1.18) && rightJoy_y > 0)
        {
            directionToTravel = "FORWARD"; // Forwards direction
        }
        else if (rightJoy_x > Math.cos(1.18) && rightJoy_x < Math.cos(0.393)  && rightJoy_y > 0)
        {
            directionToTravel = "FORWARD/RIGHT"; // Forward/right direction
        }
        else if (rightJoy_x > 0 && rightJoy_y > Math.sin(-0.393) && rightJoy_y < Math.sin(0.393))
        {
            directionToTravel = "RIGHT"; // Right direction
        }
        else if (rightJoy_x > Math.cos(1.18) && rightJoy_x < Math.cos(0.393)  && rightJoy_y < 0)
        {
            directionToTravel = "BACKWARD/RIGHT"; // Backward/right direction
        }
        else if (rightJoy_x > Math.cos(1.96) && rightJoy_x < Math.cos(1.18) && rightJoy_y < 0)
        {
            directionToTravel = "BACKWARD"; // Backwards direction
        }
        else if (rightJoy_x < Math.cos(1.96) && rightJoy_x > Math.cos(2.75) && rightJoy_y < 0)
        {
            directionToTravel = "BACKWARD/LEFT"; // Backward/left direction
        }
        else if (rightJoy_x < 0 && rightJoy_y > Math.sin(-0.393) && rightJoy_y < Math.sin(0.393))
        {
            directionToTravel = "LEFT"; // Left direction
        }
        else if (rightJoy_x < Math.cos(1.96) && rightJoy_x > Math.cos(2.75) && rightJoy_y > 0)
        {
            directionToTravel = "FORWARD/LEFT"; // Forward/left direction
        }
        else
        {
            directionToTravel = "STOP";
        }
        
        telemetry.addData("Check Direction: ", directionToTravel);

        return directionToTravel;
    }

    private double calculatedSpeed()
    {
        double speed;

        // Repeat of initial values, but only applies to this method
        double rx = this.gamepad1.right_stick_x;
        double ry = -this.gamepad1.right_stick_y;
        double lx = this.gamepad1.left_stick_x;
        double ly = -this.gamepad1.left_stick_y;

        // Calculate distance from joystick to center
        double speedRight = Math.sqrt(Math.pow(rx, 2) + Math.pow(ry, 2)) * 0.75;
        double speedLeft = Math.sqrt(Math.pow(lx, 2) + Math.pow(ly, 2)) * 0.75;

        if (speedRight > speedLeft)
        {
            speed = speedRight;
        }
        else if(speedLeft > speedRight)
        {
            speed = speedLeft;
        }
        else
        {
            speed = 0;
        }

        return speed;
    }

    private void MoveClaw (Enum position)
    {
        // Code to move claw to placement position
        if (position == ClawPosition.START)
        {
            // put in an actual target position
            barry.setTargetPosition(0);
            barry.setPower(0);

            // Move the things
            barry.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            garry.setPosition(0.25);
        }

        // Code move claw to pickup position
        else if (position == ClawPosition.DOWN)
        {
            // put in an actual target position
            barry.setTargetPosition(1800);
            barry.setPower(0.5);

            // Move the things
            barry.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            garry.setPosition(0.5);
        }

        else if (position == ClawPosition.UP)
        {
            // put in an actual target position
            barry.setTargetPosition(1000);
            barry.setPower(0.5);

            // Move the things
            barry.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            garry.setPosition(0.25);
        }

        else
        {
            return;
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