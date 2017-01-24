package org.usfirst.frc.team1111.robot;

import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import subsystems.Drivetrain;
import variables.Dimensions;
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
			pegAndBoiler = "Boiler Peg and Highgoal", pegAndHopper = "Boiler Peg and Dump Nearest Hopper",
			highBoiler = "Boiler High Goal", nothing = "Nothing";
	public static boolean isRed;
	public static boolean overrideDriverJoysticks = false;
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit()
	{
		chooser = new SendableChooser();
		
		chooser.addObject(base, base);
		chooser.addObject(pegLeft, pegLeft);
		chooser.addObject(pegMid, pegMid);
		chooser.addObject(pegRight, pegRight);// TODO do we want to rename
												// this, e.g. boilerSidePeg
		chooser.addObject(highBoiler, highBoiler);
		chooser.addObject(pegAndBoiler, pegAndBoiler);
		chooser.addObject(pegAndHopper, pegAndHopper);
		chooser.addDefault(nothing, nothing);
		
		SmartDashboard.putData("Auto Selection", chooser);
		updateDashboard();
		
		Motors.motorInit();
		if (m_ds.getAlliance() == Alliance.Red)
			isRed = true;
		else
			isRed = false;
		
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
		SmartDashboard.putBoolean("Gear Stop Down", Motors.gearHold1.getAngle() == Motors.gearStopdownAngle);
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
		Motors.motorDriveLeft1.enableBrakeMode(true);
		Motors.motorDriveLeft2.enableBrakeMode(true);
		Motors.motorDriveRight1.enableBrakeMode(true);
		Motors.motorDriveRight2.enableBrakeMode(true);
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
				// Drivetrain.moveToDistance(Dimensions.DIST_TO_BASELINE+60);//Goes
				// 5ft (60 in) past baseline)
				double distanceDelta = (Dimensions.DIST_TO_BASELINE + 60) - Motors.motorDriveLeft2.getEncPosition();
				if (distanceDelta > 0)
					Drivetrain.drive(1, 1);
				else
					Drivetrain.drive(0, 0);
				break;
			case pegMid:
			default:
				Auto.pegMiddle();
				break;
			case pegRight:
				pegleft = false;
				Auto.pegSide(/* distFromCorner */0.0, pegleft);
			case pegLeft:
				Auto.pegSide(/* distFromCorner */0.0, pegleft);
				break;
			case highBoiler:
				Auto.highBoiler(/* distFromCorner */0.0, isRed);
				break;
			case pegAndBoiler:
				Auto.pegAndBoiler(/* distFromCorner */0.0, isRed);
				break;
			case pegAndHopper:
				Auto.pegAndHopper(/* distFromCorner */0.0, isRed);
				break;
			case nothing:
				break;
		}
	}
	
	@Override
	public void teleopInit()
	{
		Motors.motorDriveLeft1.enableBrakeMode(false);
		Motors.motorDriveLeft2.enableBrakeMode(false);
		Motors.motorDriveRight1.enableBrakeMode(false);
		Motors.motorDriveRight2.enableBrakeMode(false);
	}
	
	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic()
	{
		if ( !overrideDriverJoysticks)
			Drivetrain.drive(Joysticks.joyDrive.getRawAxis(1), Joysticks.joyDrive.getRawAxis(3));
		if ((Math.abs(Joysticks.joyDrive.getRawAxis(0)) + Math.abs(Joysticks.joyDrive.getRawAxis(2))) >= .3)
			System.out.println("Mark! You can't go sideways...");
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
		Motors.motorDriveLeft1.enableBrakeMode(true);
		Motors.motorDriveLeft2.enableBrakeMode(true);
		Motors.motorDriveRight1.enableBrakeMode(true);
		Motors.motorDriveRight2.enableBrakeMode(true);
		Drivetrain.drive(0, 0);
		Motors.motorHang.set(0.0);
		Motors.motorIntake.set(0.0);
		Motors.motorShooter.set(0.0);
	}
	
}
