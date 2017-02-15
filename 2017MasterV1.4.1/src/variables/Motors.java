package variables;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.Servo;

public class Motors
{
	
	public static CANTalon motorDriveRight1, motorDriveLeft1, motorDriveRight2, motorDriveLeft2;
	
	public static CANTalon motorAgitator, motorShooter, motorIntake, motorHang;
	
	public static Servo gearHold1, gearHold2, fuelStop;
	
	final static int RIGHT_DRIVE_1 = 59, LEFT_DRIVE_1 = 39, RIGHT_DRIVE_2 = 38, LEFT_DRIVE_2 = 46, AGITATOR = -1,
			SHOOTER = -1, INTAKE = 51, HANG = 58;
	
	public static final double shooterVoltage = 12.5 * .72, agitationPower = 0, intakePower = 1, outtakePower = 0,
			hangPower = -1, hangStopCurrent = 80;
	
	// public static final double hangStopCurrent;
	public static final double fuelOpenAngle = 90.0;
	public static final double fuelCloseAngle = 0.0;
	
	public static final double lGearStopdownAngle = 7.5;
	public static final double lGearDropdownAngle = 14.0;
	
	public static final double rGearStopdownAngle = 8.5;
	public static final double rGearDropdownAngle = 14.0;
	
	public final static double AUTO_ALIGN_POWER = .2;
	
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
		
		motorShooter = new CANTalon(SHOOTER);
		motorShooter.changeControlMode(TalonControlMode.Voltage);
		
		motorIntake = new CANTalon(INTAKE);
		motorHang = new CANTalon(HANG);
		
		gearHold1 = new Servo(0);// 1);
		gearHold2 = new Servo(1);// 2);Ports are 0 indexed.
		
		fuelStop = new Servo(2);
	}
}
