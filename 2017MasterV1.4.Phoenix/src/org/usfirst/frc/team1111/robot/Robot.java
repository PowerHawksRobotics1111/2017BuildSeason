package org.usfirst.frc.team1111.robot;

import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import subsystems.Drivetrain;
import variables.Dimensions;
import variables.Joysticks;
import variables.Motors;
import variables.Sensors;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot
{
	
	String autoSelected;
	SendableChooser chooser;
	final String keyZonePeg = "Key Zone Peg", keyZonePegCustom = "Key Zone Peg (Custom)",
			retrievalPeg = "Retrieval Zone Peg", retrievalPegCustom = "Retrieval Zone Peg (Custom)",
			pegAndBoiler = "Key Zone Peg and Boiler", pegAndBoilerCustom = "Key Zone Peg and Boiler (Custom)",
			pegAndHopper = "Key Zone Peg and Hopper", pegAndHopperCustom = "Key Zone Peg and Hopper (Custom)",
			highBoiler = "High Goal", highBoilerCustom = "High Goal (Custom)", pegMid = "Middle Peg", base = "Baseline",
			nothing = "Nothing", pegMidPID = "Peg Mid PID", easyBoiler = "Easy Boiler Start";
	public static boolean isRed;
	public static boolean overrideDriverJoysticks = false;
	public static boolean gearReleased = false;
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit()
	{
		
		CameraServer.getInstance().startAutomaticCapture(0);
		CameraServer.getInstance().startAutomaticCapture("cam0", "/dev/video0");


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
		chooser.addObject(pegMidPID, pegMidPID);
		chooser.addObject(easyBoiler, easyBoiler);
		
		SmartDashboard.putData("Auto Selection", chooser);
		
		// Makes the box for custom distance auto methods
		//SmartDashboard.putNumber("Custom Distance from Corner (Inches)", 0.0);
		
		updateDashboard();
		Sensors.sensorsInit();
		Motors.motorInit();
		
		isRed =(m_ds.getAlliance() == Alliance.Red);
		
		Motors.motorDriveLeft1.setEncPosition(0);
		Motors.motorDriveRight1.setEncPosition(0);
		
	}
	
	/**
	 * Put in here stuff that needs to be on the dashboard Examples are
	 * literally anything the drive team would need to know Booleans, sensor
	 * statuses and values, mechanism statuses, etc.
	 */
	public void updateDashboard()
	{
		SmartDashboard.putBoolean("Gear Released?", gearReleased);
		
		// //SmartDashboard.putNumber("Shooter Speed",
		// Motors.motorShooter.getEncVelocity());
		// SmartDashboard.putBoolean("Shooter",
		// Motors.motorShooter.getEncVelocity() >= Motors.shooterSpeed);
		// SmartDashboard.putBoolean("Gear Stop Down",
		// Motors.gearHold1.getAngle() == Motors.gearStopdownAngle);
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
		
		Motors.motorDriveLeft1.setEncPosition(0);
		Motors.motorDriveLeft2.setEncPosition(0);
		Motors.motorDriveRight1.setEncPosition(0);
		Motors.motorDriveRight2.setEncPosition(0);
		
		Motors.gearHold1.setAngle(Motors.lGearStopdownAngle);
		Motors.gearHold2.setAngle(Motors.rGearStopdownAngle);
		
		Motors.fuelStop.setAngle(Motors.fuelCloseAngle);
		
//		Motors.lightRing1.changeControlMode(TalonControlMode.Voltage);
//		Motors.lightRing2.changeControlMode(TalonControlMode.Voltage);

//		Sensors.LEDRelay1.set(Relay.Value.kOn);
//		Sensors.LEDRelay2.set(Relay.Value.kOn);
		Sensors.navX.reset();
		Sensors.navX.zeroYaw();
	}
	
	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic()
	{
		//SmartDashboard.putNumber("NavX Yaw (SIDEPEG TESTING)", Sensors.navX.getYaw());
		boolean keyzoneStart = true;
		autoSelected = (String) chooser.getSelected();
		// System.out.println(autoSelected); TODO uncomment in final
		SmartDashboard.putNumber("Encoder Testing", Motors.motorDriveLeft1.getPosition());
		switch (autoSelected)
		{
		default:
			// Drivetrain.moveToDistance(Dimensions.DIST_TO_BASELINE+60);//Goes
			// 5ft (60 in) past baseline)
			// TODO Move this into Auto.java, add in PID Control
			double distanceDelta = ((Dimensions.DIST_TO_BASELINE + Dimensions.FIVE_FEET)*Sensors.INCHES_TO_DRIVE_ENCODER_LEFT)
					- ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
							+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0);
			if (distanceDelta > 0)
				Drivetrain.drive(Motors.AUTO_ALIGN_POWER, -Motors.AUTO_ALIGN_POWER);
			else
				Drivetrain.drive(0, 0);
			break;
		case base:
			// Drivetrain.moveToDistance(Dimensions.DIST_TO_BASELINE+60);//Goes
			// 5ft (60 in) past baseline)
			// TODO Move this into Auto.java, add in PID Control
			double distanceDeltaB = ((Dimensions.DIST_TO_BASELINE + Dimensions.FIVE_FEET)*Sensors.INCHES_TO_DRIVE_ENCODER_LEFT)
					- ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
							+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0);
			if (distanceDeltaB > 0)
				Drivetrain.drive(Motors.AUTO_ALIGN_POWER, -Motors.AUTO_ALIGN_POWER);
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
			case pegMidPID:
				Auto.pegMiddlePID();
				break;
			case easyBoiler:
				Auto.highBoilerEasy(isRed);
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
		
		Sensors.LEDRelay1.set(Relay.Value.kOn);
		
//		Motors.lightRing1.set(0);
//		Motors.lightRing2.set(0);
	}
	
	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic()
	{
		// TODO Second Control Mode - Left Stick for Forward/Backwards, Right
		// stick for rotation
		// if ( !overrideDriverJoysticks)
		Drivetrain.drive( -Joysticks.joyDrive.getRawAxis(3), Joysticks.joyDrive.getRawAxis(1));
		// if ((Math.abs(Joysticks.joyDrive.getRawAxis(0)) +
		// Math.abs(Joysticks.joyDrive.getRawAxis(2))) >= .3)
		// System.out.println("Mark! You can't go sideways...");
		Operator.operate();
		updateDashboard();
		//SmartDashboard.putNumber("Left Encoder", Motors.motorDriveLeft1.getEncPosition());
		//SmartDashboard.putNumber("Right Encoder", Motors.motorDriveRight1.getEncPosition());
		//SmartDashboard.putNumber("Encoder average", ((Math.abs(Motors.motorDriveLeft1.getEncPosition()) + Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0));
		
		//SmartDashboard.putNumber("NavX Yaw", Sensors.navX.getYaw());
	}
	
	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic()
	{
		
	}
	
	public void disabledInit()
	{
		Motors.motorDriveLeft1.enableBrakeMode(true);
		Motors.motorDriveLeft2.enableBrakeMode(true);
		Motors.motorDriveRight1.enableBrakeMode(true);
		Motors.motorDriveRight2.enableBrakeMode(true);
		Drivetrain.drive(0, 0);
		Motors.motorHang.set(0.0);
		Motors.motorIntake.set(0.0);
		Motors.motorLowShooter.set(0.0);
		Motors.motorTopShooter.set(0.0);
		Motors.pushPiston1.set(DoubleSolenoid.Value.kReverse);
		Motors.pushPiston2.set(DoubleSolenoid.Value.kReverse);
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
		Motors.motorLowShooter.set(0.0);
		Motors.motorTopShooter.set(0.0);
		Motors.pushPiston1.set(DoubleSolenoid.Value.kReverse);
		Motors.pushPiston2.set(DoubleSolenoid.Value.kReverse);
	}
	
}
