package org.usfirst.frc.team1111.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import subsystems.Drivetrain;
import variables.Joysticks;
import variables.Motors;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	
	final String defaultAuto = "Default";
	final String customAuto = "My Auto";
	
	String autoSelected;
	SendableChooser chooser;
	final String pegLeft = "Left Peg", pegRight = "Right Peg", pegMid = "Middle Peg", base = "Baseline",
			pegAndBoiler = "Left Peg and Boiler High", highBoiler = "Boiler High Goal", nothing = "Nothing";
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit()
	{
		chooser = new SendableChooser();
		
		chooser.addObject(base, "Baseline");
		chooser.addObject(pegLeft, "Left Peg");
		chooser.addObject(pegMid, "Middle Peg");
		chooser.addObject(pegRight, "Right Peg");
		chooser.addObject(highBoiler, "Boiler High Goal");
		chooser.addObject(pegAndBoiler, "Left Peg and Boiler High");
		chooser.addDefault(nothing, "Nothing");
		
		SmartDashboard.putData("Auto Selection", chooser);
		updateDashboard();
		
		Motors.motorInit();
	}
	
	/**
	 * Put in here stuff that needs to be on the dashboard Examples are
	 * literally anything the drive team would need to know Booleans, sensor
	 * statuses and values, mechanism statuses, etc.
	 */
	public void updateDashboard()
	{
		SmartDashboard.putNumber("Shooter Speed", Motors.motorShooter.getEncVelocity());
		SmartDashboard.putBoolean("Shooter", Motors.motorShooter.getEncVelocity() >= Motors.shooterSpeed);
	}
	
	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit()
	{
		autoSelected = (String) chooser.getSelected();
	}
	
	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic()
	{
		boolean pegleft = true;
		switch (autoSelected)
		{
			case base:
			case pegMid:
			default:
				Auto.pegMiddle();
				break;
			case pegRight:
				pegleft = false;
				Auto.pegSide(distFromCorner, pegleft);
			case pegLeft:
				Auto.pegSide(distFromCorner, pegleft);
				// TODO need to determine this value
				break;
			case highBoiler:
				Auto.highBoiler(distFromCorner);
				break;
			case pegAndBoiler:
				Auto.pegAndBoiler();
				break;
			case nothing:
				break;
		}
	}
	
	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic()
	{
		Drivetrain.drive(Joysticks.joyDrive.getRawAxis(1), Joysticks.joyDrive.getRawAxis(3));
		Operator.operate();
		updateDashboard();
	}
	
	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic()
	{
		
	}
	
	@Override
	public void disabledPeriodic()
	{
		
	}
	
}
