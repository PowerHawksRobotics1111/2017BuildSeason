
package org.usfirst.frc.team1111.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import variables.Motors;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot 
{
	public static boolean findValues=false, moveAlignDist=false, turnAngle=false, visionAlign=false, findPegDist=false, movePegDist=false;
	final String defaultAuto = "Default";
    final String customAuto = "My Auto";
    
    String autoSelected;
    SendableChooser chooser;
    final String pegLeft = "Left Peg", pegRight = "Right Peg", pegMid = "Middle Peg", base = "Baseline", reach = "Reach", lowShoot = "Boiler Low Goal", nothing = "Nothing";
    
    
    
    double leftDrive, rightDrive;
	
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() 
    {
        chooser = new SendableChooser();
        
        chooser.addObject(base, "Baseline");
        chooser.addObject(pegLeft, "Left Peg");
        chooser.addObject(pegMid, "Middle Peg");
        chooser.addObject(pegRight, "Right Peg");
        chooser.addObject(lowShoot, "Boiler Low Goal");
        chooser.addObject(reach, "Boiler Low and Left Peg");
        chooser.addDefault(nothing, "Nothing");
        
        rightDrive = variables.Joysticks.joyDrive.getRawAxis(3);
		leftDrive = variables.Joysticks.joyDrive.getRawAxis(1);
		
		SmartDashboard.putData("Auto Selection", chooser);
		updateDashboard();
		
		Motors.motorInit();
    }
    
    /**
     * Put in here stuff that needs to be on the dashboard
     * Examples are literally anything the drive team would need to know
     * 		Booleans, sensor statuses and values, mechanism statuses, etc.
     */
    public void updateDashboard()
    {
    	
    }
	/**
	 * This autonomous (along with the chooser code above) shows how to select between different autonomous modes
	 * using the dashboard. The sendable chooser code works with the Java SmartDashboard. If you prefer the LabVIEW
	 * Dashboard, remove all of the chooser code and uncomment the getString line to get the auto name from the text box
	 * below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the switch structure below with additional strings.
	 * If using the SendableChooser make sure to add them to the chooser code above as well.
	 */
    public void autonomousInit() 
    {
    	boolean[] pegAuto = {findValues, moveAlignDist, turnAngle, visionAlign, findPegDist, movePegDist}; //Not sure if necessary
    	autoSelected = (String) chooser.getSelected();
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() 
    {
    	switch(autoSelected) 
    	{
    	case base:
        case pegMid:   
        	Auto.pegMiddle();
            break;
    	case pegLeft:
    		Auto.pegLeft(distFromCorner); //TODO Figure out this value
    		break;
    	case pegRight:
    		Auto.pegRight(distFromCorner); //TODO Figure out this value
    		break;
    	case lowShoot:
    		Auto.lowBoiler();
    		break;
    	case reach:
    		Auto.reach();
    		break;
    	case nothing:
            break;
    	}
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() 
    {
    	subsystems.Drivetrain.drive(leftDrive, rightDrive);
    	Operator.operate();
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() 
    {
    
    }
    
}
