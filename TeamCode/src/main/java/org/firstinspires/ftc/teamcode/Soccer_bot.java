//Imports
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

@TeleOp
//this program lets the robot recieve and respond to input from the controller
public class Soccer_bot extends LinearOpMode{

    //name motors
    private DcMotorEx front_left_motor;
    private DcMotorEx front_right_motor;
    private DcMotorEx back_left_motor;
    private DcMotorEx back_right_motor;

    @Override
    public void runOpMode() {

        //connect the motor variables to the actual hardware
        front_left_motor = hardwareMap.get(DcMotorEx.class, "front_left_motor");
        front_right_motor = hardwareMap.get(DcMotorEx.class, "front_right_motor");
        back_left_motor = hardwareMap.get(DcMotorEx.class, "back_left_motor");
        back_right_motor = hardwareMap.get(DcMotorEx.class, "back_right_motor");

        //Waits for it to start
        waitForStart();

        //Initialize the stuffzyx
        double leftJoy_y = 0;
        double rightJoy_y = 0;
        double leftJoy_x = 0;
        double rightJoy_x = 0;
        double powerMultiplier = 1;
        boolean a_flag = true;

        //loop after start button
        while (opModeIsActive()) {

            //get the cords for the joysticks. Switched so positive y is forwards
            leftJoy_y = -this.gamepad1.left_stick_y;
            rightJoy_y = -this.gamepad1.right_stick_y;

            //Right side 0-point method
            front_right_motor.setPower(rightJoy_y);
            back_right_motor.setPower(rightJoy_y);

            //Left side 0-point method
            front_left_motor.setPower(-leftJoy_y);
            back_left_motor.setPower(-leftJoy_y);

            //Deadzone just in case some funky junk happens
            if (leftJoy_y < 0.1 && leftJoy_y > -0.1) {
                front_left_motor.setPower(0);
                back_left_motor.setPower(0);
            }

            if (rightJoy_y < 0.1 && rightJoy_y > -0.1) {
                front_right_motor.setPower(0);
                back_right_motor.setPower(0);
            }
            //Deadzone code done

        // end wheel stuff


            //update the text on the phone (telemetry)
            telemetry.addData("Y value", leftJoy_y);
            telemetry.addData("X value", leftJoy_x);
            telemetry.addData("Servo logic status", this.gamepad1.a);
            telemetry.addData("", a_flag);
            telemetry.update();
        }
    }
}
