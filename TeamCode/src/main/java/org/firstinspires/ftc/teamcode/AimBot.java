//Imports
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

@TeleOp
//this program lets the robot recieve and respond to input from the controller
public class AimBot extends LinearOpMode{

    private static final String TFOD_MODEL_ASSET = "UltimateGoal.tflite";
    private static final String LABEL_FIRST_ELEMENT = "Quad";
    private static final String LABEL_SECOND_ELEMENT = "Single";

    //name motors
    private DcMotorEx front_left_motor;
    private DcMotorEx front_right_motor;
    private DcMotorEx back_left_motor;
    private DcMotorEx back_right_motor;
    private DcMotorEx left_shooty_motor;
    private DcMotor weed_wacker;
    private DcMotor lift_claw;
    private Servo conveyor;
    private Servo open_claw;

    private static final String VUFORIA_KEY =
            "AbskhHb/////AAABmb8nKWBiYUJ9oEFmxQL9H2kC6M9FzPa1acXUaS/H5wRkeNbpNVBJjDfcrhlTV2SIGc/lxBOtq9X7doE2acyeVOPg4sP69PQQmDVQH5h62IwL8x7BS/udilLU7MyX3KEoaFN+eR1o4FKBspsYrIXA/Oth+TUyrXuAcc6bKSSblICUpDXCeUbj17KrhghgcgxU6wzl84lCDoz6IJ9egO+CG4HlsBhC/YAo0zzi82/BIUMjBLgFMc63fc6eGTGiqjCfrQPtRWHdj2sXHtsjZr9/BpLDvFwFK36vSYkRoSZCZ38Fr+g3nkdep25+oEsmx30IkTYvQVMFZKpK3WWMYUWjWgEzOSvhh+3BOg+3UoxBJSNk";

    private VuforiaLocalizer vuforia;

    private TFObjectDetector tfod;


