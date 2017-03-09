package variables;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Servo;

public class Motors
{
	
	public static CANTalon motorDriveRight1, motorDriveLeft1, motorDriveRight2, motorDriveLeft2;
	
	public static CANTalon motorAgitator, motorTopShooter, motorLowShooter, motorIntake, motorHang, lightRing1,
			lightRing2;
	
	public static Servo gearHold1, gearHold2, fuelStop;
	
	public static DoubleSolenoid pushPiston1, pushPiston2;
	
	// Final bot talon IDs (topshoot and lowshoot will change)
	final static int RIGHT_DRIVE_1 = 48, LEFT_DRIVE_1 = 54, RIGHT_DRIVE_2 = 43, LEFT_DRIVE_2 = 7, AGITATOR = 56,
			INTAKE = 45, HANG = 53, TOP_SHOOT = 61, LOW_SHOOT = 57, LIGHT_RING1 = -1, LIGHT_RING2 = -1;
	
	// Practice bot talon IDs
	// final static int RIGHT_DRIVE_1 = 59, LEFT_DRIVE_1 = 39, RIGHT_DRIVE_2 =
	// 38, LEFT_DRIVE_2 = 46, AGITATOR = 50,
	// INTAKE = 51, HANG = 62, TOP_SHOOT = 58, LOW_SHOOT = 37, LIGHT_RING1 = -1,
	// LIGHT_RING2 = -1;
	
	public static final double shooterVoltage = 12.5 * .73, agitationPower = 1, intakePower = -1, outtakePower = 1,
			hangPower = 1, hangStopCurrent = 85;
	
	public static final double fuelOpenAngle = 112.0;
	public static final double fuelCloseAngle = 180.0;
	
	public static final double lGearStopdownAngle = 17.6;// 10 p
															// //17.6
	public static final double lGearDropdownAngle = 0;// 0 p
														// //0
	public static final double lGearPrimeAngle = 15.5; // 8 //6p
	
	public static final double rGearStopdownAngle = 21.5;// 7.2p
															// //21.4
	public static final double rGearDropdownAngle = 35.2; // 14.5p
															// //35.2
	public static final double rGearPrimeAngle = 23; // 12 p //28
	
	public final static double AUTO_ALIGN_POWER = .25;
	
	public final static double GEAR_RATIO = 625.0 / 84.0;
	
	public static final double shooterSpeed = 0;// TODO Shooter Velocity
	
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
		
		motorAgitator = new CANTalon(AGITATOR);
		
		motorTopShooter = new CANTalon(TOP_SHOOT);
		motorLowShooter = new CANTalon(LOW_SHOOT);
		motorTopShooter.changeControlMode(TalonControlMode.Voltage);
		motorLowShooter.changeControlMode(TalonControlMode.Voltage);
		
		motorDriveLeft1.setInverted(true);
		motorDriveLeft2.setInverted(true);
		
		motorIntake = new CANTalon(INTAKE);
		motorHang = new CANTalon(HANG);
		motorHang.enableBrakeMode(true);
		
		gearHold1 = new Servo(0);
		gearHold2 = new Servo(1);
		
		fuelStop = new Servo(2);
		
		pushPiston1 = new  DoubleSolenoid(1,2);
		pushPiston2 = new DoubleSolenoid(3,4);
		
		lightRing1 = new CANTalon(LIGHT_RING1);
		lightRing2 = new CANTalon(LIGHT_RING2);
	}
}
