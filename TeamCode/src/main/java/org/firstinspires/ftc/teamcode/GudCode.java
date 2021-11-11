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
    private double deadzone;
    private boolean y_flag;
    private boolean x_flag;

    private enum ClawPosition
    {
        UP, DOWN, START
    }

    ClawPosition currentPosition = ClawPosition.START;

    private static final String VUFORIA_KEY =
            "AbskhHb/////AAABmb8nKWBiYUJ9oEFmxQL9H2kC6M9FzPa1acXUaS/H5wRkeNbpNVBJjDfcrhlTV2SIGc/lxBOtq9X7doE2acyeVOPg4sP69PQQmDVQH5h62IwL8x7BS/udilLU7MyX3KEoaFN+eR1o4FKBspsYrIXA/Oth+TUyrXuAcc6bKSSblICUpDXCeUbj17KrhghgcgxU6wzl84lCDoz6IJ9egO+CG4HlsBhC/YAo0zzi82/BIUMjBLgFMc63fc6eGTGiqjCfrQPtRWHdj2sXHtsjZr9/BpLDvFwFK36vSYkRoSZCZ38Fr+g3nkdep25+oEsmx30IkTYvQVMFZKpK3WWMYUWjWgEzOSvhh+3BOg+3UoxBJSNk";

    @Override
    public void runOpMode() throws InterruptedException {

        // Hardware mapping
        bob = hardwareMap.get(DcMotorEx.class, "front_left_motor");
        dylan = hardwareMap.get(DcMotorEx.class, "front_right_motor");
        larry = hardwareMap.get(DcMotorEx.class, "back_left_motor");
        jerry = hardwareMap.get(DcMotorEx.class, "back_right_motor");
        barry = hardwareMap.get(DcMotorEx.class, "swing_arm_motor");
        garry = hardwareMap.get(Servo.class, "wrist_joint");

        // Wait for the game to begin
        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();

        dylan.setDirection((DcMotorSimple.Direction.REVERSE));
        jerry.setDirection(DcMotorSimple.Direction.REVERSE);
        bob.setDirection(DcMotorSimple.Direction.FORWARD);
        larry.setDirection(DcMotorSimple.Direction.FORWARD);

        barry.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        waitForStart();

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

            if (JoyIsActive("left"))
            {
                TurnPlaces(leftJoy_x, leftJoy_y, calculatedSpeed("left"));
                telemetry.addData("Turny things", 0);
            }
            else if (JoyIsActive("right"))
            {
                DrivePlaces(calculatedDirection(rightJoy_x, rightJoy_y), calculatedSpeed("right"));
                telemetry.addData("Drivy things", 0);
            }
            else
            {
                dylan.setPower(0);
                jerry.setPower(0);
                bob.setPower(0);
                larry.setPower(0);
            }

            if (this.gamepad1.x && x_flag){
                MoveClaw(currentPosition);
                x_flag = false;
            }
            else if (!this.gamepad1.x && !x_flag)
            {
                x_flag = true;
            }

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

    private void DrivePlaces (String direction, double speed)
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

        telemetry.addData("It should be turning", leftJoy_x);

        if(leftJoy_x > 0)
        {
            dylan.setPower(speed * Math.abs(leftJoy_x / 2));
            jerry.setPower(speed * Math.abs(leftJoy_x / 2));
            bob.setPower(speed);
            larry.setPower(speed);
        }
        else if(leftJoy_x < 0)
        {
            dylan.setPower(speed);
            jerry.setPower(speed);
            bob.setPower(speed * Math.abs(leftJoy_x / 2));
            larry.setPower(speed * Math.abs(leftJoy_x / 2));
        }
        else
        {
            dylan.setPower(speed);
            jerry.setPower(speed);
            bob.setPower(speed);
            larry.setPower(speed);
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

    private double calculatedSpeed(String joyStick)
    {
        double speed;

        // Repeat of initial values, but only applies to this method
        double rx = this.gamepad1.right_stick_x;
        double ry = -this.gamepad1.right_stick_y;
        double lx = this.gamepad1.left_stick_x;
        double ly = -this.gamepad1.left_stick_y;

        joyStick.toLowerCase();

        if (joyStick == "right")
        {
            speed = Math.sqrt(Math.pow(rx, 2) + Math.pow(ry, 2)) * 0.75;;
        }
        else if(joyStick == "left")
        {
            speed = Math.sqrt(Math.pow(lx, 2) + Math.pow(ly, 2)) * 0.75;;
        }
        else
        {
            speed = 0;
        }

        return speed;
    }

    private boolean JoyIsActive (String joyStick)
    {
        boolean isActive = false;
        double rx = this.gamepad1.right_stick_x;
        double ry = -this.gamepad1.right_stick_y;
        double lx = this.gamepad1.left_stick_x;
        double ly = -this.gamepad1.left_stick_y;

        joyStick.toLowerCase();

        if (joyStick == "right")
        {
            if (Math.abs(rx) > deadzone || Math.abs(ry) > deadzone)
            {
                isActive = true;
            }
            else
            {
                isActive = false;
            }
        }
        else if (joyStick == "left")
        {
            if (Math.abs(lx) > deadzone || Math.abs(ly) > deadzone)
            {
                isActive = true;
            }
            else
            {
                isActive = false;
            }
        }

        return isActive;
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
    }
}