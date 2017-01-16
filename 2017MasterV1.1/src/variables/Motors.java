package variables;

import com.ctre.CANTalon;

public class Motors 
{
	
	public static CANTalon motorDriveFrontRight, motorDriveFrontLeft, motorDriveBackRight, motorDriveBackLeft;
	
	final static int FRONT_RIGHT_DRIVE = 0, FRONT_LEFT_DRIVE = 0, BACK_RIGHT_DRIVE = 0, BACK_LEFT_DRIVE = 0;
	
	public final static double FULL_POWER = 1.0, THREE_QUARTERS_POWER = .75, HALF_POWER = .5, QUARTER_POWER = .25, NO_POWER = 0.0;
	public final static double REVERSE_FULL_POWER = -1.0, REVERSE_THREE_QUARTERS_POWER = -.75, REVERSE_HALF_POWER = -.5, REVERSE_QUARTER_POWER = -.25;
	public final static double AUTO_ALIGN_POWER = .2;
	
	public final static double GEAR_RATIO = 625/84;
	
	public static void motorInit()
	{
		motorDriveFrontRight = new CANTalon(FRONT_RIGHT_DRIVE);
		motorDriveFrontLeft = new CANTalon(FRONT_LEFT_DRIVE);
		motorDriveBackRight = new CANTalon(BACK_RIGHT_DRIVE);
		motorDriveBackLeft = new CANTalon(BACK_LEFT_DRIVE);
	}
}
