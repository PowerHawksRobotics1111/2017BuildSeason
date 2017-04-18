package org.usfirst.frc.team1111.robot;

import java.io.File;
import java.io.IOException;

import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import subsystems.BTMain;
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
	BTMain recorder;
	String autoSelected;
	String autoModeSelected;
	SendableChooser autoMode;
	SendableChooser chooser;
	final String keyZonePeg = "Key Zone Peg", retrievalPeg = "Retrieval Zone Peg",
			pegAndBoiler = "Key Zone Peg and Boiler", pegAndHopper = "Key Zone Peg and Hopper",
			highBoiler = "High Goal", pegMid = "Middle Peg", base = "Baseline", nothing = "Nothing",
			easyBoiler = "Easy Boiler Start", pegAndPrep = "Peg and Prepare", codeAuto = "Code Auto", fileAuto = "File Auto";;
	public static boolean isRed;
	public static boolean gearReleased = false;
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit()
	{
		
//		CameraServer.getInstance().startAutomaticCapture(0);
//		CameraServer.getInstance().startAutomaticCapture("cam0", "/dev/video0");
		
		chooser = new SendableChooser();
		autoMode = new SendableChooser();

		File btmain = new File("/home/lvuser/auto");
//		FileNameExtensionFilter fileNameExtension = new FileNameExtensionFilter("csv");
		for(File file: btmain.listFiles()){
//			if(fileNameExtension.accept(file)){
				chooser.addObject(file.getName(), file.getName());
//			}
		}
		
		
		chooser.addObject(base, base);
		chooser.addDefault(pegMid, pegMid);
//		chooser.addObject(keyZonePeg, keyZonePeg);
//		chooser.addObject(keyZonePegCustom, keyZonePegCustom);
//		chooser.addObject(retrievalPeg, retrievalPeg);
//		chooser.addObject(retrievalPegCustom, retrievalPegCustom);
//		chooser.addObject(pegAndBoiler, pegAndBoiler);
//		chooser.addObject(pegAndBoilerCustom, pegAndBoilerCustom);
//		chooser.addObject(pegAndHopper, pegAndHopper);
//		chooser.addObject(pegAndHopperCustom, pegAndHopperCustom);
//		chooser.addObject(highBoiler, highBoiler);
//		chooser.addObject(highBoilerCustom, highBoilerCustom);
		chooser.addObject(nothing, nothing);
//		chooser.addObject(pegMidPID, pegMidPID);
//		chooser.addObject(easyBoiler, easyBoiler);
		
//		autoMode.addDefault(codeAuto, codeAuto);
		autoMode.addObject(fileAuto, fileAuto);
		
		SmartDashboard.putData("Auto Mode", autoMode);
		SmartDashboard.putData("Auto Selection", chooser);
		
		Sensors.sensorsInit();
		Motors.motorInit();
		SmartDashboard.putString("Custom auto code name", "");

		isRed = (m_ds.getAlliance() == Alliance.Red);
		
		Motors.motorDriveLeft1.setEncPosition(0);
		Motors.motorDriveRight1.setEncPosition(0);
				updateDashboard();

	}
	
	/**
	 * Put in here stuff that needs to be on the dashboard Examples are
	 * literally anything the drive team would need to know Booleans, sensor
	 * statuses and values, mechanism statuses, etc.
	 */
	public void updateDashboard()
	{
		SmartDashboard.putBoolean("Gear Released?", gearReleased);
		
		SmartDashboard.putNumber("1 Servo", Motors.gearHold1.getAngle());
		SmartDashboard.putNumber("2 Servo", Motors.gearHold2.getAngle());
		
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
		autoModeSelected = (String) autoMode.getSelected();

		Motors.motorDriveLeft1.enableBrakeMode(true);
		Motors.motorDriveLeft2.enableBrakeMode(true);
		Motors.motorDriveRight1.enableBrakeMode(true);
		Motors.motorDriveRight2.enableBrakeMode(true);
		
//		Motors.motorDriveLeft1.changeControlMode(TalonControlMode.Voltage);
//		Motors.motorDriveLeft1.changeControlMode(TalonControlMode.Voltage);
//		Motors.motorDriveLeft1.changeControlMode(TalonControlMode.Voltage);
//		Motors.motorDriveLeft1.changeControlMode(TalonControlMode.Voltage);
		
		Motors.motorDriveLeft1.setEncPosition(0);
		Motors.motorDriveLeft2.setEncPosition(0);
		Motors.motorDriveRight1.setEncPosition(0);
		Motors.motorDriveRight2.setEncPosition(0);
		
		Motors.gearHold1.setAngle(Motors.lGearStopdownAngle);
		Motors.gearHold2.setAngle(Motors.rGearStopdownAngle);
		
		Motors.fuelStop.setAngle(Motors.fuelCloseAngle);
		
		// Motors.lightRing1.changeControlMode(TalonControlMode.Voltage);
		// Motors.lightRing2.changeControlMode(TalonControlMode.Voltage);
		
		// Sensors.LEDRelay1.set(Relay.Value.kOn);
		// Sensors.LEDRelay2.set(Relay.Value.kOn);
		Sensors.navX.reset();
		Sensors.navX.zeroYaw();
	}
	
	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic()
	{
		autoModeSelected = (String) autoMode.getSelected();
		autoSelected = (String) chooser.getSelected();
		boolean keyzoneStart = true;
		/*switch(autoModeSelected)
		{
		case fileAuto:
		{
			SmartDashboard.putNumber("NavX Yaw (SIDEPEG TESTING)", Sensors.navX.getYaw());
			autoSelected = (String) chooser.getSelected();
			if(recorder == null)
			{
				System.out.println("AH");
				recorder = new BTMain(autoSelected, true);
			}
			recorder.autonomous();
			
			System.out.println(autoSelected);// TODO uncomment in final
			break;
		}
		default:
		case codeAuto:
		{*/
			switch (autoSelected)
			{
				default:
////					recorder.autonomous();
//					// Drivetrain.moveToDistance(Dimensions.DIST_TO_BASELINE+60);//Goes
//					// 5ft (60 in) past baseline)
//					// TODO Move this into Auto.java, add in PID Control
//					double distanceDelta = ((Dimensions.DIST_TO_BASELINE + Dimensions.FIVE_FEET)*Sensors.INCHES_TO_DRIVE_ENCODER_LEFT)
//							- ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
//									+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0);
//					if (distanceDelta > 0)
//						Drivetrain.drive(Motors.AUTO_ALIGN_POWER, Motors.AUTO_ALIGN_POWER);
//					else
//						Drivetrain.drive(0, 0);
//					break;
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

				case retrievalPeg:
					keyzoneStart = false;
					Auto.pegSide(keyzoneStart, isRed);
					break;
				case highBoiler:
					Auto.highBoiler(isRed);
					break;

				case pegAndBoiler:
					Auto.pegAndBoiler(isRed);
					break;

				case pegAndHopper:
					Auto.pegAndHopper(isRed);
					break;

				case nothing:
					break;

				case easyBoiler:
					Auto.highBoilerEasy(isRed);
					break;
			}
			
//		}
//		}
	}
	
	@Override
	public void teleopInit()
	{
		Motors.motorDriveLeft1.enableBrakeMode(false);
		Motors.motorDriveLeft2.enableBrakeMode(false);
		Motors.motorDriveRight1.enableBrakeMode(false);
		Motors.motorDriveRight2.enableBrakeMode(false);
		
		Motors.motorDriveLeft1.changeControlMode(TalonControlMode.PercentVbus);
		Motors.motorDriveLeft2.changeControlMode(TalonControlMode.PercentVbus);
		Motors.motorDriveRight1.changeControlMode(TalonControlMode.PercentVbus);
		Motors.motorDriveRight2.changeControlMode(TalonControlMode.PercentVbus);
		
		Sensors.LEDRelay1.set(Relay.Value.kOn);
		
		// Motors.lightRing1.set(0);
		// Motors.lightRing2.set(0);
	}
	
	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic()
	{
//		if(recorder == null)
//			recorder = new BTMain(SmartDashboard.getString("Custom auto code name") + ".csv", false);
		// TODO Second Control Mode - Left Stick for Forward/Backwards, Right
		// stick for rotation
		// if ( !overrideDriverJoysticks)
		// if ((Math.abs(Joysticks.joyDrive.getRawAxis(0)) +
		// Math.abs(Joysticks.joyDrive.getRawAxis(2))) >= .3)
		// System.out.println("Mark! You can't go sideways...");
		Operator.operate();
//		Operator.drive();
		Drivetrain.drive( -Joysticks.joyDrive.getRawAxis(3), Joysticks.joyDrive.getRawAxis(1));
//		try 
//		{
//			recorder.operatorControl();
//		} catch (IOException e) 
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		updateDashboard();
		// SmartDashboard.putNumber("Left Encoder",
		// Motors.motorDriveLeft1.getEncPosition());
		// SmartDashboard.putNumber("Right Encoder",
		// Motors.motorDriveRight1.getEncPosition());
		// SmartDashboard.putNumber("Encoder average",
		// ((Math.abs(Motors.motorDriveLeft1.getEncPosition()) +
		// Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0));
		
		// SmartDashboard.putNumber("NavX Yaw", Sensors.navX.getYaw());
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
		recorder = null;
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
