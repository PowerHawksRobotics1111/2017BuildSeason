package org.usfirst.frc.team1111.robot;

import variables.Joysticks;
import variables.Motors;
import variables.Sensors;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot 
{

	CameraServer server;

	final String lowbarShoot = "LowShoot", lowbar = "lowbar", rockwall = "Rockwall", moat = "Moat", ramparts = "Ramp", roughTerrain = "Rough";

	String autoSelected;
	SendableChooser chooser;

	Double startTime = 0.0;
	
	double rumble = 0;
	
	public static Timer timer = new Timer();

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	public void robotInit()
	{
		server = CameraServer.getInstance();
		server.setQuality(50);//1080p camera. Capturing 540
		//the camera name (ex "cam0") can be found through the roborio web interface TODO TEMP CAMERA CODE
		server.startAutomaticCapture("cam2");
//		Sensors.Cameras.shootCam.

		chooser = new SendableChooser();
		
		//		chooser.addObject("Lowbar Shoot", lowbarShoot);
		chooser.addObject("Moat", moat);
		chooser.addDefault("Reach", "reach");
		chooser.addObject("Nothing", "nothing");
		chooser.addObject("Low Bar", lowbar);
		chooser.addObject(rockwall, rockwall);
		chooser.addObject("Ramparts", ramparts);
		chooser.addObject("Reach Drop", "reachThenDropArm");
//		chooser.addObject("Spy Box Shoot", "spyBoxShoot");
		chooser.addObject("Rough Terrain", roughTerrain);
		chooser.addObject("Rockwall, Keep Going", "Rockwall, Keep Going");

		SmartDashboard.putData("Auto Selection", chooser);

		Motors.motorInit();
//		Motors.lightingControlSpike.set(Relay.Value.kOn);
//		
		Sensors.initUltras();
//		Sensors.armEncoderInit();

		updateDashboard();
	}

	/**
	 * Yes. This is for initializing the Dashboard. Put stuff in here.
	 */
	public void updateDashboard()
	{
		SmartDashboard.putBoolean("Ball In", Sensors.intakeLimitSwitch.get() || Sensors.intakeLimitSwitch2.get());
		SmartDashboard.putBoolean("Inner Intake Running", Motors.motorInnerIntake.get() >= 0.2 || Motors.motorInnerIntake.get() <= -0.2);
		SmartDashboard.putBoolean("Outer Intake Running", Motors.motorOuterIntake.get() >= 0.2 || Motors.motorOuterIntake.get() <= -0.2);
		SmartDashboard.putBoolean("Shooting", Operate.shooting);
		SmartDashboard.putBoolean("BallStop Down", Motors.hardBallStop.getAngle() == 45.0);
//		SmartDashboard.putNumber("Arm revolutions", Sensors.getArmRev());
		SmartDashboard.putNumber("Arm", Motors.motorArm.getEncPosition());
		
		SmartDashboard.putNumber("Left Front Amperage", Motors.motorDriveFrontLeft.getOutputCurrent());
		SmartDashboard.putNumber("Left Back Amperage", Motors.motorDriveBackLeft.getOutputCurrent());
		SmartDashboard.putNumber("Right Front Amperage", Motors.motorDriveFrontRight.getOutputCurrent());
		SmartDashboard.putNumber("Right Back Amperage", Motors.motorDriveBackRight.getOutputCurrent());
		
		SmartDashboard.putNumber("Right Ultra", Sensors.rightUltra.getRangeInches());
		SmartDashboard.putNumber("Left Ultra", Sensors.leftUltra.getRangeInches());
		
		SmartDashboard.putNumber("NavX Yaw", Sensors.navX.getYaw());
		
		if(rumble == 0)
			rumble = 100;
		else
			rumble-=2;
		Joysticks.joyDrive.setRumble(edu.wpi.first.wpilibj.Joystick.RumbleType.kLeftRumble, (float) (rumble/100.0));
		Joysticks.joyDrive.setRumble(edu.wpi.first.wpilibj.Joystick.RumbleType.kRightRumble, (float) (rumble/100.0));

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
	public void autonomousInit()
	{
		autoSelected = (String) chooser.getSelected();
	}

	/**
	 * This function is called periodically during autonomous
	 */
	public void autonomousPeriodic()
	{
		
		Motors.motorDriveBackLeft.set(-.25);
		Motors.motorDriveBackRight.set(.25);
		
		SmartDashboard.putNumber("Left Encoder", Motors.motorDriveBackLeft.getEncPosition());
		SmartDashboard.putNumber("Right Encoder", Motors.motorDriveBackRight.getEncPosition());
		/*switch (autoSelected) 
		{
		case "reachThenDropArm":
			Auto.reachThenDropArm();
			break;
		default:
		case "reach":
			Auto.reach();
			break;
		case "nothing":
			break;
		case moat:
			Auto.moat();
			break;
		case ramparts:
			Auto.ramparts();
			break;
		case roughTerrain:
		case rockwall:
			Auto.roughTerrainRockwall();
			break;
		case lowbar:
			Auto.lowBar();
			break;
		case "Rockwall, Keep Going":
			Auto.rockwallKeepGoing();
			break;
//		case "spyBoxShoot":
//			Auto.spyBoxShoot();
//			break;
		}
		
//		Motors.lightingControlSpike.set(Relay.Value.kOn);
		*/
		updateDashboard();
	}
	
	public void teleopInit()
	{
		Motors.motorArm.enableBrakeMode(true);
		Auto.retrievedImage = false;
	}

	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic()
	{
		if(!Motors.motorArm.getBrakeEnableDuringNeutral())
			Motors.motorArm.enableBrakeMode(true);
		drive();
		//Operate.operate();
		Auto.autoAlign();

//		Motors.lightingControlSpike.set(Relay.Value.kOn);

		//		cameraControl();

		updateDashboard();
	}

	void drive()
	{
		double right = variables.Joysticks.joyDrive.getRawAxis(3);
		double left = variables.Joysticks.joyDrive.getRawAxis(1);

		Motors.motorDriveFrontLeft.set(left);
		Motors.motorDriveBackLeft.set(Motors.BACK_WHEEL_DRIVE_RATIO * left);

		Motors.motorDriveFrontRight.set(-right);
		Motors.motorDriveBackRight.set(Motors.BACK_WHEEL_DRIVE_RATIO * -right);
	}

	public void disabledPeriodic()
	{
		//		Motors.brake.setAngle(Motors.BRAKE_ANGLE); TODO implement?!?!?
		Auto.Movement.stopDriveMotors();
		Motors.motorInnerIntake.set(0.0);
		Motors.motorOuterIntake.set(0.0);
		Motors.motorShooter.set(0.0);
		Motors.motorArm.set(0.0);
		Operate.disable();
		updateDashboard();
	}

	//	static void cameraControl()
	//	{
	//		if(Operator.shooting)
	//		{
	//			Sensors.Cameras.driveCam.writeResolution(edu.wpi.first.wpilibj.vision.AxisCamera.Resolution.k160x120);
	//			Sensors.Cameras.shootCam.startCapture();
	//		}else
	//		{
	//			Sensors.Cameras.driveCam.writeResolution(edu.wpi.first.wpilibj.vision.AxisCamera.Resolution.k640x480);
	//			Sensors.Cameras.shootCam.stopCapture();
	//		}
	//	}

}