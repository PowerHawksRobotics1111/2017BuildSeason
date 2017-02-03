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
	
	String autoSelected;
	SendableChooser chooser;
	final String keyZonePeg = "Key Zone Peg", keyZonePegCustom = "Key Zone Peg (Custom)",
			retrievalPeg = "Retrieval Zone Peg", retrievalPegCustom = "Retrieval Zone Peg (Custom)",
			pegAndBoiler = "Key Zone Peg and Boiler", pegAndBoilerCustom = "Key Zone Peg and Boiler (Custom)",
			pegAndHopper = "Key Zone Peg and Hopper", pegAndHopperCustom = "Key Zone Peg and Hopper (Custom)",
			highBoiler = "High Goal", highBoilerCustom = "High Goal (Custom)", pegMid = "Middle Peg", base = "Baseline",
			nothing = "Nothing";
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
		chooser.addObject(pegMid, pegMid);
		chooser.addObject(keyZonePeg, keyZonePeg);
		chooser.addObject(keyZonePegCustom, keyZonePegCustom);
		chooser.addObject(retrievalPeg, retrievalPeg);
		chooser.addObject(retrievalPegCustom, retrievalPegCustom);
		chooser.addObject(pegAndBoiler, pegAndBoiler);
		chooser.addObject(pegAndBoilerCustom, pegAndBoilerCustom);
		chooser.addObject(pegAndHopper, pegAndHopper);
		chooser.addObject(pegAndHopperCustom, pegAndHopperCustom);
		chooser.addObject(highBoiler, highBoiler);
		chooser.addObject(highBoilerCustom, highBoilerCustom);
		chooser.addDefault(nothing, nothing);
		
		SmartDashboard.putData("Auto Selection", chooser);
		
		// Makes the box for custom distance auto methods
		SmartDashboard.putNumber("Custom Distance from Corner (Inches)", 0.0);
		
		updateDashboard();
		
		Motors.motorInit();
		
		isRed = (m_ds.getAlliance() == Alliance.Red);
		
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
		boolean keyzoneStart = true;
		switch (autoSelected)
		{
			case base:
				// Drivetrain.moveToDistance(Dimensions.DIST_TO_BASELINE+60);//Goes
				// 5ft (60 in) past baseline)
				// TODO Move this into Auto.java, add in PID Control
				double distanceDelta = (Dimensions.DIST_TO_BASELINE + 60) - Motors.motorDriveLeft2.getEncPosition();
				if (distanceDelta > 0)
					Drivetrain.drive(1, 1);
				else
					Drivetrain.drive(0, 0);
				break;
			case pegMid:
				Auto.pegMiddle();
				break;
			case keyZonePeg:
				Auto.pegSide(keyzoneStart, isRed);
				break;
			case keyZonePegCustom:
				Auto.pegSide(SmartDashboard.getNumber("Custom Distance from Corner (Inches)"), keyzoneStart, isRed);
				break;
			case retrievalPeg:
				keyzoneStart = false;
				Auto.pegSide(keyzoneStart, isRed);
				break;
			case retrievalPegCustom:
				keyzoneStart = false;
				Auto.pegSide(SmartDashboard.getNumber("Custom Distance from Corner (Inches)"), keyzoneStart, isRed);
				break;
			case highBoiler:
				Auto.highBoiler(isRed);
				break;
			case highBoilerCustom:
				Auto.highBoiler(SmartDashboard.getNumber("Custom Distance from Corner (Inches)"), isRed);
				break;
			case pegAndBoiler:
				Auto.pegAndBoiler(isRed);
				break;
			case pegAndBoilerCustom:
				Auto.pegAndBoiler(SmartDashboard.getNumber("Custom Distance from Corner (Inches)"), isRed);
				break;
			case pegAndHopper:
				Auto.pegAndHopper(isRed);
				break;
			case pegAndHopperCustom:
				Auto.pegAndHopper(SmartDashboard.getNumber("Custom Distance from Corner (Inches)"), isRed);
				break;
			case nothing:
				break;
			default:
				Auto.pegMiddle();
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
		//TODO Second Control Mode - Left Stick for Forward/Backwards, Right stick for rotation
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
