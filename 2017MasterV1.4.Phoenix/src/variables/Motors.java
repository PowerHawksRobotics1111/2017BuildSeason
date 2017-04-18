package variables;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Servo;

public class Motors
{
	
	public static CANTalon motorDriveRight1, motorDriveLeft1, motorDriveRight2, motorDriveLeft2;
	
	public static CANTalon motorAgitator, motorTopShooter, motorLowShooter, motorIntake, motorHang, motorHang2;//, lightRing1,
//			lightRing2;
	
	public static Servo gearHold1, gearHold2, fuelStop;
	
	public static DoubleSolenoid pushPiston1, pushPiston2;
	
	// Final bot talon IDs (topshoot and lowshoot will change)
	final static int RIGHT_DRIVE_1 = 48, LEFT_DRIVE_1 = 54, RIGHT_DRIVE_2 = 43, LEFT_DRIVE_2 = 7, AGITATOR = 0,
			INTAKE = 56, HANG = 53,HANG2 = 45, TOP_SHOOT = 61, LOW_SHOOT = 57, LIGHT_RING1 = -1, LIGHT_RING2 = -1;
	
	// Practice bot talon IDs
	// final static int RIGHT_DRIVE_1 = 59, LEFT_DRIVE_1 = 39, RIGHT_DRIVE_2 =
	// 38, LEFT_DRIVE_2 = 46, AGITATOR = 50,
	// INTAKE = 51, HANG = 62, TOP_SHOOT = 58, LOW_SHOOT = 37, LIGHT_RING1 = -1,
	// LIGHT_RING2 = -1;
	
	public static final double shooterVoltage = 12.5 * .73, agitationPower = 1, intakePower = -1, outtakePower = 1,
			hangPower = -1, hangStopCurrent = 80;
	
	//Note, RGear is port 1, which is the left side
	
	public static final double fuelOpenAngle = 120;//120
	public static final double fuelCloseAngle = 31;//61
	
	public static final double lGearStopdownAngle = 0;// 10 p
															// //17.6
	public static final double lGearDropdownAngle = 17.6;// 0 p
														// //0
	public static final double lGearPrimeAngle = 15; // 8 //6p
	
	public static final double rGearStopdownAngle = 161;// 7.2p
															// //21.4
	public static final double rGearDropdownAngle = 109; // 14.5p
															// //35.2
	public static final double rGearPrimeAngle = 131; // 12 p //28
	
	public final static double AUTO_ALIGN_POWER = .25;
	
	public final static double GEAR_RATIO = 625.0 / 84.0;
	
	public static final double shooterSpeed = 0;// TODO Shooter Velocity
	
	public static final double DRIVE_ACCELERATION = .05;//every 20 milliseconds
	public static final double DRIVE_DEACCELERATION = .02;
	public static final double DRIVE_SPEED_LIMIT = .5;

	
	public static void motorInit()
	{
		motorDriveRight1 = new CANTalon(RIGHT_DRIVE_1);
		motorDriveLeft1 = new CANTalon(LEFT_DRIVE_1);
		motorDriveRight2 = new CANTalon(RIGHT_DRIVE_2);
		motorDriveLeft2 = new CANTalon(LEFT_DRIVE_2);
		
		motorDriveRight1.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
		motorDriveRight2.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
		motorDriveLeft1.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
		motorDriveLeft2.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
		
		//motorAgitator = new CANTalon(AGITATOR);
		
		motorTopShooter = new CANTalon(TOP_SHOOT);
		motorLowShooter = new CANTalon(LOW_SHOOT);
		motorTopShooter.changeControlMode(TalonControlMode.Voltage);
		motorLowShooter.changeControlMode(TalonControlMode.Voltage);
		
		motorDriveLeft1.setInverted(true);
		motorDriveLeft2.setInverted(true);
		
		motorIntake = new CANTalon(INTAKE);
		motorHang = new CANTalon(HANG);
		motorHang2 = new CANTalon(HANG2);
		motorHang.enableBrakeMode(true);
		
		gearHold1 = new Servo(1);
		gearHold2 = new Servo(0);
		
		fuelStop = new Servo(2);
		
		pushPiston1 = new  DoubleSolenoid(0,1);
		pushPiston2 = new DoubleSolenoid(2,3);		
//		lightRing1 = new CANTalon(LIGHT_RING1);
//		lightRing2 = new CANTalon(LIGHT_RING2);
	}
}
