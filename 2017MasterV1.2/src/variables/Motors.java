package variables;

import com.ctre.CANTalon;

public class Motors {
	
	public static CANTalon motorDriveRight1, motorDriveLeft1, motorDriveRight2, motorDriveLeft2;
	
	public static CANTalon motorShooter, motorIntake;
	
	final static int RIGHT_DRIVE_1 = 54, LEFT_DRIVE_1 = 48, RIGHT_DRIVE_2 = 43, LEFT_DRIVE_2 = 7, SHOOTER = -1,
			INTAKE = -1;
	
	public static final double shooterPower = 0, intakePower = 0, outtakePower = 0;
	
	public final static double AUTO_ALIGN_POWER = .2;
	
	public final static double GEAR_RATIO = 625.0 / 84.0;
	// 625 / 84;This equals 7, not 7.44. Dividing integers returns an integer.
	// If you want a double, divide doubles...
	
	public static final double shooterSpeed = 0;// TODO Shooter Velocity
	
	public static void motorInit()
	{
		motorDriveRight1 = new CANTalon(RIGHT_DRIVE_1);
		motorDriveLeft1 = new CANTalon(LEFT_DRIVE_1);
		motorDriveRight2 = new CANTalon(RIGHT_DRIVE_2);
		motorDriveLeft2 = new CANTalon(LEFT_DRIVE_2);
		
		motorShooter = new CANTalon(SHOOTER);
		motorIntake = new CANTalon(INTAKE);
	}
}