    @Override
    public void runOpMode() {

        initVuforia();

        //connect the motor variables to the actual hardware
        front_left_motor = hardwareMap.get(DcMotorEx.class, "front_left_motor");
        front_right_motor = hardwareMap.get(DcMotorEx.class, "front_right_motor");
        back_left_motor = hardwareMap.get(DcMotorEx.class, "back_left_motor");
        back_right_motor = hardwareMap.get(DcMotorEx.class, "back_right_motor");
        left_shooty_motor = hardwareMap.get(DcMotorEx.class, "left_shooty_motor");
        weed_wacker = hardwareMap.get(DcMotor.class, "weed_wacker");
        conveyor = hardwareMap.get(Servo.class, "conveyor");
        lift_claw = hardwareMap.get(DcMotor.class, "lift_claw");
        open_claw = hardwareMap.get(Servo.class, "open_claw");

        //Waits for it to start
        waitForStart();

        //Initialize the stuff
        double leftJoy_y = 0;
        double rightJoy_y = 0;
        double leftJoy_x = 0;
        double rightJoy_x = 0;
        double powerMultiplier = 0.75;
        boolean weed_wacker_on = false;
        boolean claw_open = true;
        boolean left_bumper_flag = true;
        boolean y_flag = true;
        boolean chaos_mode = false;


        //set flywheel to use encoder
        left_shooty_motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //loop after start button
        while (opModeIsActive()) {

            //get the cords for the joysticks. Switched so positive y is forwards
            leftJoy_y = -this.gamepad1.left_stick_y;
            leftJoy_x = this.gamepad1.left_stick_x;
            rightJoy_y = -this.gamepad1.right_stick_y;
            rightJoy_x = this.gamepad1.right_stick_x;

            //Set deadzone for wheels with copiously long if statement
            if (leftJoy_y < 0.1 && leftJoy_y > -0.1 && leftJoy_x < 0.1 && leftJoy_x > -0.1
                    && rightJoy_y < 0.1 && rightJoy_y > -0.1 && rightJoy_x < 0.1 && rightJoy_x > -0.1) {

                back_right_motor.setPower(0);
                front_right_motor.setPower(0);
                back_left_motor.setPower(0);
                front_left_motor.setPower(0);

            }

            //Only run if chaos mode is on
            else if (chaos_mode) {

                //If the right joystick is pushed right, tank turn right
                if(rightJoy_x > 0) {

                    back_right_motor.setVelocity(rightJoy_x*powerMultiplier);
                    front_right_motor.setVelocity(rightJoy_x*powerMultiplier);
                    back_left_motor.setVelocity(rightJoy_x*powerMultiplier);
                    front_left_motor.setVelocity(rightJoy_x*powerMultiplier);
                }

                //If the right joystick is pushed left, tank turn left
                else if(rightJoy_x < 0) {

                    back_right_motor.setVelocity(rightJoy_x*powerMultiplier);
                    front_right_motor.setVelocity(rightJoy_x*powerMultiplier);
                    back_left_motor.setVelocity(rightJoy_x*powerMultiplier);
                    front_left_motor.setVelocity(rightJoy_x*powerMultiplier);
                }

                //if the left joystick is pushed forward, move the robot forward.
                else if(leftJoy_y > leftJoy_x && leftJoy_y > -leftJoy_x){

                    back_right_motor.setVelocity(-leftJoy_y*powerMultiplier);
                    front_right_motor.setVelocity(-leftJoy_y*powerMultiplier);
                    back_left_motor.setVelocity(leftJoy_y*powerMultiplier);
                    front_left_motor.setVelocity(leftJoy_y*powerMultiplier);
                    telemetry.addData("Moving forwards: ", leftJoy_y);
                }

                //if the left joystick is pushed backwards, move the robot backwards.
                else if(leftJoy_y < leftJoy_x && leftJoy_y < -leftJoy_x){

                    back_right_motor.setVelocity(-leftJoy_y*powerMultiplier);
                    front_right_motor.setVelocity(-leftJoy_y*powerMultiplier);
                    back_left_motor.setVelocity(leftJoy_y*powerMultiplier);
                    front_left_motor.setVelocity(leftJoy_y*powerMultiplier);
                    telemetry.addData("Moving backwards: ", leftJoy_y);
                }


                //if the left joystick is pushed right, move the robot right.
                else if(leftJoy_y < leftJoy_x && leftJoy_y > -leftJoy_x){

                    back_right_motor.setVelocity(-leftJoy_x*powerMultiplier);
                    front_right_motor.setVelocity(leftJoy_x*powerMultiplier);
                    back_left_motor.setVelocity(-leftJoy_x*powerMultiplier);
                    front_left_motor.setVelocity(leftJoy_x*powerMultiplier);
                    telemetry.addData("Moving right: ", leftJoy_x);
                }

                //if the left joystick is pushed left, move the robot left.
                else if(leftJoy_y > leftJoy_x && leftJoy_y < -leftJoy_x){

                    back_right_motor.setVelocity(-leftJoy_x*powerMultiplier);
                    front_right_motor.setVelocity(leftJoy_x*powerMultiplier);
                    back_left_motor.setVelocity(-leftJoy_x*powerMultiplier);
                    front_left_motor.setVelocity(leftJoy_x*powerMultiplier);
                    telemetry.addData("Moving left: ", leftJoy_x);
                }

            }

            //Anytime it's not in the deadzone as long as chaos mode is off
            else {

                //If the right joystick is pushed right, tank turn right
                if(rightJoy_x > 0) {
                    back_right_motor.setPower(rightJoy_x*powerMultiplier);
                    front_right_motor.setPower(rightJoy_x*powerMultiplier);
                    back_left_motor.setPower(rightJoy_x*powerMultiplier);
                    front_left_motor.setPower(rightJoy_x*powerMultiplier);
                }

                //If the right joystick is pushed left, tank turn left
                else if(rightJoy_x < 0) {
                    back_right_motor.setPower(rightJoy_x*powerMultiplier);
                    front_right_motor.setPower(rightJoy_x*powerMultiplier);
                    back_left_motor.setPower(rightJoy_x*powerMultiplier);
                    front_left_motor.setPower(rightJoy_x*powerMultiplier);
                }

                //if the left joystick is pushed forward, move the robot forward.
                else if(leftJoy_y > leftJoy_x && leftJoy_y > -leftJoy_x){
                    back_right_motor.setPower(-leftJoy_y*powerMultiplier);
                    front_right_motor.setPower(-leftJoy_y*powerMultiplier);
                    back_left_motor.setPower(leftJoy_y*powerMultiplier);
                    front_left_motor.setPower(leftJoy_y*powerMultiplier);
                    telemetry.addData("Moving forwards: ", leftJoy_y);
                }

                //if the left joystick is pushed backwards, move the robot backwards.
                else if(leftJoy_y < leftJoy_x && leftJoy_y < -leftJoy_x){
                    back_right_motor.setPower(-leftJoy_y*powerMultiplier);
                    front_right_motor.setPower(-leftJoy_y*powerMultiplier);
                    back_left_motor.setPower(leftJoy_y*powerMultiplier);
                    front_left_motor.setPower(leftJoy_y*powerMultiplier);
                    telemetry.addData("Moving backwards: ", leftJoy_y);
                }


                //if the left joystick is pushed right, move the robot right.
                else if(leftJoy_y < leftJoy_x && leftJoy_y > -leftJoy_x){
                    back_right_motor.setPower(-leftJoy_x*powerMultiplier);
                    front_right_motor.setPower(leftJoy_x*powerMultiplier);
                    back_left_motor.setPower(-leftJoy_x*powerMultiplier);
                    front_left_motor.setPower(leftJoy_x*powerMultiplier);
                    telemetry.addData("Moving right: ", leftJoy_x);
                }

                //if the left joystick is pushed left, move the robot left.
                else if(leftJoy_y > leftJoy_x && leftJoy_y < -leftJoy_x){
                    back_right_motor.setPower(-leftJoy_x*powerMultiplier);
                    front_right_motor.setPower(leftJoy_x*powerMultiplier);
                    back_left_motor.setPower(-leftJoy_x*powerMultiplier);
                    front_left_motor.setPower(leftJoy_x*powerMultiplier);
                    telemetry.addData("Moving left: ", leftJoy_x);
                }
            }
            //end wheel stuff

            //##########################################################
            //When the right trigger is down, make the conveyor go
            if(this.gamepad2.right_trigger > 0) {
                conveyor.setPosition(1);
                telemetry.addData("BRRRRRRRRRRRRRR", 0);
            }

            //When the right trigger isn't down, no more conveyor
            else if (this.gamepad2.right_trigger == 0) {
                conveyor.setPosition(0.5);

            }


            //##########################################################
            //toggle intake
            if(this.gamepad2.y && y_flag) {

                weed_wacker_on = !weed_wacker_on;

                if(weed_wacker_on){

                    weed_wacker.setPower(1);

                }

                else if(!weed_wacker_on){

                    weed_wacker.setPower(0);

                }
                y_flag = false;
                telemetry.addData("do things", y_flag);

            }

            else if(!this.gamepad2.y && !y_flag){

                y_flag = true;

            }

            //CHAOS MODE
            if (this.gamepad1.y){
                chaos_mode = true;
                powerMultiplier = 1000;
                telemetry.addData("CHAOS MODE: ENABLED", powerMultiplier);
            }

            //##########################################################
            //claw code for open/close
            //When the left bumper is down and flag is there
            if(this.gamepad1.left_bumper && left_bumper_flag) {

                //toggle the claw_open boolean
                claw_open = !claw_open;

                //open the claw if necessary
                if(claw_open){

                    open_claw.setPosition(0.85);

                }

                //close the claw if necessary
                else if(!claw_open){

                    open_claw.setPosition(0.15);

                }
                //ditch the flag
                left_bumper_flag = false;

                //telemetry
                telemetry.addData("pain", 0);

            }
            //when the left bumper is not down and the flag isn't there
            else if(!this.gamepad1.left_bumper && !left_bumper_flag){

                //add the flag
                left_bumper_flag = true;

            }

            //*************************************
            //claw code for lift ect

            if(this.gamepad1.a) {
                lift_claw.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                lift_claw.setPower(0.5);
            }

            else if(this.gamepad1.x) {
                lift_claw.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                lift_claw.setPower(-0.5);
            }

            else {

                lift_claw.setPower(0);
            }
            //##########################################################
            //toggle high and low power b is for high goal
            if(this.gamepad2.b) {

                left_shooty_motor.setVelocity(-1580);
                //left_shooty_motor.setPower
            }

            //x is for power shot targets
            if(this.gamepad2.x) {
                left_shooty_motor.setVelocity(-1400);
            }
            //a turns off flywheel
            if(this.gamepad2.a) {
                left_shooty_motor.setVelocity(0);
            }

            //The D-pad and left trigger are for unjamming
            //The D-pad up makes the flywheel go backwards
            if(this.gamepad2.dpad_up) {
                left_shooty_motor.setVelocity(1);
            }

            //The D-pad down makes the weedwacker go backwards
            if(this.gamepad2.dpad_down) {
                weed_wacker.setPower(1);
            }

            //The left trigger makes the conveyer go backwards
            if(this.gamepad2.left_trigger > 0) {
                conveyor.setPosition(0);
            }

            //update the text on the phone (telemetry)
            telemetry.addData("Y value", leftJoy_y);
            telemetry.addData("X value", leftJoy_x);
            telemetry.addData("Left Trigger", this.gamepad1.left_trigger);
            telemetry.addData("Right Trigger", this.gamepad1.right_trigger);
            telemetry.addData("right_bumper",this.gamepad1.right_bumper);
            telemetry.addData("left_bumper_flag", left_bumper_flag);
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
