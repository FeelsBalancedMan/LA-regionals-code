//Package statement
package org.firstinspires.ftc.teamcode.Teleop.Objects;

//Import statements
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;

/** Grabber created by Isaac Dienstag for team 9804 Bombsquad
 * This class holds all the methods used to function the grabber of the robot. It has two DcMotors,
 * one for the extender and one for the sweeper, and one CRServo for the disrupter. It also has two
 * DigitalChannels representing the SlowState and the store sensors. We use these digital channels
 * along with the variables given to us from the main to determine these motor and servo powers.*/

//Class declorations
public class Grabber {

    //Declare DcMotors
    private DcMotor sweeper;
    private DcMotor extender;

    //Declare DigitalChannels
    private DigitalChannel store, slow;

    //Declare CRServo
    private CRServo disrupter;

    //Declare instance variables
    private double sum;


    //Constructs a Grabber with two motor names and two motor directions, as well as a digitChannel
    public Grabber(DcMotor newSweeper, DcMotor newExtender, DcMotorSimple.Direction sweeperD, DcMotorSimple.Direction extenderD,
                   DigitalChannel newStore,  DigitalChannel newSlow, CRServo newDisrupter) {
        //Sets our private instance motors to the inputs
        sweeper = newSweeper;
        extender = newExtender;
        sweeper.setDirection(sweeperD);
        extender.setDirection(extenderD);
        store = newStore;
        disrupter = newDisrupter;
        slow = newSlow;
    } //End constructor

    //Asks for two doubles, and checks if input is too small to be considered intentional based on the value of tolereance.
    private double deadzone(double tolerance, double input){
        if(input > tolerance || input < -(tolerance)) //If |input| > deadzon
            return input; //We return input, because the input is likely intentional.
        else //Else (which means |input| <= |deadzone|)
            return 0; //e ignore input, and output 0.
    } //End double method

    //Returns true if |input| > |deadzone|, false if |input| <= |deadzone|
    private boolean outOfDeadzone(double tolerance, double input){
        return deadzone(tolerance, input) != 0;
    } //End boolean method

    //Reach asks for two doubles and a boolean, and runs the extender motor at rt - lt, so it ranges from 1 to -1
    //Uses the boolean as a toggle and uses it to determine whether or not to run the disrupter servo.
    public void reach(double lt, double rt) {

        sum = rt - lt;//The sum of the two triggers that ranges from 1 to -1
        if(!slow.getState()) { //If slowState is false (which means activated)
            extender.setPower(deadzone(.05, sum)*.75); //Cut the extender power in half for more precise control
            disrupt(false); //Turn off the disrupter, since previousStatus is false
        }
        else { //Else (slow.getState() && !previousStatus)
            extender.setPower(deadzone(.05, sum)); //Set extender to full power
            disrupt(false); //Turn off the disrupter
        }
    } //End void method

    //Asks for a double and sets the sweeper motor to the value of pow^2, while maintaing + or - direction
    public void intake(double pow){
        if(outOfDeadzone(.05, pow))//If pow is a significant value (More than .05 away from 0, not accidental)
            sweeper.setPower(deadzone(.05, pow) * Math.abs(pow)); //Set the power to pow with a squared power curve
        else if(!store.getState()) //If store is false, meaning the sweeper is in the store position
            sweeper.setPower(0); //Stop moving the store motor
        else //Else (!outOfDeadzone(.05, pow) && store.getState())
            sweeper.setPower(.5); //Set the power of sweeper to .2 so it moves back to the store position
    } //End void method

    //Asks for a boolean and determines whether or not to run the disrupter servo
    public void disrupt(boolean on){
        if(on) //If on is true
            disrupter.setPower(-.7); //We run the servo at .7 power
        else //Else (!on)
            disrupter.setPower(0); //We set the servo power to 0
    } //End void method

    //Sets the ZeroPowerBehavior of the sweeper motor to BRAKE
    public void setZeroPow(){
        //Set the zeroPowerBehavior of the sweeper motor to break for more fine control
        sweeper.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    } //End void method

    //Getter statement used in telemetry
    public double getExtenderPower(){return extender.getPower();} //Returns the current power of the extender motor
    public double getSweeperPower(){return sweeper.getPower();} //Returns the current power of the sweeper motor
    public double getDisrupterPower(){return disrupter.getPower();} //Returns the current power of disrupter CR servo
    public boolean getSlowState(){return slow.getState();} //Returns the current state of the slowState sensor

} //End class